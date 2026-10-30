package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.acl.ReaderCatalogGateway;
import cn.intellijboy.demo.librarylending.domain.event.BookLoanedEvent;
import cn.intellijboy.demo.librarylending.domain.event.BookReturnedEvent;
import cn.intellijboy.demo.librarylending.domain.event.DomainEventPublisher;
import cn.intellijboy.demo.librarylending.domain.event.LoanOverdueEvent;
import cn.intellijboy.demo.librarylending.domain.event.ReservationCreatedEvent;
import cn.intellijboy.demo.librarylending.domain.exception.BookCopyUnavailableException;
import cn.intellijboy.demo.librarylending.domain.exception.BookNotFoundException;
import cn.intellijboy.demo.librarylending.domain.exception.DuplicateLoanException;
import cn.intellijboy.demo.librarylending.domain.exception.DuplicateReservationException;
import cn.intellijboy.demo.librarylending.domain.exception.LoanNotFoundException;
import cn.intellijboy.demo.librarylending.domain.exception.ReaderNotFoundException;
import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopy;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.OverdueFee;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReservationStatus;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.domain.repository.LoanRepository;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.domain.service.BorrowingPolicy;
import cn.intellijboy.demo.librarylending.domain.service.LoanFactory;
import cn.intellijboy.demo.librarylending.domain.service.ReservationPromotionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 借阅应用服务：借阅、归还与预约创建，在事务边界内编排 Book/Loan/Reservation 聚合。
 */
@Service
public class LoanApplicationService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final ReaderCatalogGateway readerGateway;
    private final BorrowingPolicy policy;
    private final LoanFactory loanFactory;
    private final ReservationPromotionService reservationPromotionService;
    private final DomainEventPublisher publisher;

    public LoanApplicationService(BookRepository bookRepository, LoanRepository loanRepository,
                                  ReservationRepository reservationRepository, ReaderCatalogGateway readerGateway,
                                  BorrowingPolicy policy, LoanFactory loanFactory,
                                  ReservationPromotionService reservationPromotionService,
                                  DomainEventPublisher publisher) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.readerGateway = readerGateway;
        this.policy = policy;
        this.loanFactory = loanFactory;
        this.reservationPromotionService = reservationPromotionService;
        this.publisher = publisher;
    }

    /** 借阅：防腐层校验读者、可借副本判断、重复借阅不变式、副本借出与借阅记录创建 */
    @Transactional
    public Loan borrow(ReaderId readerId, BookId bookId) {
        requireReader(readerId);
        Book book = requireBook(bookId);
        if (!policy.hasAvailableCopy(book)) {
            throw new BookCopyUnavailableException("该图书所有副本均被借出，可预约");
        }
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        if (loanRepository.findActiveByReaderAndCopy(readerId, copy.getId()).isPresent()) {
            throw new DuplicateLoanException("读者已借阅该馆藏副本，不可重复借阅");
        }
        copy.borrow();
        Loan loan = loanFactory.createLoan(null, readerId, bookId, copy.getId(),
            LocalDate.now(), policy.loanPeriod());
        loan = loanRepository.save(loan);
        bookRepository.save(book);
        publisher.publish(new BookLoanedEvent(readerId, copy.getId()));
        return loan;
    }

    /** 归还：计算逾期费用、归还副本并触发预约转正 */
    @Transactional
    public ReturnResult returnCopy(Long loanId, LocalDate today) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new LoanNotFoundException("借阅记录不存在: id=" + loanId));
        BookId bookId = loan.getBookId();
        BookCopyId copyId = loan.getBookCopyId();

        OverdueFee fee = policy.overdueFee();
        BigDecimal overdueFeeAmount = fee.amountFor(loan.overdueDays(today));
        boolean wasOverdue = loan.isOverdue(today);
        loan.returnBook(today);
        loanRepository.save(loan);

        Book book = requireBook(bookId);
        BookCopy copy = book.findCopy(copyId)
            .orElseThrow(() -> new BookCopyUnavailableException("馆藏副本不存在"));
        Optional<Reservation> promoted = reservationPromotionService.promoteNextFor(bookId);
        if (promoted.isPresent()) {
            copy.returnToReserved();
        } else {
            copy.returnCopy();
        }
        bookRepository.save(book);

        publisher.publish(new BookReturnedEvent(loan.getReaderId(), copyId));
        if (wasOverdue) {
            publisher.publish(new LoanOverdueEvent(loan.getReaderId(), copyId));
        }
        return new ReturnResult(overdueFeeAmount, promoted.map(Reservation::getReaderId).orElse(null));
    }

    /** 预约：全部副本被借出时创建预约（按创建顺序排队，唯一性校验） */
    @Transactional
    public Reservation reserve(ReaderId readerId, BookId bookId) {
        requireReader(readerId);
        Book book = requireBook(bookId);
        if (policy.hasAvailableCopy(book)) {
            throw new BookCopyUnavailableException("该图书仍有可借副本，可直接借阅");
        }
        if (reservationRepository.existsWaitingByReaderAndBook(readerId, bookId)) {
            throw new DuplicateReservationException("读者已预约该图书，不可重复预约");
        }
        Reservation reservation = new Reservation(null, readerId, bookId,
            ReservationStatus.WAITING, LocalDateTime.now());
        reservation = reservationRepository.save(reservation);
        publisher.publish(new ReservationCreatedEvent(readerId, bookId));
        return reservation;
    }

    private ReaderId requireReader(ReaderId readerId) {
        readerGateway.findReader(readerId)
            .orElseThrow(() -> new ReaderNotFoundException("读者不存在: id=" + readerId.value()));
        return readerId;
    }

    private Book requireBook(BookId bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("图书不存在: id=" + bookId.value()));
    }
}

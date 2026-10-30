package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.acl.ReaderCatalogGateway;
import cn.intellijboy.demo.librarylending.domain.acl.ReaderRef;
import cn.intellijboy.demo.librarylending.domain.event.BookLoanedEvent;
import cn.intellijboy.demo.librarylending.domain.event.BookReturnedEvent;
import cn.intellijboy.demo.librarylending.domain.event.DomainEvent;
import cn.intellijboy.demo.librarylending.domain.event.DomainEventPublisher;
import cn.intellijboy.demo.librarylending.domain.event.LoanOverdueEvent;
import cn.intellijboy.demo.librarylending.domain.event.ReservationCreatedEvent;
import cn.intellijboy.demo.librarylending.domain.event.ReservationPromotedEvent;
import cn.intellijboy.demo.librarylending.domain.exception.BookCopyUnavailableException;
import cn.intellijboy.demo.librarylending.domain.exception.BookNotFoundException;
import cn.intellijboy.demo.librarylending.domain.exception.DuplicateLoanException;
import cn.intellijboy.demo.librarylending.domain.exception.DuplicateReservationException;
import cn.intellijboy.demo.librarylending.domain.exception.ReaderNotFoundException;
import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopy;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyStatus;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReservationStatus;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.domain.repository.LoanRepository;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.domain.service.BorrowingPolicy;
import cn.intellijboy.demo.librarylending.domain.service.BookFactory;
import cn.intellijboy.demo.librarylending.domain.service.LoanFactory;
import cn.intellijboy.demo.librarylending.domain.service.ReservationPromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoanApplicationService 应用层测试：借阅/归还/预约/转正/逾期与防腐层校验（内存 stub）。
 */
@DisplayName("借阅应用服务测试")
class LoanApplicationServiceTest {

    private final ReaderId reader1 = new ReaderId(1L);
    private final ReaderId unknownReader = new ReaderId(999L);
    private final BookId bookId = new BookId(1L);

    private StubBookRepository bookRepository;
    private StubLoanRepository loanRepository;
    private StubReservationRepository reservationRepository;
    private StubReaderGateway readerGateway;
    private RecordingPublisher publisher;
    private LoanApplicationService service;

    @BeforeEach
    void setUp() {
        bookRepository = new StubBookRepository();
        loanRepository = new StubLoanRepository();
        reservationRepository = new StubReservationRepository();
        readerGateway = new StubReaderGateway();
        publisher = new RecordingPublisher();
        ReservationPromotionService promotionService =
            new ReservationPromotionService(reservationRepository, publisher);
        service = new LoanApplicationService(bookRepository, loanRepository, reservationRepository,
            readerGateway, new BorrowingPolicy(), new LoanFactory(), promotionService, publisher);
    }

    private Book bookWithCopies(int copies) {
        Book book = new BookFactory().createBook(bookId, "DDD 实战", "作者", copies);
        bookRepository.save(book);
        return book;
    }

    @Test
    @DisplayName("副本可借时借阅成功并发布借出事件")
    void borrowSucceedsWhenCopyAvailable() {
        Book book = bookWithCopies(1);
        Loan loan = service.borrow(reader1, bookId);
        assertNotNull(loan.getId());
        assertEquals(reader1, loan.getReaderId());
        assertEquals(BookCopyStatus.BORROWED, book.getCopies().get(0).getStatus());
        assertTrue(publisher.published().stream().anyMatch(e -> e instanceof BookLoanedEvent));
    }

    @Test
    @DisplayName("借阅时拒绝不存在的读者")
    void borrowRejectsUnknownReader() {
        bookWithCopies(1);
        assertThrows(ReaderNotFoundException.class, () -> service.borrow(unknownReader, bookId));
    }

    @Test
    @DisplayName("借阅时拒绝不存在的图书")
    void borrowRejectsUnknownBook() {
        assertThrows(BookNotFoundException.class, () -> service.borrow(reader1, new BookId(404L)));
    }

    @Test
    @DisplayName("全部副本已借出时拒绝再次借阅")
    void borrowRejectsWhenAllCopiesBorrowed() {
        bookWithCopies(1);
        service.borrow(reader1, bookId);
        assertThrows(BookCopyUnavailableException.class, () -> service.borrow(new ReaderId(2L), bookId));
    }

    @Test
    @DisplayName("同一读者重复借阅同一副本时拒绝借阅")
    void borrowRejectsDuplicateForSameReaderAndCopy() {
        // 2 副本：读者借出副本 101 后，将其状态重置为可借以模拟同副本再次借阅触发重复不变式
        Book book = bookWithCopies(2);
        service.borrow(reader1, bookId);
        BookCopy first = book.findCopy(new BookCopyId(101L)).orElseThrow();
        first.returnCopy();
        bookRepository.save(book);

        assertThrows(DuplicateLoanException.class, () -> service.borrow(reader1, bookId));
    }

    @Test
    @DisplayName("全部副本已借出时预约进入等待队列")
    void reserveCreatesWaitingReservationWhenAllBorrowed() {
        bookWithCopies(1);
        service.borrow(reader1, bookId);
        Reservation reservation = service.reserve(new ReaderId(2L), bookId);
        assertEquals(ReservationStatus.WAITING, reservation.getStatus());
        assertTrue(publisher.published().stream().anyMatch(e -> e instanceof ReservationCreatedEvent));
    }

    @Test
    @DisplayName("存在可借副本时拒绝预约")
    void reserveRejectedWhenAvailableCopyExists() {
        bookWithCopies(1);
        assertThrows(BookCopyUnavailableException.class, () -> service.reserve(reader1, bookId));
    }

    @Test
    @DisplayName("同一读者重复预约同一图书时拒绝预约")
    void reserveRejectedForDuplicate() {
        bookWithCopies(1);
        service.borrow(reader1, bookId);
        ReaderId reader2 = new ReaderId(2L);
        service.reserve(reader2, bookId);
        assertThrows(DuplicateReservationException.class, () -> service.reserve(reader2, bookId));
    }

    @Test
    @DisplayName("归还时无等待预约则副本恢复可借且免收滞纳金")
    void returnCopyRestoresAvailableWithoutReservation() {
        bookWithCopies(1);
        Loan loan = service.borrow(reader1, bookId);
        ReturnResult result = service.returnCopy(loan.getId(), LocalDate.of(2026, 11, 1));
        assertEquals(java.math.BigDecimal.ZERO, result.overdueFee());
        assertEquals(null, result.promotedReaderId());
        assertEquals(BookCopyStatus.AVAILABLE, bookRepository.findById(bookId).orElseThrow().firstAvailableCopy().orElseThrow().getStatus());
        assertTrue(publisher.published().stream().anyMatch(e -> e instanceof BookReturnedEvent));
    }

    @Test
    @DisplayName("归还逾期借阅时收取滞纳金并发布逾期事件")
    void overdueLoanChargesFeeAndPublishesEvent() {
        // 固定借出日期以制造逾期：直接构造逾期 Loan（副本 id 对应 BookFactory 生成的 101）
        Loan loan = new Loan(1L, reader1, bookId, new BookCopyId(101L),
            LocalDate.now().minusDays(30), new cn.intellijboy.demo.librarylending.domain.model.LoanPeriod(14));
        loanRepository.save(loan);
        Book book = bookWithCopies(1);
        book.firstAvailableCopy().orElseThrow().borrow();
        bookRepository.save(book);

        ReturnResult result = service.returnCopy(loan.getId(), LocalDate.now());
        assertTrue(result.overdueFee().signum() > 0);
        assertTrue(publisher.published().stream().anyMatch(e -> e instanceof LoanOverdueEvent));
    }

    @Test
    @DisplayName("归还时通知最早等待的预约读者并转正预约")
    void returnCopyPromotesEarliestWaitingReservation() {
        bookWithCopies(1);
        Loan loan = service.borrow(reader1, bookId);
        service.reserve(new ReaderId(2L), bookId);
        service.reserve(new ReaderId(3L), bookId);

        ReturnResult result = service.returnCopy(loan.getId(), LocalDate.now());
        assertEquals(new ReaderId(2L), result.promotedReaderId());
        List<Reservation> reservations = reservationRepository.findByBookOrderedByCreatedAt(bookId);
        assertEquals(ReservationStatus.NOTIFIED, reservations.get(0).getStatus());
        assertEquals(ReservationStatus.WAITING, reservations.get(1).getStatus());
        assertEquals(BookCopyStatus.RESERVED, bookRepository.findById(bookId).orElseThrow().getCopies().get(0).getStatus());
        assertTrue(publisher.published().stream().anyMatch(e -> e instanceof ReservationPromotedEvent));
    }

    // ============ stubs ============

    static class StubBookRepository implements BookRepository {
        final Map<BookId, Book> store = new HashMap<>();

        @Override
        public Book save(Book book) {
            store.put(book.getId(), book);
            return book;
        }

        @Override
        public Optional<Book> findById(BookId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    static class StubLoanRepository implements LoanRepository {
        final Map<Long, Loan> store = new HashMap<>();
        final AtomicLong idSeq = new AtomicLong(1);

        @Override
        public Loan save(Loan loan) {
            if (loan.getId() == null) {
                loan.setId(idSeq.getAndIncrement());
            }
            store.put(loan.getId(), loan);
            return loan;
        }

        @Override
        public Optional<Loan> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Optional<Loan> findActiveByReaderAndCopy(ReaderId readerId, BookCopyId bookCopyId) {
            return store.values().stream()
                .filter(l -> l.getReaderId().equals(readerId) && l.getBookCopyId().equals(bookCopyId) && !l.isReturned())
                .findFirst();
        }

        @Override
        public List<Loan> findByReader(ReaderId readerId) {
            return store.values().stream().filter(l -> l.getReaderId().equals(readerId)).collect(Collectors.toList());
        }

        @Override
        public List<Loan> findActiveLoans() {
            return store.values().stream().filter(l -> !l.isReturned()).collect(Collectors.toList());
        }

        @Override
        public List<Loan> findOverdueLoans(LocalDate today) {
            return store.values().stream().filter(l -> l.isOverdue(today)).collect(Collectors.toList());
        }
    }

    static class StubReservationRepository implements ReservationRepository {
        final Map<Long, Reservation> store = new HashMap<>();
        final AtomicLong idSeq = new AtomicLong(1);

        @Override
        public Reservation save(Reservation reservation) {
            if (reservation.getId() == null) {
                reservation.setId(idSeq.getAndIncrement());
            }
            store.put(reservation.getId(), reservation);
            return reservation;
        }

        @Override
        public Optional<Reservation> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Reservation> findByBookOrderedByCreatedAt(BookId bookId) {
            return store.values().stream()
                .filter(r -> r.getBookId().equals(bookId))
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .collect(Collectors.toList());
        }

        @Override
        public List<Reservation> findWaitingByBookOrderedByCreatedAt(BookId bookId) {
            return findByBookOrderedByCreatedAt(bookId).stream()
                .filter(Reservation::isWaiting)
                .collect(Collectors.toList());
        }

        @Override
        public boolean existsWaitingByReaderAndBook(ReaderId readerId, BookId bookId) {
            return store.values().stream().anyMatch(
                r -> r.getReaderId().equals(readerId) && r.getBookId().equals(bookId) && r.isWaiting());
        }
    }

    static class StubReaderGateway implements ReaderCatalogGateway {
        @Override
        public Optional<ReaderRef> findReader(ReaderId readerId) {
            if (readerId.value() == 999L) {
                return Optional.empty();
            }
            return Optional.of(new ReaderRef(readerId, "读者" + readerId.value()));
        }
    }

    static class RecordingPublisher implements DomainEventPublisher {
        final List<DomainEvent> events = new ArrayList<>();

        @Override
        public void publish(DomainEvent event) {
            events.add(event);
        }

        List<DomainEvent> published() {
            return events;
        }
    }
}

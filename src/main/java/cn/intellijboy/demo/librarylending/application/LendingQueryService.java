package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.domain.repository.LoanRepository;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.domain.service.BorrowingPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 借阅查询服务（CQRS 读模型）：读路径直接以仓储投影返回，不经过写模型聚合。
 */
@Service
public class LendingQueryService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final BorrowingPolicy policy;

    public LendingQueryService(BookRepository bookRepository, LoanRepository loanRepository,
                               ReservationRepository reservationRepository, BorrowingPolicy policy) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.policy = policy;
    }

    /** 按读者查询借阅记录 */
    @Transactional(readOnly = true)
    public List<Loan> loansOfReader(ReaderId readerId) {
        return loanRepository.findByReader(readerId);
    }

    /** 按图书查询馆藏与预约队列 */
    @Transactional(readOnly = true)
    public Optional<BookStatusView> bookStatus(BookId bookId) {
        return bookRepository.findById(bookId)
            .map(book -> BookStatusView.of(
                book.getId(), book.getTitle(), book.availableCount(), book.borrowedCount(),
                reservationRepository.findByBookOrderedByCreatedAt(bookId)));
    }

    /** 逾期借阅列表（含按策略计算的逾期费用） */
    @Transactional(readOnly = true)
    public List<Loan> overdueLoans(LocalDate today) {
        return loanRepository.findOverdueLoans(today);
    }

    /** 计算某借阅的逾期费用 */
    public java.math.BigDecimal overdueFeeOf(Loan loan, LocalDate today) {
        return policy.overdueFee().amountFor(loan.overdueDays(today));
    }

    /** 图书是否存在可借副本 */
    @Transactional(readOnly = true)
    public boolean hasAvailableCopy(BookId bookId) {
        return bookRepository.findById(bookId).map(policy::hasAvailableCopy).orElse(false);
    }

    /** 供测试/展示用的图书实体访问（读路径） */
    @Transactional(readOnly = true)
    public Optional<Book> book(BookId bookId) {
        return bookRepository.findById(bookId);
    }
}

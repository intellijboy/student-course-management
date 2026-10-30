package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.LoanPeriod;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReservationStatus;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.domain.repository.LoanRepository;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.domain.service.BorrowingPolicy;
import cn.intellijboy.demo.librarylending.domain.service.BookFactory;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LendingQueryService 读模型测试：按读者借阅、图书状态与逾期列表。
 */
@DisplayName("借阅查询服务测试")
class LendingQueryServiceTest {

    private final ReaderId reader1 = new ReaderId(1L);
    private final BookId bookId = new BookId(1L);

    private StubBookRepository bookRepository;
    private StubLoanRepository loanRepository;
    private StubReservationRepository reservationRepository;
    private LendingQueryService service;

    @BeforeEach
    void setUp() {
        bookRepository = new StubBookRepository();
        loanRepository = new StubLoanRepository();
        reservationRepository = new StubReservationRepository();
        service = new LendingQueryService(bookRepository, loanRepository, reservationRepository,
            new BorrowingPolicy());
    }

    @Test
    @DisplayName("查询读者借阅时只返回该读者的借阅记录")
    void loansOfReaderReturnsReaderLoans() {
        Book book = new BookFactory().createBook(bookId, "DDD 实战", "作者", 2);
        bookRepository.save(book);
        loanRepository.save(new Loan(1L, reader1, bookId, new BookCopyId(101L),
            LocalDate.now(), new LoanPeriod(14)));
        loanRepository.save(new Loan(2L, new ReaderId(2L), bookId, new BookCopyId(102L),
            LocalDate.now(), new LoanPeriod(14)));

        List<Loan> loans = service.loansOfReader(reader1);
        assertEquals(1, loans.size());
        assertEquals(new BookCopyId(101L), loans.get(0).getBookCopyId());
    }

    @Test
    @DisplayName("查询图书状态时展示可借与已借数量及预约队列")
    void bookStatusShowsCountsAndReservationQueue() {
        Book book = new BookFactory().createBook(bookId, "DDD 实战", "作者", 2);
        book.getCopies().get(0).borrow();
        bookRepository.save(book);
        reservationRepository.save(new Reservation(1L, new ReaderId(2L), bookId,
            ReservationStatus.WAITING, LocalDateTime.of(2026, 10, 1, 9, 0)));
        reservationRepository.save(new Reservation(2L, new ReaderId(3L), bookId,
            ReservationStatus.WAITING, LocalDateTime.of(2026, 10, 2, 9, 0)));

        BookStatusView view = service.bookStatus(bookId).orElseThrow();
        assertEquals(1, view.availableCount());
        assertEquals(1, view.borrowedCount());
        assertEquals(2, view.reservationQueue().size());
        assertEquals("DDD 实战", view.title());
    }

    @Test
    @DisplayName("查询逾期列表时只返回已逾期的借阅记录")
    void overdueLoansReturnsOnlyOverdue() {
        loanRepository.save(new Loan(1L, reader1, bookId, new BookCopyId(101L),
            LocalDate.now().minusDays(30), new LoanPeriod(14)));
        loanRepository.save(new Loan(2L, reader1, bookId, new BookCopyId(102L),
            LocalDate.now(), new LoanPeriod(14)));

        List<Loan> overdue = service.overdueLoans(LocalDate.now());
        assertEquals(1, overdue.size());
        assertEquals(1L, overdue.get(0).getId());
        assertTrue(service.overdueFeeOf(overdue.get(0), LocalDate.now()).signum() > 0);
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
}

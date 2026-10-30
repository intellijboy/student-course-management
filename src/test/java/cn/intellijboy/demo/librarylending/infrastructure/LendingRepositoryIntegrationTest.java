package cn.intellijboy.demo.librarylending.infrastructure;

import cn.intellijboy.demo.librarylending.domain.event.BookLoanedEvent;
import cn.intellijboy.demo.librarylending.domain.event.DomainEvent;
import cn.intellijboy.demo.librarylending.domain.event.DomainEventPublisher;
import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyStatus;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.LoanPeriod;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReservationStatus;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.domain.repository.LoanRepository;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.domain.service.BookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 基础设施层集成测试：JPA 仓储读写与领域事件发布。
 */
@SpringBootTest
@SpringJUnitConfig(classes = LendingRepositoryIntegrationTest.EventConfig.class)
class LendingRepositoryIntegrationTest {

    @org.springframework.beans.factory.annotation.Autowired
    private BookRepository bookRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private LoanRepository loanRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private ReservationRepository reservationRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private DomainEventPublisher publisher;

    @Test
    void shouldSaveAndFindBookWithCopies() {
        Book saved = new BookFactory().createBook(new BookId(1L), "DDD 实战", "作者", 2);
        bookRepository.save(saved);

        Optional<Book> found = bookRepository.findById(new BookId(1L));
        assertTrue(found.isPresent());
        assertEquals("DDD 实战", found.get().getTitle());
        assertEquals(2, found.get().totalCount());
        assertEquals(BookCopyStatus.AVAILABLE, found.get().getCopies().get(0).getStatus());
    }

    @Test
    void shouldSaveAndFindLoanAndActiveByReaderAndCopy() {
        Loan saved = loanRepository.save(new Loan(null, new ReaderId(1L), new BookId(1L),
            new BookCopyId(101L), LocalDate.of(2026, 10, 1), new LoanPeriod(14)));
        assertNotNull(saved.getId());

        Optional<Loan> active = loanRepository.findActiveByReaderAndCopy(new ReaderId(1L), new BookCopyId(101L));
        assertTrue(active.isPresent());
        assertEquals(saved.getId(), active.get().getId());
    }

    @Test
    void shouldFindOverdueLoans() {
        loanRepository.save(new Loan(null, new ReaderId(1L), new BookId(1L), new BookCopyId(101L),
            LocalDate.of(2026, 9, 1), new LoanPeriod(14)));
        List<Loan> overdue = loanRepository.findOverdueLoans(LocalDate.of(2026, 10, 31));
        assertFalse(overdue.isEmpty());
    }

    @Test
    void shouldSaveReservationAndOrderByCreatedAt() {
        reservationRepository.save(new Reservation(null, new ReaderId(2L), new BookId(1L),
            ReservationStatus.WAITING, LocalDateTime.of(2026, 10, 2, 9, 0)));
        reservationRepository.save(new Reservation(null, new ReaderId(3L), new BookId(1L),
            ReservationStatus.WAITING, LocalDateTime.of(2026, 10, 1, 9, 0)));

        List<Reservation> queue = reservationRepository.findWaitingByBookOrderedByCreatedAt(new BookId(1L));
        assertEquals(2, queue.size());
        assertEquals(new ReaderId(3L), queue.get(0).getReaderId());
        assertTrue(reservationRepository.existsWaitingByReaderAndBook(new ReaderId(2L), new BookId(1L)));
    }

    @Test
    void shouldPublishDomainEventThroughAdapter() {
        EventRecorder.reset();
        publisher.publish(new BookLoanedEvent(new ReaderId(1L), new BookCopyId(101L)));
        assertTrue(EventRecorder.events().stream().anyMatch(e -> e instanceof BookLoanedEvent));
    }

    /** 事件监听测试配置：收集发布的事件 */
    @TestConfiguration
    static class EventConfig {
        @Bean
        EventRecorder eventRecorder() {
            return new EventRecorder();
        }
    }

    static class EventRecorder {
        private static final List<DomainEvent> EVENTS = new ArrayList<>();

        @EventListener
        void on(DomainEvent event) {
            EVENTS.add(event);
        }

        static void reset() {
            EVENTS.clear();
        }

        static List<DomainEvent> events() {
            return EVENTS;
        }
    }
}

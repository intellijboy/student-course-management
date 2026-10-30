package cn.intellijboy.demo.librarylending.domain.model;

import cn.intellijboy.demo.librarylending.domain.exception.BookCopyUnavailableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Book 聚合根领域单元测试：馆藏登记、数量统计与副本状态流转。
 */
class BookTest {

    private Book newBookWithCopies(int copyCount) {
        Book book = new Book(new BookId(1L), "DDD 实战", "作者");
        for (int i = 1; i <= copyCount; i++) {
            book.addCopy(new BookCopy(new BookCopyId((long) i)));
        }
        return book;
    }

    @Test
    void blankTitleRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Book(new BookId(1L), " ", "作者"));
    }

    @Test
    void registerCopiesAndCounts() {
        Book book = newBookWithCopies(3);
        assertEquals(3, book.totalCount());
        assertEquals(3, book.availableCount());
        assertEquals(0, book.borrowedCount());
        assertTrue(book.firstAvailableCopy().isPresent());
    }

    @Test
    void borrowMovesCopyToBorrowed() {
        Book book = newBookWithCopies(2);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        copy.borrow();
        assertEquals(BookCopyStatus.BORROWED, copy.getStatus());
        assertEquals(1, book.availableCount());
        assertEquals(1, book.borrowedCount());
    }

    @Test
    void borrowUnavailableCopyRejected() {
        Book book = newBookWithCopies(1);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        copy.borrow();
        assertThrows(BookCopyUnavailableException.class, copy::borrow);
    }

    @Test
    void reserveAndBorrowReserved() {
        Book book = newBookWithCopies(1);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        copy.reserve();
        assertEquals(BookCopyStatus.RESERVED, copy.getStatus());
        assertEquals(1, book.reservedCount());
        copy.borrowReserved();
        assertEquals(BookCopyStatus.BORROWED, copy.getStatus());
    }

    @Test
    void returnCopyRestoresAvailable() {
        Book book = newBookWithCopies(1);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        copy.borrow();
        copy.returnCopy();
        assertEquals(BookCopyStatus.AVAILABLE, copy.getStatus());
        assertTrue(copy.isAvailable());
    }

    @Test
    void returnNonBorrowedCopyRejected() {
        Book book = newBookWithCopies(1);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        assertThrows(BookCopyUnavailableException.class, copy::returnCopy);
    }

    @Test
    void allCopiesBorrowedThenNoAvailable() {
        Book book = newBookWithCopies(2);
        book.getCopies().forEach(BookCopy::borrow);
        assertFalse(book.firstAvailableCopy().isPresent());
        assertEquals(0, book.availableCount());
    }
}

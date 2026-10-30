package cn.intellijboy.demo.librarylending.domain.model;

import cn.intellijboy.demo.librarylending.domain.exception.BookCopyUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Book 聚合根领域单元测试：馆藏登记、数量统计与副本状态流转。
 */
@DisplayName("图书聚合测试")
class BookTest {

    private Book newBookWithCopies(int copyCount) {
        Book book = new Book(new BookId(1L), "DDD 实战", "作者");
        for (int i = 1; i <= copyCount; i++) {
            book.addCopy(new BookCopy(new BookCopyId((long) i)));
        }
        return book;
    }

    @Test
    @DisplayName("书名为空白时创建图书被拒绝")
    void blankTitleRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Book(new BookId(1L), " ", "作者"));
    }

    @Test
    @DisplayName("登记多个副本后总数、可借数与已借数统计正确")
    void registerCopiesAndCounts() {
        Book book = newBookWithCopies(3);
        assertEquals(3, book.totalCount());
        assertEquals(3, book.availableCount());
        assertEquals(0, book.borrowedCount());
        assertTrue(book.firstAvailableCopy().isPresent());
    }

    @Test
    @DisplayName("借阅后副本变为已借出")
    void borrowMovesCopyToBorrowed() {
        Book book = newBookWithCopies(2);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        copy.borrow();
        assertEquals(BookCopyStatus.BORROWED, copy.getStatus());
        assertEquals(1, book.availableCount());
        assertEquals(1, book.borrowedCount());
    }

    @Test
    @DisplayName("重复借阅同一副本被拒绝")
    void borrowUnavailableCopyRejected() {
        Book book = newBookWithCopies(1);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        copy.borrow();
        assertThrows(BookCopyUnavailableException.class, copy::borrow);
    }

    @Test
    @DisplayName("预约副本后可凭预约借出")
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
    @DisplayName("归还副本后恢复为可借状态")
    void returnCopyRestoresAvailable() {
        Book book = newBookWithCopies(1);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        copy.borrow();
        copy.returnCopy();
        assertEquals(BookCopyStatus.AVAILABLE, copy.getStatus());
        assertTrue(copy.isAvailable());
    }

    @Test
    @DisplayName("归还未借出的副本被拒绝")
    void returnNonBorrowedCopyRejected() {
        Book book = newBookWithCopies(1);
        BookCopy copy = book.firstAvailableCopy().orElseThrow();
        assertThrows(BookCopyUnavailableException.class, copy::returnCopy);
    }

    @Test
    @DisplayName("全部副本借出后无可借副本")
    void allCopiesBorrowedThenNoAvailable() {
        Book book = newBookWithCopies(2);
        book.getCopies().forEach(BookCopy::borrow);
        assertFalse(book.firstAvailableCopy().isPresent());
        assertEquals(0, book.availableCount());
    }
}

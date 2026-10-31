package cn.intellijboy.demo.librarylending.web.book;

import cn.intellijboy.demo.librarylending.domain.model.Book;

public record BookView(Long id, String title, String author, int totalCopies, int availableCopies) {
    public static BookView of(Book book) {
        return new BookView(book.getId().value(), book.getTitle(), book.getAuthor(),
            book.totalCount(), book.availableCount());
    }
}

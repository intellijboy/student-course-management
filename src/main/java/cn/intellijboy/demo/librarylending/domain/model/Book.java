package cn.intellijboy.demo.librarylending.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 图书聚合根：管理书目信息与馆藏副本集合，维护馆藏数量与可借统计。
 */
public class Book {

    private final BookId id;
    private final String title;
    private final String author;
    private final List<BookCopy> copies;

    public Book(BookId id, String title, String author) {
        this(id, title, author, new ArrayList<>());
    }

    public Book(BookId id, String title, String author, List<BookCopy> copies) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("书名不能为空");
        }
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = new ArrayList<>(copies);
    }

    /** 增补馆藏副本 */
    public void addCopy(BookCopy copy) {
        this.copies.add(copy);
    }

    /** 第一本可借副本 */
    public Optional<BookCopy> firstAvailableCopy() {
        return copies.stream().filter(BookCopy::isAvailable).findFirst();
    }

    /** 按副本 id 查找 */
    public Optional<BookCopy> findCopy(BookCopyId copyId) {
        return copies.stream().filter(c -> c.getId().equals(copyId)).findFirst();
    }

    public int totalCount() {
        return copies.size();
    }

    public int availableCount() {
        return (int) copies.stream().filter(BookCopy::isAvailable).count();
    }

    public int borrowedCount() {
        return (int) copies.stream().filter(c -> c.getStatus() == BookCopyStatus.BORROWED).count();
    }

    public int reservedCount() {
        return (int) copies.stream().filter(c -> c.getStatus() == BookCopyStatus.RESERVED).count();
    }

    public BookId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public List<BookCopy> getCopies() {
        return Collections.unmodifiableList(copies);
    }
}

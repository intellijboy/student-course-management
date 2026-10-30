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

    /**
     * 创建图书对象，馆藏副本集合默认为空。
     *
     * @param id     图书标识
     * @param title  书名
     * @param author 作者
     */
    public Book(BookId id, String title, String author) {
        this(id, title, author, new ArrayList<>());
    }

    /**
     * 创建图书对象并携带初始馆藏副本集合。
     *
     * @param id     图书标识
     * @param title  书名，不能为空白
     * @param author 作者
     * @param copies 初始馆藏副本集合，内部会复制为防御性副本
     * @throws IllegalArgumentException 书名为 null 或空白时抛出
     */
    public Book(BookId id, String title, String author, List<BookCopy> copies) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("书名不能为空");
        }
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = new ArrayList<>(copies);
    }

    /**
     * 增补馆藏副本，将指定副本加入本书的馆藏集合。
     *
     * @param copy 待添加的馆藏副本
     */
    public void addCopy(BookCopy copy) {
        this.copies.add(copy);
    }

    /**
     * 查找第一本可借出的馆藏副本。
     *
     * @return 第一本状态为可借的副本；若不存在则返回空 Optional
     */
    public Optional<BookCopy> firstAvailableCopy() {
        return copies.stream().filter(BookCopy::isAvailable).findFirst();
    }

    /**
     * 按副本标识查找馆藏副本。
     *
     * @param copyId 馆藏副本标识
     * @return 标识匹配的副本；若不存在则返回空 Optional
     */
    public Optional<BookCopy> findCopy(BookCopyId copyId) {
        return copies.stream().filter(c -> c.getId().equals(copyId)).findFirst();
    }

    /**
     * 统计本书馆藏副本总数。
     *
     * @return 馆藏副本总数
     */
    public int totalCount() {
        return copies.size();
    }

    /**
     * 统计当前处于可借状态的副本数量。
     *
     * @return 可借副本数量
     */
    public int availableCount() {
        return (int) copies.stream().filter(BookCopy::isAvailable).count();
    }

    /**
     * 统计当前处于已借出状态的副本数量。
     *
     * @return 已借出副本数量
     */
    public int borrowedCount() {
        return (int) copies.stream().filter(c -> c.getStatus() == BookCopyStatus.BORROWED).count();
    }

    /**
     * 统计当前处于预约占用状态的副本数量。
     *
     * @return 预约占用副本数量
     */
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

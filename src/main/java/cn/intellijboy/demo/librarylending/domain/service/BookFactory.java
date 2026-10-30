package cn.intellijboy.demo.librarylending.domain.service;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopy;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书工厂：创建书目并批量生成馆藏副本，集中处理创建校验。
 */
public class BookFactory {

    /**
     * 创建图书及其指定数量的馆藏副本，副本数量不合法时抛出异常。
     *
     * @param id 图书标识，不能为 {@code null}
     * @param title 图书标题
     * @param author 图书作者
     * @param copyCount 需要创建的馆藏副本数量，必须为正整数
     * @return 已挂载全部馆藏副本的图书聚合
     * @throws IllegalArgumentException 当副本数量小于或等于 0 时抛出
     */
    public Book createBook(BookId id, String title, String author, int copyCount) {
        if (copyCount <= 0) {
            throw new IllegalArgumentException("馆藏副本数量必须为正");
        }
        Book book = new Book(id, title, author);
        List<BookCopy> copies = new ArrayList<>();
        for (int i = 1; i <= copyCount; i++) {
            copies.add(new BookCopy(new BookCopyId(id.value() * 100L + i)));
        }
        copies.forEach(book::addCopy);
        return book;
    }
}

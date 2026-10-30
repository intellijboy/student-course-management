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

    /** 创建图书与指定数量的馆藏副本 */
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

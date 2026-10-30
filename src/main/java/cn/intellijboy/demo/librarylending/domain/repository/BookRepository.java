package cn.intellijboy.demo.librarylending.domain.repository;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;

import java.util.List;
import java.util.Optional;

/**
 * 图书仓储端口：领域层定义，由基础设施层实现。
 */
public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(BookId id);

    List<Book> findAll();
}

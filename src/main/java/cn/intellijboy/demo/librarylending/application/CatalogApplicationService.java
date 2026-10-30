package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.repository.BookIdGenerator;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.domain.service.BookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 馆藏目录应用服务：登记图书与馆藏副本、按图书查询馆藏。
 */
@Service
public class CatalogApplicationService {

    private final BookRepository bookRepository;
    private final BookFactory bookFactory;
    private final BookIdGenerator bookIdGenerator;

    public CatalogApplicationService(BookRepository bookRepository, BookFactory bookFactory,
                                     BookIdGenerator bookIdGenerator) {
        this.bookRepository = bookRepository;
        this.bookFactory = bookFactory;
        this.bookIdGenerator = bookIdGenerator;
    }

    /** 登记图书与指定数量的馆藏副本 */
    @Transactional
    public Book registerBook(String title, String author, int copyCount) {
        BookId id = bookIdGenerator.next();
        return bookRepository.save(bookFactory.createBook(id, title, author, copyCount));
    }

    @Transactional(readOnly = true)
    public Optional<Book> findById(BookId id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}

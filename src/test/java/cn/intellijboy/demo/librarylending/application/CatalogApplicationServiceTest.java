package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.repository.BookIdGenerator;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.domain.service.BookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CatalogApplicationService 应用层测试：馆藏登记与查询（内存 stub 仓储）。
 */
@DisplayName("馆藏目录应用服务测试")
class CatalogApplicationServiceTest {

    private CatalogApplicationService service;
    private InMemoryBookRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBookRepository();
        service = new CatalogApplicationService(repository, new BookFactory(), new SequentialBookIdGenerator());
    }

    @Test
    @DisplayName("登记图书时创建书名、作者与副本数量")
    void registerBookCreatesTitleAndCopies() {
        Book book = service.registerBook("DDD 实战", "作者", 3);
        assertEquals("DDD 实战", book.getTitle());
        assertEquals("作者", book.getAuthor());
        assertEquals(3, book.totalCount());
        assertEquals(3, book.availableCount());
    }

    @Test
    @DisplayName("按 ID 查询时返回已登记的图书")
    void findByIdReturnsRegisteredBook() {
        Book saved = service.registerBook("领域驱动设计", "Eric Evans", 2);
        Optional<Book> found = service.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("领域驱动设计", found.get().getTitle());
    }

    @Test
    @DisplayName("查询全部图书时返回所有已登记图书")
    void findAllReturnsAllBooks() {
        service.registerBook("图书一", "作者A", 1);
        service.registerBook("图书二", "作者B", 1);
        assertEquals(2, service.findAll().size());
    }

    /** 内存 stub 仓储 */
    static class InMemoryBookRepository implements BookRepository {
        private final Map<BookId, Book> store = new HashMap<>();

        @Override
        public Book save(Book book) {
            store.put(book.getId(), book);
            return book;
        }

        @Override
        public Optional<Book> findById(BookId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    /** 顺序递增 id 生成器 */
    static class SequentialBookIdGenerator implements BookIdGenerator {
        private final AtomicLong seq = new AtomicLong(1);

        @Override
        public BookId next() {
            return new BookId(seq.getAndIncrement());
        }
    }
}

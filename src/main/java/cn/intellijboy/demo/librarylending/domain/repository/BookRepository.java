package cn.intellijboy.demo.librarylending.domain.repository;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;

import java.util.List;
import java.util.Optional;

/**
 * 图书仓储端口：领域层定义，由基础设施层实现。
 */
public interface BookRepository {

    /**
     * 保存图书；若图书已存在则更新，否则新增。
     *
     * @param book 待保存的图书聚合，不能为 {@code null}
     * @return 保存后的图书聚合
     */
    Book save(Book book);

    /**
     * 按图书标识查找图书。
     *
     * @param id 图书标识，不能为 {@code null}
     * @return 对应的图书；不存在时返回 {@link Optional#empty()}
     */
    Optional<Book> findById(BookId id);

    /**
     * 查找全部图书。
     *
     * @return 全部图书列表；无图书时返回空列表
     */
    List<Book> findAll();
}

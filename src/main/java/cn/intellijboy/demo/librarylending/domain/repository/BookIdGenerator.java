package cn.intellijboy.demo.librarylending.domain.repository;

import cn.intellijboy.demo.librarylending.domain.model.BookId;

/**
 * 图书标识生成端口：领域层定义，由基础设施层实现（如数据库自增或内存递增）。
 */
public interface BookIdGenerator {

    /**
     * 生成下一个唯一的图书标识。
     *
     * @return 新生成的图书标识，保证不与已有标识重复
     */
    BookId next();
}

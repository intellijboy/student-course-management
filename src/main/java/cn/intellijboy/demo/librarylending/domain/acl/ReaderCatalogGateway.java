package cn.intellijboy.demo.librarylending.domain.acl;

import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

import java.util.Optional;

/**
 * 读者目录防腐层端口：领域层定义，由基础设施层对接外部学生上下文实现。
 */
public interface ReaderCatalogGateway {

    /** 按读者标识查找读者引用 */
    Optional<ReaderRef> findReader(ReaderId readerId);
}

package cn.intellijboy.demo.librarylending.domain.acl;

import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

import java.util.Optional;

/**
 * 读者目录防腐层端口：领域层定义，由基础设施层对接外部学生上下文实现。
 */
public interface ReaderCatalogGateway {

    /**
     * 按读者标识查找防腐层翻译后的读者引用。
     *
     * @param readerId 读者标识，不能为 {@code null}
     * @return 对应的读者引用；外部读者目录中不存在时返回 {@link Optional#empty()}
     */
    Optional<ReaderRef> findReader(ReaderId readerId);
}

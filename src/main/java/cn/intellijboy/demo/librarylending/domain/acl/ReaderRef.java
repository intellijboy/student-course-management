package cn.intellijboy.demo.librarylending.domain.acl;

import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

/**
 * 读者引用：防腐层从外部上下文翻译得到的读者信息，不暴露外部领域模型。
 */
public record ReaderRef(ReaderId readerId, String name) {

    /**
     * 紧凑构造器：校验读者引用信息的完整性。
     *
     * @param readerId 读者标识，不能为 {@code null}
     * @param name 读者姓名，不能为 {@code null} 或空白
     */
    public ReaderRef {
        if (readerId == null || name == null || name.isBlank()) {
            throw new IllegalArgumentException("读者引用信息不完整");
        }
    }
}

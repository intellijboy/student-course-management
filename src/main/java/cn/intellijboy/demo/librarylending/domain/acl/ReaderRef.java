package cn.intellijboy.demo.librarylending.domain.acl;

import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

/**
 * 读者引用：防腐层从外部上下文翻译得到的读者信息，不暴露外部领域模型。
 */
public record ReaderRef(ReaderId readerId, String name) {

    public ReaderRef {
        if (readerId == null || name == null || name.isBlank()) {
            throw new IllegalArgumentException("读者引用信息不完整");
        }
    }
}

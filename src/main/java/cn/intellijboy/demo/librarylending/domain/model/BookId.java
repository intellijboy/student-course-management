package cn.intellijboy.demo.librarylending.domain.model;

/**
 * 图书标识值对象。
 */
public record BookId(Long value) {

    public BookId {
        if (value == null) {
            throw new IllegalArgumentException("图书 id 不能为空");
        }
    }
}

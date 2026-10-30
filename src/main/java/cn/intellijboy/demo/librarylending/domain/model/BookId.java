package cn.intellijboy.demo.librarylending.domain.model;

/**
 * 图书标识值对象。
 */
public record BookId(Long value) {

    /**
     * @param value 图书 id，不能为 null
     */
    public BookId {
        if (value == null) {
            throw new IllegalArgumentException("图书 id 不能为空");
        }
    }
}

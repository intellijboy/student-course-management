package cn.intellijboy.demo.librarylending.domain.model;

/**
 * 馆藏副本标识值对象。
 */
public record BookCopyId(Long value) {

    /**
     * @param value 馆藏副本 id，不能为 null
     */
    public BookCopyId {
        if (value == null) {
            throw new IllegalArgumentException("馆藏副本 id 不能为空");
        }
    }
}

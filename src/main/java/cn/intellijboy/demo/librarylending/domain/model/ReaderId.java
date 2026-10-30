package cn.intellijboy.demo.librarylending.domain.model;

/**
 * 读者标识值对象：防腐层将现有学生上下文的学生翻译为该标识。
 */
public record ReaderId(Long value) {

    public ReaderId {
        if (value == null) {
            throw new IllegalArgumentException("读者 id 不能为空");
        }
    }
}

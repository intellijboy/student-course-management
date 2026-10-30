package cn.intellijboy.demo.librarylending.domain.model;

/**
 * 馆藏副本状态。
 */
public enum BookCopyStatus {
    /** 可借 */
    AVAILABLE,
    /** 已借出 */
    BORROWED,
    /** 已被预约占用（保留给预约读者） */
    RESERVED
}

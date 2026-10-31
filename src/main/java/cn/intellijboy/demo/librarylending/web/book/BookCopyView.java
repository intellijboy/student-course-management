package cn.intellijboy.demo.librarylending.web.book;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyStatus;

public record BookCopyView(Long id, BookCopyStatus status) {
}

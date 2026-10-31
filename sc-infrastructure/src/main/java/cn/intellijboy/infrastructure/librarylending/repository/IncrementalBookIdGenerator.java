/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.librarylending.repository;

import cn.intellijboy.domain.librarylending.model.BookId;
import cn.intellijboy.domain.librarylending.repository.BookIdGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 递增图书标识生成器：单机演示用内存递增；生产环境可替换为数据库序列。
 */
@Component
public class IncrementalBookIdGenerator implements BookIdGenerator {

    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public BookId next() {
        return new BookId(seq.getAndIncrement());
    }
}

/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.librarylending.acl;

import cn.intellijboy.domain.librarylending.acl.ReaderCatalogGateway;
import cn.intellijboy.domain.librarylending.acl.ReaderRef;
import cn.intellijboy.domain.librarylending.model.ReaderId;
import cn.intellijboy.infrastructure.studentcourse.repository.JpaStudentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 读者目录防腐层适配器：复用现有学生上下文的仓储，将学生翻译为本上下文的读者引用。
 */
@Component
public class ReaderCatalogGatewayAdapter implements ReaderCatalogGateway {

    private final JpaStudentRepository studentRepository;

    public ReaderCatalogGatewayAdapter(JpaStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Optional<ReaderRef> findReader(ReaderId readerId) {
        return studentRepository.findById(readerId.value())
            .map(student -> new ReaderRef(readerId, student.getName()));
    }
}

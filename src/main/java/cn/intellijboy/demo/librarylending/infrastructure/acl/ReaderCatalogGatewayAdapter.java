package cn.intellijboy.demo.librarylending.infrastructure.acl;

import cn.intellijboy.demo.librarylending.domain.acl.ReaderCatalogGateway;
import cn.intellijboy.demo.librarylending.domain.acl.ReaderRef;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.infrastructure.studentcourse.repository.JpaStudentRepository;
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

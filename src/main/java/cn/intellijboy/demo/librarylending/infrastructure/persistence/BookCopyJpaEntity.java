package cn.intellijboy.demo.librarylending.infrastructure.persistence;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book_copies")
public class BookCopyJpaEntity {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;

    public BookCopyJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookCopyStatus getStatus() {
        return status;
    }

    public void setStatus(BookCopyStatus status) {
        this.status = status;
    }
}

package cn.intellijboy.demo.librarylending.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class BookJpaEntity {

    @Id
    private Long id;

    private String title;

    private String author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER)
    @JoinColumn(name = "bookId")
    private List<BookCopyJpaEntity> copies = new ArrayList<>();

    public BookJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<BookCopyJpaEntity> getCopies() {
        return copies;
    }

    public void setCopies(List<BookCopyJpaEntity> copies) {
        this.copies = copies;
    }
}

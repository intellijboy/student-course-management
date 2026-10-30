package cn.intellijboy.demo.librarylending.infrastructure.mapper;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookCopy;
import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.BookCopyJpaEntity;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.BookJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper {

    public BookJpaEntity toJpa(Book book) {
        BookJpaEntity entity = new BookJpaEntity();
        entity.setId(book.getId().value());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setCopies(book.getCopies().stream().map(copy -> {
            BookCopyJpaEntity copyEntity = new BookCopyJpaEntity();
            copyEntity.setId(copy.getId().value());
            copyEntity.setStatus(copy.getStatus());
            return copyEntity;
        }).toList());
        return entity;
    }

    public Book toDomain(BookJpaEntity entity) {
        List<BookCopy> copies = entity.getCopies().stream()
            .map(c -> new BookCopy(new BookCopyId(c.getId()), c.getStatus()))
            .toList();
        return new Book(new BookId(entity.getId()), entity.getTitle(), entity.getAuthor(), copies);
    }
}

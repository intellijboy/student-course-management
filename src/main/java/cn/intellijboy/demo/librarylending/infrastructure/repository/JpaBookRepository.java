package cn.intellijboy.demo.librarylending.infrastructure.repository;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.infrastructure.mapper.BookMapper;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.SpringDataBookRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookRepository implements BookRepository {

    private final SpringDataBookRepository springDataRepository;
    private final BookMapper mapper;

    public JpaBookRepository(SpringDataBookRepository springDataRepository, BookMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Book save(Book book) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(book)));
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return springDataRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return springDataRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}

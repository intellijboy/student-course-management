package cn.intellijboy.demo.librarylending.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBookRepository extends JpaRepository<BookJpaEntity, Long> {
}

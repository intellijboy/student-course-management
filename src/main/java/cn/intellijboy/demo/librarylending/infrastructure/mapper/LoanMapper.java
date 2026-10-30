package cn.intellijboy.demo.librarylending.infrastructure.mapper;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.LoanPeriod;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.LoanJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanJpaEntity toJpa(Loan loan) {
        LoanJpaEntity entity = new LoanJpaEntity();
        entity.setId(loan.getId());
        entity.setReaderId(loan.getReaderId().value());
        entity.setBookId(loan.getBookId().value());
        entity.setBookCopyId(loan.getBookCopyId().value());
        entity.setBorrowedAt(loan.getBorrowedAt());
        entity.setDueDate(loan.getDueDate());
        entity.setReturnedAt(loan.getReturnedAt());
        return entity;
    }

    public Loan toDomain(LoanJpaEntity entity) {
        LoanPeriod period = new LoanPeriod(
            (int) java.time.temporal.ChronoUnit.DAYS.between(entity.getBorrowedAt(), entity.getDueDate()));
        Loan loan = new Loan(entity.getId(), new ReaderId(entity.getReaderId()),
            new BookId(entity.getBookId()), new BookCopyId(entity.getBookCopyId()),
            entity.getBorrowedAt(), period);
        if (entity.getReturnedAt() != null) {
            loan.returnBook(entity.getReturnedAt());
        }
        return loan;
    }
}

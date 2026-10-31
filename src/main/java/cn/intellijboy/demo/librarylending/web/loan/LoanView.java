package cn.intellijboy.demo.librarylending.web.loan;

import cn.intellijboy.demo.librarylending.domain.model.Loan;

import java.time.LocalDate;

public record LoanView(Long id, Long readerId, Long bookId, Long bookCopyId,
                       LocalDate borrowedAt, LocalDate dueDate, LocalDate returnedAt) {
    public static LoanView of(Loan loan) {
        return new LoanView(loan.getId(), loan.getReaderId().value(), loan.getBookId().value(),
            loan.getBookCopyId().value(), loan.getBorrowedAt(), loan.getDueDate(), loan.getReturnedAt());
    }
}

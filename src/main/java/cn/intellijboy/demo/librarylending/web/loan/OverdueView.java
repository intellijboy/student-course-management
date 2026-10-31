package cn.intellijboy.demo.librarylending.web.loan;

import cn.intellijboy.demo.librarylending.domain.model.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OverdueView(Long loanId, Long readerId, Long bookCopyId, LocalDate dueDate,
                          BigDecimal fee) {
    public static OverdueView of(Loan loan, BigDecimal fee) {
        return new OverdueView(loan.getId(), loan.getReaderId().value(), loan.getBookCopyId().value(),
            loan.getDueDate(), fee);
    }
}

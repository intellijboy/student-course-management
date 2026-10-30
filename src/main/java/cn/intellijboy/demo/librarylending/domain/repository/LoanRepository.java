package cn.intellijboy.demo.librarylending.domain.repository;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 借阅记录仓储端口：领域层定义，由基础设施层实现。
 */
public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    /** 读者在某一副本上的在借记录（未归还） */
    Optional<Loan> findActiveByReaderAndCopy(ReaderId readerId, BookCopyId bookCopyId);

    List<Loan> findByReader(ReaderId readerId);

    /** 全部在借记录（未归还） */
    List<Loan> findActiveLoans();

    /** 按日期的逾期在借记录 */
    List<Loan> findOverdueLoans(LocalDate today);
}

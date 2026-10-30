package cn.intellijboy.demo.librarylending.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataLoanRepository extends JpaRepository<LoanJpaEntity, Long> {

    /** 读者在某副本上的在借记录（未归还） */
    Optional<LoanJpaEntity> findFirstByReaderIdAndBookCopyIdAndReturnedAtIsNull(Long readerId, Long bookCopyId);

    List<LoanJpaEntity> findByReaderId(Long readerId);

    /** 全部在借记录（未归还） */
    List<LoanJpaEntity> findByReturnedAtIsNull();

    /** 逾期在借记录（未归还且应还日期早于指定日期） */
    List<LoanJpaEntity> findByReturnedAtIsNullAndDueDateBefore(LocalDate date);
}

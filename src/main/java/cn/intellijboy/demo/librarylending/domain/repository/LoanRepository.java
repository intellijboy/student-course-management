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

    /**
     * 保存借阅记录；若借阅记录已存在则更新，否则新增。
     *
     * @param loan 待保存的借阅记录，不能为 {@code null}
     * @return 保存后的借阅记录
     */
    Loan save(Loan loan);

    /**
     * 按借阅记录标识查找借阅记录。
     *
     * @param id 借阅记录标识，不能为 {@code null}
     * @return 对应的借阅记录；不存在时返回 {@link Optional#empty()}
     */
    Optional<Loan> findById(Long id);

    /**
     * 查找读者在某一副本上的在借记录（未归还）。
     *
     * @param readerId 读者标识，不能为 {@code null}
     * @param bookCopyId 馆藏副本标识，不能为 {@code null}
     * @return 该读者在该副本上的在借记录；不存在时返回 {@link Optional#empty()}
     */
    Optional<Loan> findActiveByReaderAndCopy(ReaderId readerId, BookCopyId bookCopyId);

    /**
     * 查找读者的全部借阅记录。
     *
     * @param readerId 读者标识，不能为 {@code null}
     * @return 该读者的借阅记录列表；无记录时返回空列表
     */
    List<Loan> findByReader(ReaderId readerId);

    /**
     * 查找全部在借记录（未归还）。
     *
     * @return 全部在借记录列表；无在借记录时返回空列表
     */
    List<Loan> findActiveLoans();

    /**
     * 查找截至指定日期已逾期的在借记录。
     *
     * @param today 用于判定逾期的当前日期，不能为 {@code null}
     * @return 已逾期的在借记录列表；无逾期记录时返回空列表
     */
    List<Loan> findOverdueLoans(LocalDate today);
}

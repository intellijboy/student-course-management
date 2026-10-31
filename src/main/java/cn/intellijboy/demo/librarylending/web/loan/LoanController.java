package cn.intellijboy.demo.librarylending.web.loan;

import cn.intellijboy.demo.librarylending.application.LendingQueryService;
import cn.intellijboy.demo.librarylending.application.LoanApplicationService;
import cn.intellijboy.demo.librarylending.application.ReturnResult;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class LoanController {

    private final LoanApplicationService loanService;
    private final LendingQueryService queryService;

    public LoanController(LoanApplicationService loanService, LendingQueryService queryService) {
        this.loanService = loanService;
        this.queryService = queryService;
    }

    /** 借阅 */
    @PostMapping("/api/loans")
    public ResponseEntity<LoanView> borrow(@RequestBody LoanRequest request) {
        Loan loan = loanService.borrow(new ReaderId(request.readerId()), new BookId(request.bookId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(LoanView.of(loan));
    }

    /** 归还 */
    @PostMapping("/api/loans/{id}/return")
    public ResponseEntity<ReturnView> returnCopy(@PathVariable Long id) {
        ReturnResult result = loanService.returnCopy(id, LocalDate.now());
        return ResponseEntity.ok(new ReturnView(result.overdueFee(),
            result.promotedReaderId() == null ? null : result.promotedReaderId().value()));
    }

    /** 按读者查询借阅 */
    @GetMapping("/api/readers/{readerId}/loans")
    public List<LoanView> readerLoans(@PathVariable Long readerId) {
        return queryService.loansOfReader(new ReaderId(readerId)).stream().map(LoanView::of).toList();
    }

    /** 逾期列表 */
    @GetMapping("/api/loans/overdue")
    public List<OverdueView> overdue() {
        LocalDate today = LocalDate.now();
        return queryService.overdueLoans(today).stream()
            .map(loan -> OverdueView.of(loan, queryService.overdueFeeOf(loan, today)))
            .toList();
    }
}

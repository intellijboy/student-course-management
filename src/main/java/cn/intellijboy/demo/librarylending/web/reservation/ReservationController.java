package cn.intellijboy.demo.librarylending.web.reservation;

import cn.intellijboy.demo.librarylending.application.LoanApplicationService;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final LoanApplicationService loanService;

    public ReservationController(LoanApplicationService loanService) {
        this.loanService = loanService;
    }

    /** 预约借阅 */
    @PostMapping
    public ResponseEntity<ReservationView> reserve(@RequestBody ReservationRequest request) {
        Reservation reservation = loanService.reserve(new ReaderId(request.readerId()), new BookId(request.bookId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservationView.of(reservation));
    }
}

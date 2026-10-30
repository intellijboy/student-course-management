package cn.intellijboy.demo.librarylending.web;

import cn.intellijboy.demo.librarylending.application.CatalogApplicationService;
import cn.intellijboy.demo.librarylending.application.LendingQueryService;
import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final CatalogApplicationService catalogService;
    private final LendingQueryService queryService;

    public BookController(CatalogApplicationService catalogService, LendingQueryService queryService) {
        this.catalogService = catalogService;
        this.queryService = queryService;
    }

    /** 登记图书与馆藏副本 */
    @PostMapping
    public ResponseEntity<Dtos.BookView> register(@RequestBody BookRequest request) {
        Book book = catalogService.registerBook(request.title(), request.author(), request.copyCount());
        return ResponseEntity.status(HttpStatus.CREATED).body(Dtos.BookView.of(book));
    }

    /** 按图书查询馆藏状态与预约队列 */
    @GetMapping("/{id}")
    public ResponseEntity<?> status(@PathVariable Long id) {
        return queryService.bookStatus(new BookId(id))
            .map(status -> {
                List<Dtos.ReservationView> queue = status.reservationQueue().stream()
                    .map(r -> new Dtos.ReservationView(r.id(), r.readerId(), r.status().name(), r.createdAt()))
                    .toList();
                return ResponseEntity.ok(new Dtos.BookStatusView(
                    status.bookId().value(), status.title(), status.availableCount(), status.borrowedCount(), queue));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /** 图书列表 */
    @GetMapping
    public List<Dtos.BookView> list() {
        return catalogService.findAll().stream().map(Dtos.BookView::of).toList();
    }
}

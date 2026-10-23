package cn.intellijboy.demo.web;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 查询全部书目列表。
     *
     * @return 200 与书目列表
     */
    @GetMapping
    public List<Book> list() {
        return List.copyOf(books.values());
    }

    /**
     * 按 id 查询单个书目。
     *
     * @param id 书目 id
     * @return 200 与书目信息，id 不存在时返回 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> get(@PathVariable Long id) {
        return Optional.ofNullable(books.get(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 创建书目。
     *
     * @param request 创建请求（title、author），id 由服务端生成
     * @return 201 与新建书目
     */
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody BookRequest request) {
        Book book = new Book(idGenerator.getAndIncrement(), request.title(), request.author());
        books.put(book.id(), book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    /**
     * 更新指定 id 书目的标题与作者。
     *
     * @param id      书目 id
     * @param request 更新请求（title、author）
     * @return 200 与更新后书目，id 不存在时返回 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody BookRequest request) {
        Book existing = books.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Book updated = new Book(id, request.title(), request.author());
        books.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除指定 id 的书目。
     *
     * @param id 书目 id
     * @return 204，id 不存在时返回 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (books.remove(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

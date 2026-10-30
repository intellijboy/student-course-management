package cn.intellijboy.demo.librarylending.web;

/**
 * 借阅请求。
 */
public record LoanRequest(Long readerId, Long bookId) {
}

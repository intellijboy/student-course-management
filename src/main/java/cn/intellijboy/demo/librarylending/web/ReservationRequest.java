package cn.intellijboy.demo.librarylending.web;

/**
 * 预约请求。
 */
public record ReservationRequest(Long readerId, Long bookId) {
}

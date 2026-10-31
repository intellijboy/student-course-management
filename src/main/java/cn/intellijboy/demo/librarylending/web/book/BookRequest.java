package cn.intellijboy.demo.librarylending.web.book;

/**
 * 登记图书请求。
 */
public record BookRequest(String title, String author, int copyCount) {
}

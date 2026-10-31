package cn.intellijboy.demo.librarylending.web.loan;

import java.math.BigDecimal;

public record ReturnView(BigDecimal overdueFee, Long promotedReaderId) {
}

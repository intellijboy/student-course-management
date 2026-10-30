package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.model.ReaderId;

import java.math.BigDecimal;

/**
 * 归还结果：逾期费用与触发的预约转正读者（若有）。
 */
public record ReturnResult(BigDecimal overdueFee, ReaderId promotedReaderId) {
}

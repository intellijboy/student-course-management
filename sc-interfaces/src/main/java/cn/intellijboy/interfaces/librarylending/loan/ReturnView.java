/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.loan;

import java.math.BigDecimal;

/**
 * 归还结果视图：承载归还产生的逾期费用与（若有）被转正预约的读者标识。
 */
public record ReturnView(BigDecimal overdueFee, Long promotedReaderId) {
}

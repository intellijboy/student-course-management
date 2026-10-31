/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.application.librarylending;

import cn.intellijboy.domain.librarylending.event.DomainEventPublisher;
import cn.intellijboy.domain.librarylending.repository.ReservationRepository;
import cn.intellijboy.domain.librarylending.service.BookFactory;
import cn.intellijboy.domain.librarylending.service.BorrowingPolicy;
import cn.intellijboy.domain.librarylending.service.LoanFactory;
import cn.intellijboy.domain.librarylending.service.ReservationPromotionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 组合根配置：在应用层组装领域服务与工厂，保持 domain 层无框架依赖（依赖倒置）。
 */
@Configuration
public class LibraryLendingDomainConfig {

    @Bean
    public BorrowingPolicy borrowingPolicy() {
        return new BorrowingPolicy();
    }

    @Bean
    public BookFactory bookFactory() {
        return new BookFactory();
    }

    @Bean
    public LoanFactory loanFactory() {
        return new LoanFactory();
    }

    @Bean
    public ReservationPromotionService reservationPromotionService(
            ReservationRepository reservationRepository, DomainEventPublisher publisher) {
        return new ReservationPromotionService(reservationRepository, publisher);
    }
}

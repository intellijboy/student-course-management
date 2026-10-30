package cn.intellijboy.demo.librarylending.application;

import cn.intellijboy.demo.librarylending.domain.event.DomainEventPublisher;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.domain.service.BookFactory;
import cn.intellijboy.demo.librarylending.domain.service.BorrowingPolicy;
import cn.intellijboy.demo.librarylending.domain.service.LoanFactory;
import cn.intellijboy.demo.librarylending.domain.service.ReservationPromotionService;
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

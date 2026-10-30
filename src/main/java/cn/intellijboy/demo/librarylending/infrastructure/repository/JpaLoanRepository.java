package cn.intellijboy.demo.librarylending.infrastructure.repository;

import cn.intellijboy.demo.librarylending.domain.model.BookCopyId;
import cn.intellijboy.demo.librarylending.domain.model.Loan;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.repository.LoanRepository;
import cn.intellijboy.demo.librarylending.infrastructure.mapper.LoanMapper;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.SpringDataLoanRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaLoanRepository implements LoanRepository {

    private final SpringDataLoanRepository springDataRepository;
    private final LoanMapper mapper;

    public JpaLoanRepository(SpringDataLoanRepository springDataRepository, LoanMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Loan save(Loan loan) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(loan)));
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Loan> findActiveByReaderAndCopy(ReaderId readerId, BookCopyId bookCopyId) {
        return springDataRepository
            .findFirstByReaderIdAndBookCopyIdAndReturnedAtIsNull(readerId.value(), bookCopyId.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<Loan> findByReader(ReaderId readerId) {
        return springDataRepository.findByReaderId(readerId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Loan> findActiveLoans() {
        return springDataRepository.findByReturnedAtIsNull().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Loan> findOverdueLoans(LocalDate today) {
        return springDataRepository.findByReturnedAtIsNullAndDueDateBefore(today).stream()
            .map(mapper::toDomain).toList();
    }
}

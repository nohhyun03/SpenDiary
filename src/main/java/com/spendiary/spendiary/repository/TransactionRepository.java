package com.spendiary.spendiary.repository;

import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAuthor(User author);

    List<Transaction> findByAuthorAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(User author, LocalDate startDate , LocalDate endDate);

}

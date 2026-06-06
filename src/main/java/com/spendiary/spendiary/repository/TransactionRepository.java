package com.spendiary.spendiary.repository;

import com.spendiary.spendiary.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

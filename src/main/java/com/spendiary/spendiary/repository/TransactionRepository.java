package com.spendiary.spendiary.repository;

import com.spendiary.spendiary.entity.Category;
import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAuthor(User author);

    List<Transaction> findByAuthorAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(User author, LocalDate startDate , LocalDate endDate);

    List<Transaction> findByAuthorAndCategoryIdAndContentContainingOrAuthorAndCategoryIdAndMemoContainingOrderByTransactionDateDesc(
            User author1, Long categoryId1, String content, User author2, Long categoryId2, String memo);

    List<Transaction> findByAuthorAndCategoryIdOrderByTransactionDateDesc(User author, Long categoryId);

    List<Transaction> findByAuthorAndContentContainingOrAuthorAndMemoContainingOrderByTransactionDateDesc(
            User author1, String content, User author2, String memo);

    List<Transaction> findByAuthorOrderByTransactionDateDesc(User author);

    List<Transaction> findByCategory(Category category);

    void deleteAllByAuthor(User author);
}

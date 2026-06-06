package com.spendiary.spendiary.repository;

import com.spendiary.spendiary.entity.Category;
import com.spendiary.spendiary.entity.TransactionType;
import com.spendiary.spendiary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByAuthorAndType(User author, TransactionType type);

    boolean existsByAuthorAndNameAndType(User author, String name, TransactionType type);
}

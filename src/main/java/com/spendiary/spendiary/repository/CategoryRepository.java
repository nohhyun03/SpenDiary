package com.spendiary.spendiary.repository;

import com.spendiary.spendiary.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

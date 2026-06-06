package com.spendiary.spendiary.service;

import com.spendiary.spendiary.dto.CategoryRequest;
import com.spendiary.spendiary.entity.Category;
import com.spendiary.spendiary.entity.TransactionType;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void createDefaultCategory(User author) {

        List<String> expenses = List.of("미분류", "식비", "교통비", "문화생활");
        List<String> incomes = List.of("미분류", "월급", "용돈", "기타수입");

        expenses.forEach(name ->
                createCategory(author, name, TransactionType.EXPENSE, true)
        );
        incomes.forEach(name ->
                createCategory(author, name, TransactionType.INCOME, true)
        );
    }

    @Transactional
    public void createCustomCategory(User author, CategoryRequest request) {
        String trimmedName = request.getName().trim();
        TransactionType type = request.getType();
        validateDuplicateCategory(author, trimmedName, type);

        createCategory(author, trimmedName, type, false);
    }

    private void createCategory(User author, String name, TransactionType type, boolean isDefault){
        Category category = new Category();
        category.setName(name);
        category.setAuthor(author);
        category.setType(type);
        category.setIsDefault(isDefault);
        categoryRepository.save(category);
    }

    public List<Category> getCategoriesByType(User author, TransactionType type) {
        return categoryRepository.findByAuthorAndType(author, type);
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
    }

    @Transactional
    public void updateCategoryName(Long categoryId, CategoryRequest request, User author) {
        Category category = getCategoryById(categoryId);

        validateCategoryOwnership(category, author);
        validateNotDefaultCategory(category);

        String trimmedName = request.getName().trim();
        validateDuplicateCategory(author, trimmedName, category.getType());

        category.setName(trimmedName);
    }

    @Transactional
    public void deleteCategory(Long categoryId, User author) {
        Category category = getCategoryById(categoryId);

        validateCategoryOwnership(category, author);
        validateNotDefaultCategory(category);

        categoryRepository.delete(category);
    }

    protected void validateCategoryOwnership(Category category, User author) {
        if (!category.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("해당 카테고리에 대한 권한이 없습니다.");
        }
    }
    private void validateNotDefaultCategory(Category category) {
        if (category.getIsDefault()) {
            throw new IllegalStateException("시스템 기본 카테고리는 수정하거나 삭제할 수 없습니다.");
        }
    }
    private void validateDuplicateCategory(User author, String name, TransactionType type) {
        if(categoryRepository.existsByAuthorAndNameAndType(author, name, type)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }
    }

}

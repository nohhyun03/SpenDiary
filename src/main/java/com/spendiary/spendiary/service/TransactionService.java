package com.spendiary.spendiary.service;

import com.spendiary.spendiary.dto.TransactionRequest;
import com.spendiary.spendiary.entity.Category;
import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional
    public void createTransaction(User author, TransactionRequest request) {
        Category category = categoryService.getCategoryById(request.getCategoryId());

        categoryService.validateCategoryOwnership(category, author);

        Transaction transaction = Transaction.builder()
                .author(author)
                .type(request.getType())
                .category(category)
                .content(request.getContent().trim())
                .amount(request.getAmount())
                .memo(request.getMemo())
                .transactionDate(request.getTransactionDate())
                .build();

        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactions(User author){
        return transactionRepository.findByAuthor(author);
    }

    public List<Transaction> getTransactionsByDate(User author, LocalDate date) {
        return transactionRepository.findByAuthorAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(author, date, date);
    }

    public List<Transaction> getTransactionsByPeriod(User author, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("조회 시작일은 종료일보다 늦을 수 없습니다. 올바른 기간을 설정해 주세요.");
        }
        return transactionRepository.findByAuthorAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(author, startDate, endDate);
    }

    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 내역입니다."));
    }

    @Transactional
    public void updateTransaction(Long transactionId, User author, TransactionRequest request) {
        Transaction transaction = getTransactionById(transactionId);

        validateTransactionOwnership(transaction, author);

        Category category = categoryService.getCategoryById(request.getCategoryId());

        categoryService.validateCategoryOwnership(category, author);

        transaction.setType(request.getType());
        transaction.setCategory(category);
        transaction.setContent(request.getContent().trim());
        transaction.setAmount(request.getAmount());
        transaction.setMemo(request.getMemo());
        transaction.setTransactionDate(request.getTransactionDate());
    }

    @Transactional
    public void deleteTransaction(Long transactionId, User author) {
        Transaction transaction = getTransactionById(transactionId);
        validateTransactionOwnership(transaction, author);
        transactionRepository.delete(transaction);
    }

    public void validateTransactionOwnership(Transaction transaction, User author) {
        if (!transaction.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("해당 내역에 대한 권한이 없습니다.");
        }
    }
}


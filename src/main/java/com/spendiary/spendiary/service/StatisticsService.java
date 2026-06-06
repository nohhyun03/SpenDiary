package com.spendiary.spendiary.service;

import com.spendiary.spendiary.dto.StatisticsResponse;
import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.TransactionType;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final TransactionRepository transactionRepository;

    public StatisticsResponse getStatisticsByPeriod(User author, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("조회 시작일은 종료일보다 늦을 수 없습니다. 올바른 기간을 설정해 주세요.");
        }
        List<Transaction> transactions = transactionRepository.findByAuthorAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(author, startDate, endDate);
        return calculateStatistics(transactions);
    }

    public StatisticsResponse getMonthlyStatistics(User author, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1); // 해당 월의 1일
        LocalDate endDate = yearMonth.atEndOfMonth(); // 해당 월의 마지막 일

        return getStatisticsByPeriod(author, startDate, endDate);
    }

    private StatisticsResponse calculateStatistics(List<Transaction> transactions) {
        int totalIncome = 0;
        int totalExpense = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome += transaction.getAmount();
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                totalExpense += transaction.getAmount();
            }
        }

        return StatisticsResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalBalance(totalIncome - totalExpense)
                .build();
    }
}

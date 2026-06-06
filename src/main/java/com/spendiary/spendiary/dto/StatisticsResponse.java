package com.spendiary.spendiary.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatisticsResponse {
    private Integer totalIncome;
    private Integer totalExpense;
    private Integer totalBalance;
}

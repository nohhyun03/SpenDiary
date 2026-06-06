package com.spendiary.spendiary.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CalendarDay {
    private Integer day;
    private LocalDate date;
    private boolean isCurrentMonth;
    private boolean hasData;
    private boolean isSelected;

    @Builder.Default
    private Integer incomeAmount = 0;
    @Builder.Default
    private Integer expenseAmount = 0;
}

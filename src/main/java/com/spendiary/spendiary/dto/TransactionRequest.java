package com.spendiary.spendiary.dto;

import com.spendiary.spendiary.entity.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionRequest {
    private TransactionType type;
    private LocalDate transactionDate;
    private Long categoryId;
    private String content;
    private Integer amount;
    private String memo;
}


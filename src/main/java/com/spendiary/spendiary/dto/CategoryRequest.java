package com.spendiary.spendiary.dto;

import com.spendiary.spendiary.entity.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    private String name;
    private TransactionType type;
}

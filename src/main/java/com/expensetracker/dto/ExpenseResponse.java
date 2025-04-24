package com.expensetracker.dto;

import java.time.LocalDate;

import lombok.Data;


@Data
public class ExpenseResponse {
    private Long id;
    private Double amount;
    private String description;
    private String category;
    private LocalDate date;

    public ExpenseResponse() {
    }

    public ExpenseResponse(Long id, Double amount, String description, String category, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }
}

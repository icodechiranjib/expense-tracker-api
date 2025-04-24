package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest {

    @NotNull
    @Positive
    private Double amount;

    private String description;

    @NotBlank
    private String category;

    private LocalDate date; 

    public ExpenseRequest() {
    }

    public ExpenseRequest(Double amount, String description, String category, LocalDate date) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }
}

package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ExpenseResponse create(@Valid @RequestBody ExpenseRequest request, @RequestParam Long userId) {
        return expenseService.createExpense(request, userId);
    }

    @GetMapping
    public List<ExpenseResponse> getAll(@RequestParam Long userId) {
        return expenseService.getUserExpenses(userId);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request, @RequestParam Long userId) {
        return expenseService.updateExpense(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestParam Long userId) {
        expenseService.deleteExpense(id, userId);
    }
}

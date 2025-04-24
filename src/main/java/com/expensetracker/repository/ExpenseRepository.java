package com.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // For pagination
    Page<Expense> findAllByUser(User user, Pageable pageable);

    // For full report
    List<Expense> findAllByUser(User user);

    List<Expense> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}

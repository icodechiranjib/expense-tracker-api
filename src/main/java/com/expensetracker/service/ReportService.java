package com.expensetracker.service;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Double getTotalForUserBetweenDates(Long userId, LocalDate start, LocalDate end) {
        return expenseRepository.findByUserAndDateBetween(getUser(userId), start, end)
                .stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getTotalPerCategory(Long userId, LocalDate start, LocalDate end) {
        return expenseRepository.findByUserAndDateBetween(getUser(userId), start, end)
                .stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
    }

    public Map<String, Map<String, Double>> getMonthlyReport(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return expenseRepository.findAllByUser(user).stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getYear() + "-" + String.format("%02d", e.getDate().getMonthValue()),
                        Collectors.groupingBy(
                                Expense::getCategory,
                                Collectors.summingDouble(Expense::getAmount))));
    }
}

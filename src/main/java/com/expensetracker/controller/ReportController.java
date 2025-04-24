package com.expensetracker.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.exception.InvalidDateRangeException;
import com.expensetracker.service.ReportService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/total")
    public Double totalExpense(
            @RequestParam @NotNull @Positive Long userId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String start,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must be before end date.");
        }
        return reportService.getTotalForUserBetweenDates(userId, startDate, endDate);
    }

    @GetMapping("/categories")
    public Map<String, Double> totalByCategory(
            @RequestParam @NotNull @Positive Long userId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String start,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must be before end date.");
        }
        return reportService.getTotalPerCategory(userId, startDate, endDate);
    }

    @GetMapping("/monthly")
    public Map<String, Map<String, Double>> monthlyReport(
            @RequestParam @NotNull @Positive Long userId) {
        return reportService.getMonthlyReport(userId);
    }
}

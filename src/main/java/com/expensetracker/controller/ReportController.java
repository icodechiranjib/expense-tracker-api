package com.expensetracker.controller;

import com.expensetracker.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/total")
    public Double totalExpense(
            @RequestParam Long userId,
            @RequestParam String start,
            @RequestParam String end) {
        return reportService.getTotalForUserBetweenDates(userId, LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/categories")
    public Map<String, Double> totalByCategory(
            @RequestParam Long userId,
            @RequestParam String start,
            @RequestParam String end) {
        return reportService.getTotalPerCategory(userId, LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/monthly")
    public Map<String, Map<String, Double>> monthlyReport(@RequestParam Long userId) {
        return reportService.getMonthlyReport(userId);
    }
}

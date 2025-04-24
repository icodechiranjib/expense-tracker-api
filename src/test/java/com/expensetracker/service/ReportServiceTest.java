package com.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;

public class ReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTotalForUserBetweenDates() {
        Long userId = 1L;
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        User user = new User();
        user.setId(userId);
        Expense expense1 = new Expense(1L, 100.0, "Lunch", "Food", LocalDate.of(2023, 6, 1), user);
        Expense expense2 = new Expense(2L, 200.0, "Dinner", "Food", LocalDate.of(2023, 6, 2), user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.findByUserAndDateBetween(user, start, end))
                .thenReturn(Arrays.asList(expense1, expense2));

        Double total = reportService.getTotalForUserBetweenDates(userId, start, end);

        assertEquals(300.0, total);
    }

    @Test
    void testGetTotalPerCategory() {
        Long userId = 1L;
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        User user = new User();
        user.setId(userId);
        Expense expense1 = new Expense(1L, 100.0, "Lunch", "Food", LocalDate.of(2023, 6, 1), user);
        Expense expense2 = new Expense(2L, 200.0, "Dinner", "Food", LocalDate.of(2023, 6, 2), user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.findByUserAndDateBetween(user, start, end))
                .thenReturn(Arrays.asList(expense1, expense2));

        Map<String, Double> totals = reportService.getTotalPerCategory(userId, start, end);

        assertEquals(1, totals.size());
        assertEquals(300.0, totals.get("Food"));
    }

    @Test
    void testGetMonthlyReport() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Expense expense1 = new Expense(1L, 100.0, "Lunch", "Food", LocalDate.of(2023, 6, 1), user);
        Expense expense2 = new Expense(2L, 200.0, "Dinner", "Food", LocalDate.of(2023, 6, 2), user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser(user)).thenReturn(Arrays.asList(expense1, expense2));

        Map<String, Map<String, Double>> report = reportService.getMonthlyReport(userId);

        assertEquals(1, report.size());
        assertEquals(300.0, report.get("2023-06").get("Food"));
    }

}

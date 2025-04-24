package com.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;

public class ExpenseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExpense() {
        Long userId = 1L;
        ExpenseRequest request = new ExpenseRequest(100.0, "Lunch", "Food", LocalDate.now());
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.createExpense(request, userId);

        assertNotNull(response);
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(request.getDescription(), response.getDescription());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testCreateExpenseWithoutDate() {
        Long userId = 1L;
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(100.0);
        request.setDescription("Lunch");
        request.setCategory("Food");

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.createExpense(request, userId);

        assertNotNull(response);
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(request.getDescription(), response.getDescription());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testGetUserExpenses() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Expense expense = new Expense(1L, 100.0, "Lunch", "Food", LocalDate.now(), user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser(user)).thenReturn(Arrays.asList(expense));

        List<ExpenseResponse> responses = expenseService.getUserExpenses(userId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(expense.getAmount(), responses.get(0).getAmount());
    }

    @Test
    void testUpdateExpense() {
        Long userId = 1L;
        Long expenseId = 1L;
        ExpenseRequest request = new ExpenseRequest(200.0, "Dinner", "Food", LocalDate.now());
        User user = new User();
        user.setId(userId);
        Expense expense = new Expense(expenseId, 100.0, "Lunch", "Food", LocalDate.now(), user);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.updateExpense(expenseId, request, userId);

        assertNotNull(response);
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(request.getDescription(), response.getDescription());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testUpdateExpenseWithoutDesc() {
        Long userId = 1L;
        Long expenseId = 1L;
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(200.0);
        request.setCategory("Food");
        request.setDate(LocalDate.now());

        User user = new User();
        user.setId(userId);
        Expense expense = new Expense(expenseId, 100.0, "Lunch", "Food", LocalDate.now(), user);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.updateExpense(expenseId, request, userId);

        assertNotNull(response);
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(expense.getDescription(), response.getDescription());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testUpdateExpenseWithoutDate() {
        Long userId = 1L;
        Long expenseId = 1L;
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(200.0);
        request.setCategory("Food");
        request.setDescription("Dinner");

        User user = new User();
        user.setId(userId);
        Expense expense = new Expense(expenseId, 100.0, "Lunch", "Food", LocalDate.now(), user);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.updateExpense(expenseId, request, userId);

        assertNotNull(response);
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(expense.getDescription(), response.getDescription());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testDeleteExpense() {
        Long userId = 1L;
        Long expenseId = 1L;
        User user = new User();
        user.setId(userId);
        Expense expense = new Expense(expenseId, 100.0, "Lunch", "Food", LocalDate.now(), user);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        expenseService.deleteExpense(expenseId, userId);

        verify(expenseRepository, times(1)).delete(expense);
    }

    @Test
    void testDeleteExpenseUnauthorized() {
        Long userId = 1L;
        Long expenseId = 1L;
        User user = new User();
        user.setId(2L); // Different user ID
        Expense expense = new Expense(expenseId, 100.0, "Lunch", "Food", LocalDate.now(), user);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        assertThrows(BadRequestException.class, () -> expenseService.deleteExpense(expenseId, userId));
        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void testDeleteExpenseNotFound() {
        Long userId = 1L;
        Long expenseId = 1L;

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> expenseService.deleteExpense(expenseId, userId));
        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void testUnauthorizedUpdateExpense() {
        Long userId = 1L;
        Long expenseId = 1L;
        ExpenseRequest request = new ExpenseRequest(200.0, "Dinner", "Food", LocalDate.now());
        User user = new User();
        user.setId(2L); // Different user ID
        Expense expense = new Expense(expenseId, 100.0, "Lunch", "Food", LocalDate.now(), user);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        assertThrows(BadRequestException.class, () -> expenseService.updateExpense(expenseId, request, userId));
        verify(expenseRepository, never()).save(any(Expense.class));
    }

}

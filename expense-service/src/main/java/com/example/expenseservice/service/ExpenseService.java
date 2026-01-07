/*package com.example.expenseservice.service;

import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.feign.BudgetServiceClient;
import com.example.expenseservice.repository.ExpenseRepository;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetServiceClient budgetServiceClient;


    public List<Expense> getAllExpenses() {

            return expenseRepository.findAll();
        }


    public Expense getExpenseById(Long id) {  // Enlevé userId
        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null) {
            return null;
        }

        return expense;
    }



    public List<Expense> getExpensesByCategory(String category) {

            return expenseRepository.findByCategory(category);  // ADMIN: tous les expenses de cette catégorie

    }

    @Transactional
    public Expense createExpense(Expense expense) {

        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long id) {

            expenseRepository.deleteById(id);

    }*/

package com.example.expenseservice.service;

import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.feign.BudgetServiceClient;
import com.example.expenseservice.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetServiceClient budgetServiceClient;

    // =========== CRUD Operations ===========

    /**
     * Get all expenses (returns all - filtering should be done at controller/gateway level)
     */
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    /**
     * Get expense by ID
     */
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    /**
     * Get expenses by category (all users)
     */
    public List<Expense> getExpensesByCategory(String category) {
        return expenseRepository.findByCategory(category);
    }

    /**
     * Get expenses by user ID
     */
    public List<Expense> getExpensesByUserId(String userId) {
        return expenseRepository.findByUserId(userId);
    }

    /**
     * Get expenses by category and user
     */
    public List<Expense> getExpensesByCategoryAndUserId(String category, String userId) {
        return expenseRepository.findByCategoryAndUserId(category, userId);
    }

    /**
     * Get expenses by date range
     */
    public List<Expense> getExpensesByDateRange(LocalDate start, LocalDate end) {
        return expenseRepository.findByDateBetween(start, end);
    }

    /**
     * Get expenses by date range and user
     */
    public List<Expense> getExpensesByDateRangeAndUserId(LocalDate start, LocalDate end, String userId) {
        return expenseRepository.findByDateBetweenAndUserId(start, end, userId);
    }

    /**
     * Create a new expense with optional budget validation
     */
    @Transactional
    public Expense createExpense(Expense expense) {
        // Validate required fields
        if (expense.getAmount() == null || expense.getCategory() == null ||
                expense.getDate() == null || expense.getUserId() == null) {
            throw new IllegalArgumentException("Missing required fields: amount, category, date, or userId");
        }

        // Optional: Check budget before creating expense
        try {
            String budgetCheckResult = budgetServiceClient.checkBudget(
                    expense.getCategory(),
                    expense.getAmount().doubleValue(),
                    expense.getUserId()
            );
            log.info("Budget check result: {}", budgetCheckResult);
            // You can add logic here to prevent expense creation if budget exceeded
            // For now, we just log the result
        } catch (Exception e) {
            log.warn("Budget service unavailable: {}", e.getMessage());
            // Continue creating expense even if budget service is down
        }

        // Save expense
        return expenseRepository.save(expense);
    }

    /**
     * Delete expense by ID
     */
    @Transactional
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new IllegalArgumentException("Expense with ID " + id + " not found");
        }
        expenseRepository.deleteById(id);
    }

    /**
     * Update an existing expense
     */
    @Transactional
    public Expense updateExpense(Long id, Expense updatedExpense) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense with ID " + id + " not found"));

        // Update fields
        if (updatedExpense.getAmount() != null) {
            existingExpense.setAmount(updatedExpense.getAmount());
        }
        if (updatedExpense.getCategory() != null) {
            existingExpense.setCategory(updatedExpense.getCategory());
        }
        if (updatedExpense.getDescription() != null) {
            existingExpense.setDescription(updatedExpense.getDescription());
        }
        if (updatedExpense.getDate() != null) {
            existingExpense.setDate(updatedExpense.getDate());
        }

        return expenseRepository.save(existingExpense);
    }
}







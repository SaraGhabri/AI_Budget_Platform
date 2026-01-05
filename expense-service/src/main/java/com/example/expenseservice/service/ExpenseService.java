package com.example.expenseservice.service;

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

    }




}


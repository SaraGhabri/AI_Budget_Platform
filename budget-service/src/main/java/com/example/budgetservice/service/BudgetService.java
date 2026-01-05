package com.example.budgetservice.service;

import com.example.budgetservice.entity.Budget;
import com.example.budgetservice.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    // =========== CRUD Operations ===========

    // USER: voit seulement ses propres budgets (besoin de userId)
    // ADMIN: voit tous les budgets (pas besoin de userId)
    public List<Budget> getAllBudgets() {

            return budgetRepository.findAll();  // ADMIN voit tout

    }

    // ADMIN peut chercher un budget par catégorie (n'importe quel user)
    // USER cherche son propre budget par catégorie
    public Budget getBudgetByCategory(String category) {

            return budgetRepository.findByCategory(category)
                    .stream()
                    .findFirst()
                    .orElse(null);

    }

    @Transactional
    public Budget createOrUpdateBudget(Budget budget) {

            budgetRepository.findByCategory(budget.getCategory());

        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(String category) {

            budgetRepository.findByCategory(category).remove(category);

        }
    }

    // ... autres méthodes

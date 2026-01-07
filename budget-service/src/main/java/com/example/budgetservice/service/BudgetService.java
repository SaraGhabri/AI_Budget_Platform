/*package com.example.budgetservice.service;

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
*/

package com.example.budgetservice.service;

import com.example.budgetservice.dto.BudgetSummary;
import com.example.budgetservice.entity.Budget;
import com.example.budgetservice.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;

    // =========== CRUD Operations ===========

    /**
     * Get all budgets (returns all budgets in system)
     */
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    /**
     * Get budgets by user ID
     */
    public List<Budget> getBudgetsByUserId(String userId) {
        return budgetRepository.findByUserId(userId);
    }

    /**
     * Get budget by category (returns first match)
     */
    public Budget getBudgetByCategory(String category) {
        return budgetRepository.findByCategory(category)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Get budget by category and user ID
     */
    public Budget getBudgetByCategoryAndUserId(String category, String userId) {
        return budgetRepository.findByCategory(category)
                .stream()
                .filter(b -> b.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Create or update budget
     */
    @Transactional
    public Budget createOrUpdateBudget(Budget budget) {
        // Validate required fields
        if (budget.getCategory() == null || budget.getMonthlyLimit() == null || budget.getUserId() == null) {
            throw new IllegalArgumentException("Missing required fields: category, monthlyLimit, or userId");
        }

        // Check if budget already exists for this category and user
        Optional<Budget> existingBudget = budgetRepository.findByCategory(budget.getCategory())
                .stream()
                .filter(b -> b.getUserId().equals(budget.getUserId()))
                .findFirst();

        if (existingBudget.isPresent()) {
            // Update existing budget
            Budget existing = existingBudget.get();
            existing.setMonthlyLimit(budget.getMonthlyLimit());
            if (budget.getCurrentSpent() != null) {
                existing.setCurrentSpent(budget.getCurrentSpent());
            }
            log.info("Updating budget: category={}, userId={}", budget.getCategory(), budget.getUserId());
            return budgetRepository.save(existing);
        } else {
            // Create new budget
            if (budget.getCurrentSpent() == null) {
                budget.setCurrentSpent(BigDecimal.ZERO);
            }
            log.info("Creating new budget: category={}, userId={}", budget.getCategory(), budget.getUserId());
            return budgetRepository.save(budget);
        }
    }

    /**
     * Delete budget by category (deletes all budgets with this category)
     */
    @Transactional
    public void deleteBudget(String category) {
        List<Budget> budgets = budgetRepository.findByCategory(category);
        if (budgets.isEmpty()) {
            log.warn("No budgets found for category: {}", category);
            return;
        }
        budgetRepository.deleteAll(budgets);
        log.info("Deleted {} budget(s) for category: {}", budgets.size(), category);
    }

    /**
     * Delete budget by ID
     */
    @Transactional
    public void deleteBudgetById(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new IllegalArgumentException("Budget with ID " + id + " not found");
        }
        budgetRepository.deleteById(id);
        log.info("Deleted budget with ID: {}", id);
    }

    // =========== Budget Validation & Integration ===========

    /**
     * ✅ NEW: Check if expense would exceed budget
     * Called by expense-service via Feign
     */
    public String checkBudgetLimit(String category, Double amount, String userId) {
        log.info("Checking budget: category={}, amount={}, userId={}", category, amount, userId);

        // Find budget for this category and user
        Optional<Budget> budgetOpt = budgetRepository.findByCategory(category)
                .stream()
                .filter(b -> b.getUserId().equals(userId))
                .findFirst();

        if (budgetOpt.isEmpty()) {
            log.warn("No budget found for category: {} and user: {}", category, userId);
            return "No budget set for category: " + category;
        }

        Budget budget = budgetOpt.get();
        BigDecimal expenseAmount = BigDecimal.valueOf(amount);

        // Check if expense would exceed budget
        if (budget.exceedsBudget(expenseAmount)) {
            BigDecimal remaining = budget.getMonthlyLimit().subtract(budget.getCurrentSpent());
            BigDecimal overage = expenseAmount.subtract(remaining);
            String message = String.format("WARNING: This expense exceeds your budget by $%.2f", overage.doubleValue());
            log.warn(message);
            return message;
        }

        // Calculate remaining budget after expense
        BigDecimal remaining = budget.getMonthlyLimit()
                .subtract(budget.getCurrentSpent())
                .subtract(expenseAmount);
        String message = String.format("OK: Budget check passed. Remaining budget: $%.2f", remaining.doubleValue());
        log.info(message);
        return message;
    }

    /**
     * ✅ NEW: Update current spent amount
     * Called when an expense is created or deleted
     */
    @Transactional
    public Budget updateCurrentSpent(String category, Double amount, String userId) {
        log.info("Updating spent amount: category={}, amount={}, userId={}", category, amount, userId);

        Optional<Budget> budgetOpt = budgetRepository.findByCategory(category)
                .stream()
                .filter(b -> b.getUserId().equals(userId))
                .findFirst();

        if (budgetOpt.isEmpty()) {
            log.warn("No budget found for category: {} and user: {}", category, userId);
            return null;
        }

        Budget budget = budgetOpt.get();
        BigDecimal currentSpent = budget.getCurrentSpent();
        BigDecimal newSpent = currentSpent.add(BigDecimal.valueOf(amount));

        // Prevent negative spent amounts
        if (newSpent.compareTo(BigDecimal.ZERO) < 0) {
            newSpent = BigDecimal.ZERO;
        }

        budget.setCurrentSpent(newSpent);
        Budget savedBudget = budgetRepository.save(budget);
        log.info("Updated spent amount: category={}, newSpent={}", category, newSpent);
        return savedBudget;
    }

    /**
     * ✅ NEW: Get budget summary with calculations
     */
    public BudgetSummary getBudgetSummary(String category, String userId) {
        Optional<Budget> budgetOpt = budgetRepository.findByCategory(category)
                .stream()
                .filter(b -> b.getUserId().equals(userId))
                .findFirst();

        if (budgetOpt.isEmpty()) {
            return null;
        }

        Budget budget = budgetOpt.get();
        return new BudgetSummary(
                budget.getCategory(),
                budget.getMonthlyLimit().doubleValue(),
                budget.getCurrentSpent().doubleValue()
        );
    }

    /**
     * ✅ NEW: Reset all budgets for a user (for new month)
     */
    @Transactional
    public void resetBudgetsForUser(String userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        for (Budget budget : budgets) {
            budget.setCurrentSpent(BigDecimal.ZERO);
        }
        budgetRepository.saveAll(budgets);
        log.info("Reset {} budgets for user: {}", budgets.size(), userId);
    }

    /**
     * ✅ NEW: Reset all budgets in the system (for new month)
     */
    @Transactional
    public void resetAllBudgets() {
        List<Budget> budgets = budgetRepository.findAll();
        for (Budget budget : budgets) {
            budget.setCurrentSpent(BigDecimal.ZERO);
        }
        budgetRepository.saveAll(budgets);
        log.info("Reset all {} budgets in the system", budgets.size());
    }
}
/*package com.example.budgetservice.controller;

import com.example.budgetservice.entity.Budget;
import com.example.budgetservice.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets()
             {

        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/{category}")
    public ResponseEntity<Budget> getBudgetByCategory(
            @PathVariable String category ) {


        Budget budget = budgetService.getBudgetByCategory(category);

        return budget != null ?
                ResponseEntity.ok(budget) :
                ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Budget> createOrUpdateBudget(
            @RequestBody Budget budget) {


        return ResponseEntity.ok(budgetService.createOrUpdateBudget(budget));
    }

    @DeleteMapping("/{category}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable String category) {


        budgetService.deleteBudget(category);
        return ResponseEntity.noContent().build();
    }

    private String extractPrimaryRole(String userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            return "USER";
        }
        String[] roles = userRoles.split(",");
        return roles[0].trim();
    }
}*/

package com.example.budgetservice.controller;

import com.example.budgetservice.dto.BudgetSummary;
import com.example.budgetservice.entity.Budget;
import com.example.budgetservice.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * GET all budgets
     * Returns all budgets in the system
     */
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    /**
     * GET budget by category
     * Returns first budget matching the category
     */
    @GetMapping("/{category}")
    public ResponseEntity<Budget> getBudgetByCategory(@PathVariable String category) {
        Budget budget = budgetService.getBudgetByCategory(category);
        return budget != null ?
                ResponseEntity.ok(budget) :
                ResponseEntity.notFound().build();
    }

    /**
     * GET budgets by user ID
     * Returns all budgets for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable String userId) {
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }

    /**
     * POST create or update budget
     * Creates new budget or updates existing one
     */
    @PostMapping
    public ResponseEntity<Budget> createOrUpdateBudget(@RequestBody Budget budget) {
        // Validate required fields
        if (budget.getCategory() == null || budget.getMonthlyLimit() == null || budget.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(budgetService.createOrUpdateBudget(budget));
    }

    /**
     * DELETE budget by category
     * Deletes all budgets matching the category
     */
    @DeleteMapping("/{category}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String category) {
        budgetService.deleteBudget(category);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE budget by ID
     * Deletes a specific budget by its ID
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteBudgetById(@PathVariable Long id) {
        budgetService.deleteBudgetById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ NEW: CHECK BUDGET ENDPOINT
     * Called by expense-service via Feign to validate expenses
     * Returns budget status message
     */
    @GetMapping("/{category}/check")
    public ResponseEntity<String> checkBudget(
            @PathVariable String category,
            @RequestParam Double amount,
            @RequestHeader("X-User-Id") String userId) {

        String result = budgetService.checkBudgetLimit(category, amount, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * ✅ NEW: UPDATE CURRENT SPENT
     * Updates the current spent amount for a budget
     * Called when an expense is created/deleted
     */
    @PutMapping("/{category}/spent")
    public ResponseEntity<Budget> updateCurrentSpent(
            @PathVariable String category,
            @RequestParam Double amount,
            @RequestHeader("X-User-Id") String userId) {

        Budget budget = budgetService.updateCurrentSpent(category, amount, userId);
        return budget != null ?
                ResponseEntity.ok(budget) :
                ResponseEntity.notFound().build();
    }

    /**
     * ✅ NEW: GET BUDGET SUMMARY
     * Returns budget summary with remaining amount
     */
    @GetMapping("/{category}/summary")
    public ResponseEntity<BudgetSummary> getBudgetSummary(
            @PathVariable String category,
            @RequestHeader("X-User-Id") String userId) {

        BudgetSummary summary = budgetService.getBudgetSummary(category, userId);
        return summary != null ?
                ResponseEntity.ok(summary) :
                ResponseEntity.notFound().build();
    }
}


package com.example.budgetservice.controller;

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
}
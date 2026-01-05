package com.example.expenseservice.feign;

import org.springframework.stereotype.Component;

@Component
public class BudgetServiceFallback implements BudgetServiceClient {

    @Override
    public String checkBudget(String category, Double amount, String userId) {
        // Retourne un message indiquant que le service est indisponible
        return "Budget service unavailable. Expense recorded without budget validation.";
    }
}
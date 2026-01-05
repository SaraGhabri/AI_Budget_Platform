package com.example.expenseservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "budget-service", fallback = BudgetServiceFallback.class)
public interface BudgetServiceClient {

    @GetMapping("/api/budgets/{category}/check")
    String checkBudget(
            @PathVariable("category") String category,
            @RequestParam("amount") Double amount,
            @RequestHeader("X-User-Id") String userId  // GARDE ÇA!
    );
}
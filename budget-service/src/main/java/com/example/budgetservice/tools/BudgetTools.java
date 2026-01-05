package com.example.budgetservice.tools;


import com.example.budgetservice.entity.Budget;
import com.example.budgetservice.service.BudgetService;
import lombok.AllArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class BudgetTools {


    private final BudgetService budgetService;

    @McpTool(
            name = "get_all_budgets",
            description = "Get all budgets (ADMIN sees all, USER sees only their own)"
    )
    public List<Budget> getAllBudgets()
    {
        return budgetService.getAllBudgets();
    }
}

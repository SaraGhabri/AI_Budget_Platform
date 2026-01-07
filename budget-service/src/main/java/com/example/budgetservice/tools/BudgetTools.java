package com.example.budgetservice.tools;


/*import com.example.budgetservice.entity.Budget;
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
}*/
import com.example.budgetservice.dto.BudgetSummary;
import com.example.budgetservice.entity.Budget;
import com.example.budgetservice.service.BudgetService;
import lombok.AllArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class BudgetTools {

    private final BudgetService budgetService;

    /**
     * MCP Tool: Get all budgets
     */
    @McpTool(
            name = "get_all_budgets",
            description = "Get all budgets in the system"
    )
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    /**
     * MCP Tool: Get budgets by user
     */
    @McpTool(
            name = "get_budgets_by_user",
            description = "Get all budgets for a specific user"
    )
    public List<Budget> getBudgetsByUser(
            @ToolParam(description = "The user's email or ID")
            String userId) {
        return budgetService.getBudgetsByUserId(userId);
    }

    /**
     * MCP Tool: Get budget by category
     */
    @McpTool(
            name = "get_budget_by_category",
            description = "Get budget information for a specific category"
    )
    public Budget getBudgetByCategory(
            @ToolParam(description = "The budget category (e.g., groceries, transport)")
            String category) {
        return budgetService.getBudgetByCategory(category);
    }

    /**
     * MCP Tool: Get budget summary
     */
    @McpTool(
            name = "get_budget_summary",
            description = "Get detailed budget summary with remaining amount and percentage used"
    )
    public BudgetSummary getBudgetSummary(
            @ToolParam( description = "The budget category")
            String category,
            @ToolParam(description = "The user's email or ID")
            String userId) {
        return budgetService.getBudgetSummary(category, userId);
    }

    /**
     * MCP Tool: Check if expense exceeds budget
     */
    @McpTool(
            name = "check_budget_limit",
            description = "Check if a potential expense would exceed the budget limit"
    )
    public String checkBudgetLimit(
            @ToolParam(description = "The expense category")
            String category,
            @ToolParam(description = "The expense amount")
            Double amount,
            @ToolParam(description = "The user's email or ID")
            String userId) {
        return budgetService.checkBudgetLimit(category, amount, userId);
    }
}

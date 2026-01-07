/*package com.example.expenseservice.tools;

import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ExpenseTools {


    private final ExpenseService expenseService;

    @McpTool(
            name = "get_all_expenses",
            description = "Get all expenses (ADMIN sees all, USER sees only their own)"
    )
    public List<Expense> getAllExpensesTool()
             {
        return expenseService.getAllExpenses();
    }
}*/

package com.example.expenseservice.tools;

import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class ExpenseTools {

    private final ExpenseService expenseService;

    /**
     * MCP Tool: Get all expenses
     */
    @McpTool(
            name = "get_all_expenses",
            description = "Get all expenses from the system"
    )
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    /**
     * MCP Tool: Get expenses by category
     */
    @McpTool(
            name = "get_expenses_by_category",
            description = "Get all expenses for a specific category"
    )
    public List<Expense> getExpensesByCategory(
            @ToolParam(description = "The expense category (e.g., groceries, transport)")
            String category) {
        return expenseService.getExpensesByCategory(category);
    }

    /**
     * MCP Tool: Get expenses by user
     */
    @McpTool(
            name = "get_expenses_by_user",
            description = "Get all expenses for a specific user"
    )
    public List<Expense> getExpensesByUser(
            @ToolParam(description = "The user's email or ID")
            String userId) {
        return expenseService.getExpensesByUserId(userId);
    }

    /**
     * MCP Tool: Get expenses by date range
     */
    @McpTool(
            name = "get_expenses_by_date_range",
            description = "Get all expenses within a date range"
    )
    public List<Expense> getExpensesByDateRange(
            @ToolParam(description = "Start date in YYYY-MM-DD format")
            String startDate,
            @ToolParam(description = "End date in YYYY-MM-DD format")
            String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return expenseService.getExpensesByDateRange(start, end);
    }
}

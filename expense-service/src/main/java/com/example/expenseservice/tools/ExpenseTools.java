package com.example.expenseservice.tools;

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
}

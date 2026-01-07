/*package com.example.expenseservice.controller;

import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());

    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(
            @PathVariable Long id) {


        Expense expense;
        expense = expenseService.getExpenseById(id);


        return expense != null ?
                ResponseEntity.ok(expense) :
                ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(
            @PathVariable String category) {

        List<Expense> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(
            @RequestBody Expense expense) {

        // USER ne peut créer que pour lui-même
        return ResponseEntity.ok(expenseService.createExpense(expense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id) {


        expenseService.deleteExpense(id);
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

package com.example.expenseservice.controller;

import com.example.expenseservice.entity.Expense;
import com.example.expenseservice.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // ✅ GET ALL EXPENSES
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    // ✅ GET EXPENSE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return expense != null ?
                ResponseEntity.ok(expense) :
                ResponseEntity.notFound().build();
    }

    // ✅ GET EXPENSES BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable String category) {
        List<Expense> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(expenses);
    }

    // ✅ CREATE EXPENSE
    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.createExpense(expense));
    }

    // ✅ DELETE EXPENSE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}

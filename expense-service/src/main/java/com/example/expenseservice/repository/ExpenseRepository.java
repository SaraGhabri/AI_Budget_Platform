/*package com.example.expenseservice.repository;

import com.example.expenseservice.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Remove this method since JpaRepository already has findAll()
    // List<Expense> getAllExpenses();



    // POUR ADMIN: trouve TOUTES les expenses d'une catégorie (tous les users)
    List<Expense> findByCategory(String category);

    // This method is not used in your current service but you can keep it if needed elsewhere
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
}*/
package com.example.expenseservice.repository;

import com.example.expenseservice.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // ✅ Find expenses by user ID
    List<Expense> findByUserId(String userId);

    // ✅ Find expenses by category (all users - for ADMIN)
    List<Expense> findByCategory(String category);

    // ✅ Find expenses by category and user (for USER)
    List<Expense> findByCategoryAndUserId(String category, String userId);

    // ✅ Find expenses by date range
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);

    // ✅ Find expenses by date range and user
    List<Expense> findByDateBetweenAndUserId(LocalDate start, LocalDate end, String userId);
}
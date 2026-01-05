package com.example.budgetservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyLimit;

    @Column(precision = 10, scale = 2)
    private BigDecimal currentSpent = BigDecimal.ZERO;

    @Column(nullable = false)
    private String userId;

    // Méthode pour vérifier si un montant dépasse le budget
    public boolean exceedsBudget(BigDecimal amount) {
        return currentSpent.add(amount).compareTo(monthlyLimit) > 0;
    }

    // Méthode pour ajouter une dépense
    public void addExpense(BigDecimal amount) {
        this.currentSpent = this.currentSpent.add(amount);
    }
}
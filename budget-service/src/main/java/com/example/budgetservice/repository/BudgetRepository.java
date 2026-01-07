    /*package com.example.budgetservice.repository;

    import com.example.budgetservice.entity.Budget;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface BudgetRepository extends JpaRepository<Budget, Long> {

        // Pour USER: trouve ses budgets
        List<Budget> findByUserId(String userId);


        // Pour vérifier l'existence
        boolean existsByCategory( String category);

        // POUR ADMIN: trouve TOUS les budgets d'une catégorie (tous les users)
        List<Budget> findByCategory(String category);
    }*/

    package com.example.budgetservice.repository;

    import com.example.budgetservice.entity.Budget;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.List;

    @Repository
    public interface BudgetRepository extends JpaRepository<Budget, Long> {

        // Find all budgets for a specific user
        List<Budget> findByUserId(String userId);

        // Check if budget exists for a category
        boolean existsByCategory(String category);

        // Find all budgets in a category (across all users)
        List<Budget> findByCategory(String category);

        // Find budget by category and user (combined)
        List<Budget> findByCategoryAndUserId(String category, String userId);
    }
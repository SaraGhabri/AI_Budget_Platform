package com.example.budgetservice.dto;

public class BudgetSummary {
    private String category;
    private Double monthlyLimit;
    private Double currentSpent;
    private Double remaining;
    private Double percentageUsed;
    private boolean isExceeded;

    public BudgetSummary(String category, Double monthlyLimit, Double currentSpent) {
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.currentSpent = currentSpent;
        this.remaining = monthlyLimit - currentSpent;
        this.percentageUsed = (currentSpent / monthlyLimit) * 100;
        this.isExceeded = currentSpent > monthlyLimit;
    }

    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(Double monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public Double getCurrentSpent() { return currentSpent; }
    public void setCurrentSpent(Double currentSpent) { this.currentSpent = currentSpent; }

    public Double getRemaining() { return remaining; }
    public void setRemaining(Double remaining) { this.remaining = remaining; }

    public Double getPercentageUsed() { return percentageUsed; }
    public void setPercentageUsed(Double percentageUsed) { this.percentageUsed = percentageUsed; }

    public boolean isExceeded() { return isExceeded; }
    public void setExceeded(boolean exceeded) { isExceeded = exceeded; }

}

package model;

import java.io.Serializable;

public class Investment implements Serializable {
    // Add this explicit serialVersionUID (MUST be same on client and server)
    private static final long serialVersionUID = 1L;  // Fixed version number
    
    private String symbol;
    private String type;
    private double quantity;
    private double buyPrice;
    private transient double currentPrice; // Not persisted in DB or serialized

    public Investment() {}

    public Investment(String symbol, String type, double quantity, double buyPrice) {
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
    }

    // Getters
    public String getSymbol() { return symbol; }
    public String getType() { return type; }
    public double getQuantity() { return quantity; }
    public double getBuyPrice() { return buyPrice; }
    
    // Current price methods
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double price) { this.currentPrice = price; }
    
    // Calculated values
    public double getCurrentValue() {
        return quantity * currentPrice;
    }
    
    public double getProfitLoss() {
        return getCurrentValue() - (quantity * buyPrice);
    }
    
    public double getProfitLossPercentage() {
        double costBasis = quantity * buyPrice;
        return costBasis != 0 ? (getProfitLoss() / costBasis) * 100 : 0;
    }

    // Setters
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setType(String type) { this.type = type; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }

    @Override
    public String toString() {
        return String.format("%s (%s) - Qty: %.4f, Buy: $%,.2f", 
                symbol, type, quantity, buyPrice);
    }
}
package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Investment;
import rmi.PortfolioService;
import util.RMIClient;
import java.rmi.RemoteException;
import java.util.List;

public class PortfolioGUI {
    private PortfolioService service = RMIClient.getService();
    private VBox assetsContainer;
    
    public void start(Stage stage) {
        // Header
        Label header = new Label("Remote Portfolio Tracker");
        header.getStyleClass().add("header");
        
        // Form Elements
        TextField symbolField = new TextField();
        symbolField.setPromptText("e.g. BTC, AAPL");
        
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Crypto", "Stock", "NFT");
        typeBox.setPromptText("Select type");
        
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        TextField buyPriceField = new TextField();
        buyPriceField.setPromptText("Buy price ($)");
        
        Button addButton = new Button("Add Investment");
        Button refreshButton = new Button("Refresh Prices");
        
        // Form Layout
        GridPane form = new GridPane();
        form.getStyleClass().add("card");
        form.setVgap(15);
        form.setHgap(15);
        form.setPadding(new Insets(20));
        form.addRow(0, new Label("Symbol:"), symbolField);
        form.addRow(1, new Label("Type:"), typeBox);
        form.addRow(2, new Label("Quantity:"), quantityField);
        form.addRow(3, new Label("Buy Price:"), buyPriceField);
        form.add(addButton, 1, 4);
        
        // Summary Labels
        Label totalValueLabel = new Label("Total Value: $0.00");
        Label totalProfitLabel = new Label("Total Profit: $0.00");
        totalValueLabel.getStyleClass().add("summary-label");
        totalProfitLabel.getStyleClass().add("summary-label");
        
        // Profit/loss styling
        totalProfitLabel.textProperty().addListener((obs, oldVal, newVal) -> {
            totalProfitLabel.getStyleClass().removeAll("positive", "negative");
            totalProfitLabel.getStyleClass().add(newVal.contains("-") ? "negative" : "positive");
        });
        
        VBox summaryBox = new VBox(15, totalValueLabel, totalProfitLabel);
        summaryBox.getStyleClass().add("card");
        summaryBox.setPadding(new Insets(20));
        summaryBox.setAlignment(Pos.CENTER);
        
        // Assets Container with scroll
        assetsContainer = new VBox(15);
        assetsContainer.getStyleClass().add("card");
        assetsContainer.setPadding(new Insets(20));
        
        ScrollPane scrollPane = new ScrollPane(assetsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Right Pane
        VBox rightPane = new VBox(20, summaryBox, refreshButton, scrollPane);
        rightPane.setPadding(new Insets(0, 0, 0, 20));
        
        // Main Layout
        HBox layout = new HBox(30, form, rightPane);
        layout.setPadding(new Insets(0, 20, 20, 20));
        
        VBox mainLayout = new VBox(header, layout);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        // Add Button Logic
        addButton.setOnAction(e -> handleAddInvestment(
            symbolField, typeBox, quantityField, buyPriceField, 
            totalValueLabel, totalProfitLabel
        ));
        
        // Refresh Button Logic
        refreshButton.setOnAction(e -> {
            updateSummary(totalValueLabel, totalProfitLabel);
            updateAssetCards();
        });
        
        // Initial load
        refreshButton.fire(); // Trigger initial refresh
        stage.setScene(new Scene(mainLayout, 1000, 700));
        stage.setTitle("Remote Portfolio Tracker");
        stage.show();
    }
    
    private void handleAddInvestment(TextField symbolField, ComboBox<String> typeBox,
                                   TextField quantityField, TextField buyPriceField,
                                   Label totalValueLabel, Label totalProfitLabel) {
        try {
            String symbol = symbolField.getText().trim().toUpperCase();
            String type = typeBox.getValue();
            double qty = Double.parseDouble(quantityField.getText().trim());
            double price = Double.parseDouble(buyPriceField.getText().trim());
            
            if (symbol.isEmpty() || type == null) {
                showError("Please fill all fields");
                return;
            }
            
            Investment inv = new Investment(symbol, type, qty, price);
            service.addInvestment(inv);
            
            symbolField.clear();
            quantityField.clear();
            buyPriceField.clear();
            typeBox.setValue(null);
            
            updateSummary(totalValueLabel, totalProfitLabel);
            updateAssetCards();
            
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers for quantity and price");
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void updateAssetCards() {
        assetsContainer.getChildren().clear();
        
        try {
            List<Investment> investments = service.getAllInvestments();
            double totalValue = service.getTotalValue();
            
            if (investments == null || investments.isEmpty()) {
                Label empty = new Label("No investments yet");
                empty.getStyleClass().add("empty-label");
                assetsContainer.getChildren().add(empty);
                return;
            }
            
            for (Investment inv : investments) {
                HBox card = createAssetCard(inv, totalValue);
                assetsContainer.getChildren().add(card);
            }
        } catch (RemoteException e) {
            Label error = new Label("Failed to load assets: " + e.getMessage());
            error.getStyleClass().add("error-label");
            assetsContainer.getChildren().add(error);
        }
    }
    
    private HBox createAssetCard(Investment inv, double totalValue) {
        double currentValue = inv.getCurrentValue();
        double percentage = (currentValue / totalValue) * 100;
        double profitLoss = inv.getProfitLoss();
        double profitLossPct = inv.getProfitLossPercentage();
        
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("asset-card");
        
        // Asset icon
        Label icon = new Label(inv.getSymbol().substring(0, 1));
        icon.getStyleClass().add("asset-icon");
        
        VBox infoBox = new VBox(5);
        
        // Top row - Symbol and value
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label symbol = new Label(inv.getSymbol());
        symbol.getStyleClass().add("asset-symbol");
        Label valueLabel = new Label(String.format("$%,.2f", currentValue));
        valueLabel.getStyleClass().add("asset-value");
        topRow.getChildren().addAll(symbol, valueLabel);
        
        // Progress bar for allocation
        ProgressBar progressBar = new ProgressBar(percentage/100);
        progressBar.getStyleClass().add("asset-progress");
        
        // Middle row - Profit/Loss
        HBox middleRow = new HBox(10);
        middleRow.setAlignment(Pos.CENTER_LEFT);
        Label plLabel = new Label(String.format("%s$%,.2f (%s%.1f%%)", 
            profitLoss >= 0 ? "+" : "", 
            profitLoss,
            profitLossPct >= 0 ? "+" : "",
            profitLossPct));
        plLabel.getStyleClass().add(profitLoss >= 0 ? "positive" : "negative");
        middleRow.getChildren().add(plLabel);
        
        // Bottom row - Quantity and allocation
        HBox bottomRow = new HBox(10);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label qtyLabel = new Label(String.format("%.4f %s", inv.getQuantity(), inv.getType()));
        qtyLabel.getStyleClass().add("asset-meta");
        Label allocLabel = new Label(String.format("%.1f%% of portfolio", percentage));
        allocLabel.getStyleClass().add("asset-meta");
        bottomRow.getChildren().addAll(qtyLabel, allocLabel);
        
        infoBox.getChildren().addAll(topRow, middleRow, progressBar, bottomRow);
        card.getChildren().addAll(icon, infoBox);
        
        return card;
    }
    
    private void updateSummary(Label valueLabel, Label profitLabel) {
        try {
            double value = service.getTotalValue();
            double profit = service.getTotalProfit();
            valueLabel.setText(String.format("Total Value: $%,.2f", value));
            profitLabel.setText(String.format("Total Profit: $%,.2f", profit));
        } catch (RemoteException e) {
            showError("Failed to fetch summary: " + e.getMessage());
        }
    }
    
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
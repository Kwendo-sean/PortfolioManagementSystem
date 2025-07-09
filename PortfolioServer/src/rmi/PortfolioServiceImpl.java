package rmi;

import database.DBOperations;
import model.Investment;
import service.PriceFetcher;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PortfolioServiceImpl extends UnicastRemoteObject implements PortfolioService {
    private DBOperations dbOperations;
    
    public PortfolioServiceImpl() throws RemoteException {
        super();
        this.dbOperations = new DBOperations();
    }

    @Override
    public void addInvestment(Investment inv) throws RemoteException {
        try {
            dbOperations.addInvestment(inv);
        } catch (Exception e) {
            throw new RemoteException("Database error", e);
        }
    }

    @Override
    public List<Investment> getAllInvestments() throws RemoteException {
        try {
            List<Investment> investments = dbOperations.getAllInvestments();
            // Fetch current prices for all investments
            for (Investment inv : investments) {
                double currentPrice = PriceFetcher.getLivePrice(inv.getSymbol(), inv.getType());
                inv.setCurrentPrice(currentPrice);
            }
            return investments;
        } catch (Exception e) {
            throw new RemoteException("Database error", e);
        }
    }

    @Override
    public double getTotalValue() throws RemoteException {
        try {
            return getAllInvestments().stream()
                .mapToDouble(Investment::getCurrentValue)
                .sum();
        } catch (Exception e) {
            throw new RemoteException("Calculation error", e);
        }
    }

    @Override
    public double getTotalProfit() throws RemoteException {
        try {
            return getAllInvestments().stream()
                .mapToDouble(Investment::getProfitLoss)
                .sum();
        } catch (Exception e) {
            throw new RemoteException("Calculation error", e);
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmi;
import model.Investment;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PortfolioService extends Remote{
    void addInvestment(Investment inv ) throws RemoteException;
    List<Investment> getAllInvestments() throws RemoteException;
    double getTotalValue() throws RemoteException;
    double getTotalProfit() throws RemoteException;
}

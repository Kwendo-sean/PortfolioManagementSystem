/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import rmi.PortfolioService;
import java.rmi.Naming;
public class RMIClient {
    public static PortfolioService getService() {
        try{
            return (PortfolioService) Naming.lookup("rmi://10.51.26.51:1098/portfolio");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

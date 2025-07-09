import rmi.PortfolioService;
import rmi.PortfolioServiceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MainServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1098);
            PortfolioService service = new PortfolioServiceImpl();
            System.setProperty("java.rmi.server.hostname", " 10.51.26.51:1098");
            Naming.rebind("rmi://10.51.26.51:1098/portfolio", service);

            System.out.println("Portfolio Server Started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

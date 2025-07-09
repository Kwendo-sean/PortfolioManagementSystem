package gui;

import gui.PortfolioGUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainClient extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        new PortfolioGUI().start(stage);
    }
}

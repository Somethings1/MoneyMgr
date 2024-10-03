package gui.general;

import java.net.MalformedURLException;

import gui.tabs.account.AccountTab;
import gui.tabs.home.HomeTab;
import gui.tabs.report.ReportTab;
import gui.tabs.settings.SettingsTab;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {

    @FXML
    private TabPane tabPane;

    public void initialize() throws MalformedURLException {
        // Add custom tabs to the TabPane
        tabPane.getTabs().add(new HomeTab());
        tabPane.getTabs().add(new ReportTab());
        tabPane.getTabs().add(new AccountTab());
        tabPane.getTabs().add(new SettingsTab());
    }
}
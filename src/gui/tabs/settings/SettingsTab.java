package gui.tabs.settings;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SettingsTab extends Tab {

    public SettingsTab() {
        super("Settings");
        setContent(createLayout());
        this.setClosable(false);
    }

    private VBox createLayout() {
        VBox layout = new VBox(10);
        layout.getChildren().addAll(createButtons());
        return layout;
    }

    private Button[] createButtons() {
        return new Button[]{
            createButton("Income Category Settings", e -> openCategorySettings("Income")),
            createButton("Expense Category Settings", e -> openCategorySettings("Expense")),
            createButton("Account Settings", e -> openAccountSettings()),
            createButton("App Settings", e -> openAppSettings())
        };
    }

    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        return button;
    }

    private void openCategorySettings(String type) {
        new CategorySettings(type).show();
    }

    private void openAccountSettings() {
        new AccountSettings().show();
    }

    private void openAppSettings() {
        // Implement app settings logic here if needed
    }
}
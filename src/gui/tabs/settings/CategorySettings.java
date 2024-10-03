package gui.tabs.settings;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import app.App;
import transaction.properties.Category;

import java.util.List;

import javafx.scene.layout.HBox;

public class CategorySettings {
	App app;
    private String type;
    private Stage categoryStage;
    private List<Category> categories;
    private ListView<HBox> categoryListView;
    private VBox layout;

    public CategorySettings(String type) {
        this.type = type;
        this.app = App.getInstance();
    }

    public void show() {
        categoryStage = createStage(type + " Categories");
        categories = getCategories();
        categoryListView = createCategoryListView();
        layout = createCategorySettingsLayout();
        categoryStage.setScene(new Scene(layout, 300, 400));
        categoryStage.show();
    }

    private List<Category> getCategories() {
        return type.equals("Income") ? app.getIncomeCategories() : app.getExpenseCategories();
    }

    private ListView<HBox> createCategoryListView() {
        ListView<HBox> categoryListView = new ListView<>();
        for (Category category : categories) {
            HBox categoryBox = createCategoryHBox(category);
            categoryListView.getItems().add(categoryBox);
        }
        return categoryListView;
    }

    private HBox createCategoryHBox(Category category) {
        HBox hbox = new HBox(10);
        Label emojiLabel = new Label(category.getEmoji());
        Label nameLabel = new Label(category.getName());
        Label budgetLabel = new Label("Budget: " + category.getBudget());
        Button removeButton = createRemoveButton(category);

        hbox.getChildren().addAll(emojiLabel, nameLabel, budgetLabel, removeButton);
        return hbox;
    }

    private Button createRemoveButton(Category category) {
        Button removeButton = new Button("x");
        removeButton.setOnAction(e -> removeCategory(category));
        return removeButton;
    }

    private void removeCategory(Category category) {
        if (type.equals("Income")) {
            App.getInstance().removeIncomeCategory(category);
        } else {
            App.getInstance().removeExpenseCategory(category);
        }
        categoryListView.getItems().removeIf(hbox -> {
            Label nameLabel = (Label) ((HBox) hbox).getChildren().get(1);
            return nameLabel.getText().equals(category.getName());
        });
    }

    private VBox createCategorySettingsLayout() {
        Button addButton = createAddCategoryButton();
        return new VBox(10, categoryListView, addButton);
    }

    private Button createAddCategoryButton() {
        Button addButton = new Button("Add " + type + " Category");
        addButton.setOnAction(e -> openAddCategoryWindow());
        return addButton;
    }

    private void openAddCategoryWindow() {
        Stage addCategoryStage = createStage("Add " + type + " Category");
        TextField nameField = createTextField("Category Name");
        TextField budgetField = createTextField("Budget");
        Button saveButton = createSaveCategoryButton(nameField, budgetField, addCategoryStage);
        VBox layout = new VBox(10, nameField, budgetField, saveButton);
        addCategoryStage.setScene(new Scene(layout, 300, 200));
        addCategoryStage.show();
    }

    private Button createSaveCategoryButton(TextField nameField, TextField budgetField, Stage addCategoryStage) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveCategory(nameField, budgetField, addCategoryStage));
        return saveButton;
    }

    private void saveCategory(TextField nameField, TextField budgetField, Stage addCategoryStage) {
        String name = nameField.getText();
        try {
            int budget = Integer.parseInt(budgetField.getText());
            Category newCategory = new Category("💰", name, budget);
            if (type.equals("Income")) {
                App.getInstance().addIncomeCategory(newCategory);
            } else {
                App.getInstance().addExpenseCategory(newCategory);
            }
            HBox categoryBox = createCategoryHBox(newCategory);
            categoryListView.getItems().add(categoryBox);  // Refresh the list
            addCategoryStage.close(); // Close the add window
            nameField.clear();
            budgetField.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid budget", "Please enter a valid number for the budget.");
        }
    }

    private Stage createStage(String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        return stage;
    }

    private TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        return textField;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



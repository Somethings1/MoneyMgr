package gui.general;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import transaction.core.Income;
import transaction.core.Expense;
import transaction.core.Transfer;
import transaction.properties.Account;
import transaction.properties.Category;
import app.App;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddTransactionView {
    private App app;

    // UI component properties
    private ChoiceBox<String> typeChoiceBox;
    private DatePicker datePicker;
    private TextField amountField;
    private ChoiceBox<String> sourceChoiceBox;
    private ChoiceBox<String> categoryChoiceBox;
    private ChoiceBox<String> destinationChoiceBox; // For transfer
    private TextField noteField;

    // Labels for category and destination
    private Label categoryLabel;
    private Label destinationLabel;
    
    private Runnable onTransactionSaved;
    
    public void setOnTransactionSaved (Runnable onTransactionSaved) {
    	this.onTransactionSaved = onTransactionSaved;
    }

    public AddTransactionView() {
        this.app = App.getInstance();
    }

    public void display() {
        Stage addTransactionStage = createStage();
        GridPane grid = createGridPane();

        initializeComponents(grid);
        createButtons(grid, addTransactionStage);

        addTransactionStage.setScene(new Scene(grid));
        addTransactionStage.show();
    }

    private Stage createStage() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Transaction");
        return stage;
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);
        return grid;
    }

    private void initializeComponents(GridPane grid) {
        createTransactionTypeChoiceBox(grid);
        createDatePicker(grid);
        createAmountField(grid);
        createSourceChoiceBox(grid);
        createCategoryChoiceBox(grid);
        createNoteField(grid);
        createDestinationChoiceBox(grid); // For transfer

        typeChoiceBox.setOnAction(e -> updateChoiceBoxes());
    }

    private void createTransactionTypeChoiceBox(GridPane grid) {
        typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll("Income", "Expense", "Transfer");
        typeChoiceBox.setValue("Income");
        addLabelAndControl(grid, "Transaction Type:", typeChoiceBox, 0);
    }

    private void createDatePicker(GridPane grid) {
        datePicker = new DatePicker(LocalDate.now());
        addLabelAndControl(grid, "Date:", datePicker, 1);
    }

    private void createAmountField(GridPane grid) {
        amountField = new TextField();
        addLabelAndControl(grid, "Amount:", amountField, 2);
    }

    private void createSourceChoiceBox(GridPane grid) {
        sourceChoiceBox = new ChoiceBox<>();
        sourceChoiceBox.setItems(FXCollections.observableArrayList(app.getAccounts().stream()
                .map(Account::getName).toArray(String[]::new)));
        addLabelAndControl(grid, "Source Account:", sourceChoiceBox, 3);
    }

    private void createCategoryChoiceBox(GridPane grid) {
        categoryChoiceBox = new ChoiceBox<>();
        updateCategoryChoiceBox();
        categoryLabel = new Label("Category:");
        addLabelAndControl(grid, categoryLabel, categoryChoiceBox, 4);
    }

    private void createNoteField(GridPane grid) {
        noteField = new TextField();
        addLabelAndControl(grid, "Note:", noteField, 5);
    }

    private void createDestinationChoiceBox(GridPane grid) {
        destinationChoiceBox = new ChoiceBox<>();
        destinationChoiceBox.setItems(FXCollections.observableArrayList(app.getAccounts().stream()
                .map(Account::getName).toArray(String[]::new)));
        destinationLabel = new Label("Destination Account:");
        addLabelAndControl(grid, destinationLabel, destinationChoiceBox, 4);
        destinationChoiceBox.setVisible(false);
        destinationLabel.setVisible(false); 
    }

    private void addLabelAndControl(GridPane grid, String labelText, Control control, int rowIndex) {
        Label label = new Label(labelText);
        GridPane.setConstraints(label, 0, rowIndex);
        GridPane.setConstraints(control, 1, rowIndex);
        grid.getChildren().addAll(label, control);
    }
    
    private void addLabelAndControl(GridPane grid, Label label, Control control, int rowIndex) {
        GridPane.setConstraints(label, 0, rowIndex);
        GridPane.setConstraints(control, 1, rowIndex);
        grid.getChildren().addAll(label, control);
    }

    private void createButtons(GridPane grid, Stage addTransactionStage) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveTransaction(addTransactionStage));
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> addTransactionStage.close());
        addButtonsToGrid(grid, saveButton, cancelButton);
    }

    private void addButtonsToGrid(GridPane grid, Button saveButton, Button cancelButton) {
        GridPane.setConstraints(saveButton, 0, 7);
        GridPane.setConstraints(cancelButton, 1, 7);
        grid.getChildren().addAll(saveButton, cancelButton);
    }

    private void saveTransaction(Stage addTransactionStage) {
        String type = typeChoiceBox.getValue();
        LocalDate date = datePicker.getValue();
        int amount = Integer.parseInt(amountField.getText());
        Account sourceAccount = findAccount(sourceChoiceBox.getValue());
        String note = noteField.getText();
        Category category = findCategory(categoryChoiceBox.getValue(), type);
        Account destinationAccount = findAccount(destinationChoiceBox.getValue());

        createAndSaveTransaction(type, date, amount, sourceAccount, category, note, destinationAccount);
        if (onTransactionSaved != null) {
            onTransactionSaved.run();
        }
        addTransactionStage.close();
    }

    private void createAndSaveTransaction(String type, LocalDate date, int amount, Account sourceAccount,
                                           Category category, String note, Account destinationAccount) {
        switch (type) {
            case "Income":
                saveIncome(date, amount, category, sourceAccount, note);
                break;
            case "Expense":
                saveExpense(date, amount, category, sourceAccount, note);
                break;
            case "Transfer":
                saveTransfer(date, amount, sourceAccount, destinationAccount, note);
                break;
        }
    }

    private void saveIncome(LocalDate date, int amount, Category category, Account sourceAccount, String note) {
        Income income = new Income();
        income.setDate(LocalDateTime.of(date, LocalTime.now()));
        income.setAmount(amount);
        income.setCategory(category);
        income.setSource(sourceAccount);
        income.setNote(note);
        income.saveToFile();
    }

    private void saveExpense(LocalDate date, int amount, Category category, Account sourceAccount, String note) {
        Expense expense = new Expense();
        expense.setDate(LocalDateTime.of(date, LocalTime.now()));
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setSource(sourceAccount);
        expense.setNote(note);
        expense.saveToFile();
    }

    private void saveTransfer(LocalDate date, int amount, Account sourceAccount, Account destinationAccount, String note) {
        Transfer transfer = new Transfer();
        transfer.setDate(LocalDateTime.of(date, LocalTime.now()));
        transfer.setAmount(amount);
        transfer.setSource(sourceAccount);
        transfer.setDestination(destinationAccount);
        transfer.setNote(note);
        transfer.saveToFile();
    }

    private void updateChoiceBoxes() {
        String type = typeChoiceBox.getValue();
        updateCategoryChoiceBox();
        if (type.equals("Transfer")) {
            destinationChoiceBox.setVisible(true);
            destinationLabel.setVisible(true); // Show destination label
            categoryChoiceBox.setVisible(false); // Hide category choice box
            categoryLabel.setVisible(false);
        } else {
            destinationChoiceBox.setVisible(false);
            destinationLabel.setVisible(false); // Hide destination label
            categoryChoiceBox.setVisible(true); // Show category choice box
            categoryLabel.setVisible(true);
        }
    }

    private void updateCategoryChoiceBox() {
        String type = typeChoiceBox.getValue();
        if (type.equals("Income")) {
            categoryChoiceBox.setItems(FXCollections.observableArrayList(app.getIncomeCategories().stream()
                    .map(Category::getName).toArray(String[]::new)));
        } else if (type.equals("Expense")) {
            categoryChoiceBox.setItems(FXCollections.observableArrayList(app.getExpenseCategories().stream()
                    .map(Category::getName).toArray(String[]::new)));
        }
    }

    private Category findCategory(String categoryName, String type) {
    	if (type.equals("Income"))
    		return app.getIncomeCategories().stream()
    				  .filter(cat -> cat.getName().equals(categoryName))
    				  .findFirst().orElse(null);
    	else 
    		return app.getExpenseCategories().stream()
				  .filter(cat -> cat.getName().equals(categoryName))
				  .findFirst().orElse(null);
    }

    private Account findAccount(String accountName) {
        return app.getAccounts().stream()
                .filter(acc -> acc.getName().equals(accountName))
                .findFirst().orElse(null);
    }
}

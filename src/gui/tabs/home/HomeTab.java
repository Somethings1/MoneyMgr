package gui.tabs.home;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import transaction.core.Expense;
import transaction.core.Income;
import transaction.core.Transaction;
import transaction.util.TransactionReader;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import gui.elements.DailyTransactionItem;
import gui.general.AddTransactionView;

public class HomeTab extends Tab {

    private Label monthYearLabel;
    private Button leftButton;
    private Button rightButton;
    private VBox transactionsVBox;
    private Label totalIncomeLabel;
    private Label totalExpenseLabel;
    private Label totalDifferenceLabel;
    private Button addButton;

    private LocalDate currentDate;
    private TransactionReader transactionReader;

    public HomeTab() {
        setText("Main");
        initializeUI();
        initializeData();
        this.setClosable(false);
    }

    private void initializeUI() {
        BorderPane borderPane = new BorderPane();
        createTopPane(borderPane);
        createScrollPane(borderPane);
        createBottomPane(borderPane);
        setContent(borderPane);
        setButtonActions();
    }

    private void createTopPane(BorderPane borderPane) {
        HBox topPane = new HBox(10);
        leftButton = new Button("←");
        rightButton = new Button("→");
        monthYearLabel = new Label();
        topPane.getChildren().addAll(leftButton, monthYearLabel, rightButton);
        borderPane.setTop(topPane);
    }

    private void createScrollPane(BorderPane borderPane) {
        ScrollPane scrollPane = new ScrollPane();
        transactionsVBox = new VBox(10);
        scrollPane.setContent(transactionsVBox);
        borderPane.setCenter(scrollPane);
    }

    private void createBottomPane(BorderPane borderPane) {
        HBox bottomPane = new HBox(10);
        totalIncomeLabel = new Label("Total Income: 0");
        totalExpenseLabel = new Label("Total Expense: 0");
        totalDifferenceLabel = new Label("Difference: 0");
        addButton = createAddButton();
        bottomPane.getChildren().addAll(totalIncomeLabel, totalExpenseLabel, totalDifferenceLabel, addButton);
        borderPane.setBottom(bottomPane);
    }

    private Button createAddButton() {
        Button button = new Button("+");
        button.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 20px; -fx-pref-width: 50; -fx-pref-height: 50;");
        return button;
    }

    private void setButtonActions() {
        leftButton.setOnAction(e -> handleLeftButton());
        rightButton.setOnAction(e -> handleRightButton());
        addButton.setOnAction(e -> openAddTransactionWindow());
    }

    private void initializeData() {
        transactionReader = new TransactionReader();
        currentDate = LocalDate.now();
        updateMonthYearLabel();
        loadTransactionsForCurrentMonth();
    }

    private void updateMonthYearLabel() {
        monthYearLabel.setText(currentDate.getMonth() + " " + currentDate.getYear());
    }

    private void loadTransactionsForCurrentMonth() {
        Vector<Transaction> transactions = transactionReader.readTransactionsFromMonth(currentDate.getYear(), currentDate.getMonthValue());
        transactionsVBox.getChildren().clear();
        double[] totals = calculateTotals(transactions);
        updateTotalLabels(totals[0], totals[1]);
        
        updateTransactionList(transactions);
    }
    
    private void updateTransactionList (Vector<Transaction> transactions) {
    	Map<LocalDate, List<Transaction>> transactionsByDate = new HashMap<>();

        // Group transactions by date
        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getDate().toLocalDate();
            transactionsByDate.computeIfAbsent(date, k -> new java.util.ArrayList<>()).add(transaction);
        }

        // Create DailyTransactionItem for each date group and add to VBox
        for (Map.Entry<LocalDate, List<Transaction>> entry : transactionsByDate.entrySet()) {
            List<Transaction> dailyTransactions = entry.getValue();

            DailyTransactionItem dailyTransactionItem = new DailyTransactionItem(dailyTransactions);
            transactionsVBox.getChildren().add(dailyTransactionItem);
        }
    }

    private double[] calculateTotals(Vector<Transaction> transactions) {
        double totalIncome = 0;
        double totalExpense = 0;
        for (Transaction transaction : transactions) {
            if (transaction instanceof Income) {
                totalIncome += transaction.getAmount();
            } else if (transaction instanceof Expense) {
                totalExpense += transaction.getAmount();
            }
        }
        return new double[]{totalIncome, totalExpense};
    }

    private void updateTotalLabels(double totalIncome, double totalExpense) {
        totalIncomeLabel.setText("Total Income: " + totalIncome);
        totalExpenseLabel.setText("Total Expense: " + totalExpense);
        totalDifferenceLabel.setText("Difference: " + (totalIncome - totalExpense));
    }

    private void handleLeftButton() {
        currentDate = currentDate.minusMonths(1);
        updateMonthYearLabel();
        loadTransactionsForCurrentMonth();
    }

    private void handleRightButton() {
        currentDate = currentDate.plusMonths(1);
        updateMonthYearLabel();
        loadTransactionsForCurrentMonth();
    }

    private void openAddTransactionWindow() {
    	AddTransactionView addTransactionView = new AddTransactionView();
        addTransactionView.display();
        addTransactionView.setOnTransactionSaved(() -> {
            loadTransactionsForCurrentMonth();
        });

    }
}



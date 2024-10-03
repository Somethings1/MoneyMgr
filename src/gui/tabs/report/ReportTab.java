package gui.tabs.report;

import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import transaction.core.Transaction;
import transaction.properties.Category;
import app.App;
import transaction.util.TransactionReader;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportTab extends Tab {

    private App app;
    private YearMonth currentMonth;
    private Label monthLabel;
    private TabPane tabPane;
    private ToggleGroup toggleGroupStats;
    private ToggleGroup toggleGroupBudget;
    private ToggleGroup toggleGroupNote;
    private TransactionReader transactionReader = new TransactionReader();

    public ReportTab() {
        this.app = App.getInstance();
        this.currentMonth = YearMonth.now();
        setText("Reports");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        HBox monthNavigation = createMonthNavigation();
        layout.getChildren().add(monthNavigation);

        tabPane = createTabPane();
        layout.getChildren().add(tabPane);
        this.setClosable(false);

        setContent(layout);
    }

    private HBox createMonthNavigation() {
        Button prevMonthButton = new Button("Previous Month");
        prevMonthButton.setOnAction(e -> changeMonth(-1));

        Button nextMonthButton = new Button("Next Month");
        nextMonthButton.setOnAction(e -> changeMonth(1));

        monthLabel = new Label(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        monthLabel.setPadding(new Insets(0, 20, 0, 20));

        return new HBox(10, prevMonthButton, monthLabel, nextMonthButton);
    }

    private void changeMonth(int monthOffset) {
        currentMonth = currentMonth.plusMonths(monthOffset);
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        reloadTabs();
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();

        Tab statsTab = createStatsTab();
        Tab budgetTab = createBudgetTab();
        Tab noteTab = createNoteTab();

        tabPane.getTabs().addAll(statsTab, budgetTab, noteTab);
        return tabPane;
    }

    private Tab createStatsTab() {
        Tab statsTab = new Tab("Stats");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        toggleGroupStats = createToggleGroup();
        PieChart pieChart = new PieChart();
        TextArea categoryDetails = new TextArea();
        categoryDetails.setEditable(false);

        toggleGroupStats.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> 
            updateStatsTab(pieChart, categoryDetails, isIncomeSelected(toggleGroupStats)));

        layout.getChildren().addAll(createRadioButtons(toggleGroupStats), pieChart, categoryDetails);
        statsTab.setContent(layout);

        return statsTab;
    }

    private void updateStatsTab(PieChart pieChart, TextArea categoryDetails, boolean isIncome) {
        List<Transaction> transactions = readTransactionsForCurrentMonth(isIncome);
        Map<String, Double> categoryData = calculateCategoryData(transactions);

        updatePieChart(pieChart, categoryData);
        updateCategoryDetails(categoryDetails, categoryData);
    }

    private Tab createBudgetTab() {
        Tab budgetTab = new Tab("Budget");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        toggleGroupBudget = createToggleGroup();
        VBox categoryList = new VBox(10);

        toggleGroupBudget.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> 
            updateBudgetTab(categoryList, isIncomeSelected(toggleGroupBudget)));

        layout.getChildren().addAll(createRadioButtons(toggleGroupBudget), categoryList);
        budgetTab.setContent(layout);

        return budgetTab;
    }

    private void updateBudgetTab(VBox categoryList, boolean isIncome) {
        categoryList.getChildren().clear();
        List<Category> categories = isIncome ? app.getIncomeCategories() : app.getExpenseCategories();

        for (Category category : categories) {
            addCategoryToList(categoryList, category, isIncome);
        }
    }

    private Tab createNoteTab() {
        Tab noteTab = new Tab("Note");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        toggleGroupNote = createToggleGroup();
        TextArea noteList = new TextArea();
        noteList.setEditable(false);

        toggleGroupNote.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> 
            updateNoteTab(noteList, isIncomeSelected(toggleGroupNote)));

        layout.getChildren().addAll(createRadioButtons(toggleGroupNote), noteList);
        noteTab.setContent(layout);

        return noteTab;
    }

    private void updateNoteTab(TextArea noteList, boolean isIncome) {
        List<Transaction> transactions = readTransactionsForCurrentMonth(isIncome);

        Map<String, List<Transaction>> notesData = groupByNotes(transactions);
        displayNotes(noteList, notesData);
    }

    private void reloadTabs() {
        updateStatsTab(null, null, isIncomeSelected(toggleGroupStats));
        updateBudgetTab(null, isIncomeSelected(toggleGroupBudget));
        updateNoteTab(null, isIncomeSelected(toggleGroupNote));
    }

    private boolean isIncomeSelected(ToggleGroup toggleGroup) {
        RadioButton selectedButton = (RadioButton) toggleGroup.getSelectedToggle();
        return selectedButton.getText().equals("Income");
    }

    private ToggleGroup createToggleGroup() {
        return new ToggleGroup();
    }

    private HBox createRadioButtons(ToggleGroup toggleGroup) {
        RadioButton incomeOption = new RadioButton("Income");
        RadioButton expenseOption = new RadioButton("Expense");

        incomeOption.setToggleGroup(toggleGroup);
        expenseOption.setToggleGroup(toggleGroup);
        expenseOption.setSelected(true);

        return new HBox(10, incomeOption, expenseOption);
    }

    private List<Transaction> readTransactionsForCurrentMonth(boolean isIncome) {
        int year = currentMonth.getYear();
        int month = currentMonth.getMonthValue();

        return isIncome 
                ? new ArrayList<>(transactionReader.readIncomeFromMonth(year, month)) 
                : new ArrayList<>(transactionReader.readExpenseFromMonth(year, month));
    }

    private Map<String, Double> calculateCategoryData(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(), 
                        Collectors.summingDouble(Transaction::getAmount)));
    }

    private void updatePieChart(PieChart pieChart, Map<String, Double> categoryData) {
        if (pieChart == null) return;
        pieChart.getData().clear();
        categoryData.forEach((category, amount) -> 
            pieChart.getData().add(new PieChart.Data(category, amount)));
    }

    private void updateCategoryDetails(TextArea categoryDetails, Map<String, Double> categoryData) {
        if (categoryDetails == null) return;
        StringBuilder details = new StringBuilder();
        categoryData.forEach((category, amount) -> 
            details.append(category).append(": $").append(amount).append("\n"));
        categoryDetails.setText(details.toString());
    }

    private void addCategoryToList(VBox categoryList, Category category, boolean isIncome) {
        ProgressBar progressBar = new ProgressBar();
        double progress = calculateProgress(category, isIncome);

        progressBar.setProgress(progress);
        Label categoryLabel = new Label(category.getName() + " - " + (int) (progress * 100) + "%");

        VBox item = new VBox(categoryLabel, progressBar);
        categoryList.getChildren().add(item);
    }

    private double calculateProgress(Category category, boolean isIncome) {
        List<Transaction> transactions = readTransactionsForCurrentMonth(isIncome);

        double spentAmount = transactions.stream()
                .filter(t -> t.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
        double budget = category.getBudget();
        return spentAmount / budget;
    }

    private Map<String, List<Transaction>> groupByNotes(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getNote));
    }

    private void displayNotes(TextArea noteList, Map<String, List<Transaction>> notesData) {
        StringBuilder notesDetails = new StringBuilder();
        notesData.forEach((note, noteTransactions) -> {
            double totalAmount = noteTransactions.stream().mapToDouble(Transaction::getAmount).sum();
            notesDetails.append("Note: ").append(note)
                    .append(" | Transactions: ").append(noteTransactions.size())
                    .append(" | Total Amount: $").append(totalAmount).append("\n");
        });

        noteList.setText(notesDetails.toString());
    }
}

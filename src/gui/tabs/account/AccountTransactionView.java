package gui.tabs.account;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import transaction.properties.Account;
import transaction.core.Transaction;
import transaction.util.TransactionReader;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountTransactionView {

    private TransactionReader transactionReader;
    private YearMonth currentMonth; // To keep track of the selected month
    private GridPane layout;
    private Account currentAccount;

    public AccountTransactionView() {
        this.transactionReader = new TransactionReader();
        this.currentMonth = YearMonth.now(); // Initialize with the current month
    }

    public void showAccountTransactions(Account account) {
        // Ensure the stage is created on the JavaFX Application Thread
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Transactions for " + account.getName());

            // Set window size and position
            stage.setWidth(400);
            stage.setHeight(600);
            stage.setX(200);
            stage.setY(200);

            layout = new GridPane();
            layout.setPadding(new Insets(10));
            layout.setVgap(10);
            layout.setHgap(20);

            this.currentAccount = account; // Store the account being viewed

            // Add month navigation buttons
            addMonthNavigationButtons(layout, stage);

            displayTransactionsByDate();

            Scene scene = new Scene(layout);
            stage.setScene(scene);

            stage.show();
        });
    }

    private void addMonthNavigationButtons(GridPane layout, Stage stage) {
        Button prevMonthButton = new Button("Previous Month");
        Button nextMonthButton = new Button("Next Month");

        // Set button actions
        prevMonthButton.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshTransactions(); // Reload transactions for the new month
        });

        nextMonthButton.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshTransactions(); // Reload transactions for the new month
        });

        // Add buttons to the layout
        GridPane.setConstraints(prevMonthButton, 0, 0);
        GridPane.setConstraints(nextMonthButton, 1, 0);

        layout.getChildren().addAll(prevMonthButton, nextMonthButton);
    }

    private void refreshTransactions() {
        layout.getChildren().clear(); // Clear the current content
        addMonthNavigationButtons(layout, null); // Add navigation buttons back
        displayTransactionsByDate(); // Display transactions for the updated month
    }

    private void displayTransactionsByDate() {
        List<Transaction> transactions = transactionReader.readTransactionsFromMonth(currentMonth.getYear(), currentMonth.getMonthValue())
                .stream()
                .filter(t -> isAccountRelatedTransaction(t, currentAccount))
                .collect(Collectors.toList());

        Map<LocalDate, List<Transaction>> groupedTransactions = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getDate().toLocalDate()));

        int row = 1; // Start after navigation buttons
        for (LocalDate date : groupedTransactions.keySet()) {
            List<Transaction> dailyTransactions = groupedTransactions.get(date);

            // Calculate daily difference and balance at the end of the day
            int dailyDifference = calculateDailyDifference(dailyTransactions);
            int endOfDayBalance = calculateEndOfDayBalance(currentAccount, currentMonth.getYear(), currentMonth.getMonthValue(), date);

            // Display date header
            displayDateHeader(layout, date, dailyDifference, endOfDayBalance, row++);
            
            // Display each transaction under the date
            for (Transaction transaction : dailyTransactions) {
                displayTransaction(layout, transaction, ++row);
            }
        }
    }

    private boolean isAccountRelatedTransaction(Transaction transaction, Account account) {
        return transaction.getSource().equals(account);
    }

    private int calculateDailyDifference(List<Transaction> transactions) {
        return transactions.stream()
                .mapToInt(Transaction::getAmount)
                .sum();
    }

    private int calculateEndOfDayBalance(Account account, int year, int month, LocalDate date) {
        List<Transaction> transactionsUpToDate = transactionReader.readTransactionsFromMonth(year, month)
                .stream()
                .filter(t -> t.getDate().toLocalDate().isBefore(date.plusDays(1)))
                .collect(Collectors.toList());

        return (int) (account.getBalance() + transactionsUpToDate.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());
    }

    private void displayDateHeader(GridPane layout, LocalDate date, int dailyDifference, int endOfDayBalance, int row) {
        Label dateLabel = new Label(date.toString());
        Label dailyDiffLabel = new Label("Daily Difference: " + dailyDifference);
        Label endOfDayLabel = new Label("End of Day Balance: " + endOfDayBalance);

        GridPane.setConstraints(dateLabel, 0, row);
        GridPane.setConstraints(dailyDiffLabel, 1, row);
        GridPane.setConstraints(endOfDayLabel, 2, row);

        layout.getChildren().addAll(dateLabel, dailyDiffLabel, endOfDayLabel);
    }

    private void displayTransaction(GridPane layout, Transaction transaction, int row) {
        Label transactionLabel = new Label(transaction.getNote());
        Label amountLabel = new Label(String.valueOf(transaction.getAmount()));

        GridPane.setConstraints(transactionLabel, 0, row);
        GridPane.setConstraints(amountLabel, 1, row);

        layout.getChildren().addAll(transactionLabel, amountLabel);
    }
}

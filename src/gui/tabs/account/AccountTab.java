package gui.tabs.account;

import app.App;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import transaction.properties.Account;
import transaction.util.TransactionReader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountTab extends Tab {
    private App app;

    public AccountTab() {
        this.app = App.getInstance();
        this.setText("Accounts");
        reloadTab();
        this.setClosable(false);
    }

    private void reloadTab() {
        GridPane layout = createLayout();
        this.setContent(layout);
        displayTotalBalance(layout);
        displayAccountsByGroup(layout);
    }

    private GridPane createLayout() {
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(20);
        return layout;
    }

    private void displayTotalBalance(GridPane layout) {
        int totalBalance = (int) app.getAccounts().stream()
                .filter(Account::isIncludedInTotal)
                .mapToDouble(Account::getBalance)
                .sum();
        Label totalLabel = new Label("Total Balance:");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label totalBalanceLabel = new Label(formatBalance(totalBalance));
        totalBalanceLabel.setTextFill(getBalanceColor(totalBalance));

        GridPane.setConstraints(totalLabel, 0, 0);
        GridPane.setConstraints(totalBalanceLabel, 1, 0);

        layout.getChildren().addAll(totalLabel, totalBalanceLabel);
    }

    private void displayAccountsByGroup(GridPane layout) {
        Map<String, List<Account>> groupedAccounts = app.getAccounts().stream()
                .collect(Collectors.groupingBy(Account::getGroup));

        int row = 1;
        for (String groupName : groupedAccounts.keySet()) {
            List<Account> accounts = groupedAccounts.get(groupName);
            displayGroup(layout, groupName, accounts, row);
            row += accounts.size() + 1; // Move row down by the number of accounts in the group
        }
    }

    private void displayGroup(GridPane layout, String groupName, List<Account> accounts, int row) {
        int groupTotal = (int) accounts.stream().mapToDouble(Account::getBalance).sum();
        Label groupLabel = new Label(groupName);
        groupLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label groupTotalLabel = new Label(formatBalance(groupTotal));
        groupTotalLabel.setTextFill(getBalanceColor(groupTotal));

        GridPane.setConstraints(groupLabel, 0, row);
        GridPane.setConstraints(groupTotalLabel, 1, row);

        layout.getChildren().addAll(groupLabel, groupTotalLabel);

        for (Account account : accounts) {
            displayAccount(layout, account, ++row);
        }
    }

    private void displayAccount(GridPane layout, Account account, int row) {
        Label nameLabel = new Label(account.getName());
        
        // Handle account click to display transactions
        nameLabel.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            AccountTransactionView accountTransactionView = new AccountTransactionView();
            accountTransactionView.showAccountTransactions(account);
        });
        
        GridPane.setConstraints(nameLabel, 0, row);

        Label balanceLabel = new Label(formatBalance((int) account.getBalance()));
        balanceLabel.setTextFill(getAccountBalanceColor(account));
        GridPane.setConstraints(balanceLabel, 1, row);

        layout.getChildren().addAll(nameLabel, balanceLabel);
    }

    private String formatBalance(int balance) {
        return String.valueOf(Math.abs(balance));
    }

    private Color getBalanceColor(double balance) {
        return balance >= 0 ? Color.BLUE : Color.RED;
    }

    private Color getAccountBalanceColor(Account account) {
        if (!account.isIncludedInTotal()) {
            return Color.GREY;
        }
        return getBalanceColor(account.getBalance());
    }
}

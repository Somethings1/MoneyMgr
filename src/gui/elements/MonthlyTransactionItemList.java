package gui.elements;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import transaction.core.Expense;
import transaction.core.Income;
import transaction.core.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class MonthlyTransactionItemList extends VBox {
    public MonthlyTransactionItemList(Vector<Transaction> transactions) {
        setSpacing(20);
        setAlignment(Pos.TOP_LEFT);
        
        // Create header
        HBox header = createHeader(transactions);
        getChildren().add(header);
        
        // Group transactions by date and create DailyTransactionItem for each group
        Map<LocalDate, List<Transaction>> groupedTransactions = groupTransactionsByDate(transactions);
        for (Map.Entry<LocalDate, List<Transaction>> entry : groupedTransactions.entrySet()) {
            DailyTransactionItem dailyTransactionItem = new DailyTransactionItem(entry.getValue());
            getChildren().add(dailyTransactionItem);
        }
    }

    private HBox createHeader(Vector<Transaction> transactions) {
        HBox header = new HBox();
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER);

        // Calculate totals
        int totalIncome = transactions.stream()
                .filter(t -> t instanceof Income)
                .mapToInt(Transaction::getAmount)
                .sum();

        int totalExpense = transactions.stream()
                .filter(t -> t instanceof Expense)
                .mapToInt(Transaction::getAmount)
                .sum();

        int totalBalance = totalIncome - totalExpense;

        // Create three VBox elements: Income, Expense, and Total
        VBox incomeBox = createBalanceBox("Income", totalIncome);
        VBox expenseBox = createBalanceBox("Expense", totalExpense);
        VBox totalBox = createBalanceBox("Total", totalBalance);

        // Add them to the header
        header.getChildren().addAll(incomeBox, expenseBox, totalBox);
        
        return header;
    }

    private VBox createBalanceBox(String labelText, int amount) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        BalanceLabel balanceLabel = new BalanceLabel(amount);

        box.getChildren().addAll(label, balanceLabel);
        return box;
    }

    private Map<LocalDate, List<Transaction>> groupTransactionsByDate(Vector<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getDate().toLocalDate()));
    }
}

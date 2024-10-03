package gui.elements;

import transaction.core.Expense;
import transaction.core.Income;
import transaction.core.Transaction;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

import java.time.LocalDate;
import java.util.List;

public class DailyTransactionItem extends VBox {

    public DailyTransactionItem(List<Transaction> transactions) {
        setSpacing(10);
        setAlignment(Pos.TOP_LEFT);
        setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, null))); // Set a warm background color

        if (transactions.isEmpty()) {
            return; // Return if no transactions
        }

        LocalDate date = transactions.get(0).getDate().toLocalDate();
        createHeader(date, transactions);
        createDivider();
        createTransactionItems(transactions);
    }

    private void createHeader(LocalDate date, List<Transaction> transactions) {
        HBox header = new HBox();
        header.setSpacing(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label(date.getDayOfMonth() + " " + date.getMonth());
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        int totalIncome = transactions.stream()
                .filter(t -> t instanceof Income)
                .mapToInt(Transaction::getAmount)
                .sum();
        int totalExpense = transactions.stream()
                .filter(t -> t instanceof Expense)
                .mapToInt(Transaction::getAmount)
                .sum();

        BalanceLabel incomeLabel = new BalanceLabel(totalIncome);
        BalanceLabel expenseLabel = new BalanceLabel(-totalExpense);

        header.getChildren().addAll(dateLabel, incomeLabel, expenseLabel);
        getChildren().add(header);
    }

    private void createDivider() {
        Line divider = new Line();
        divider.setStroke(Color.GRAY);
        divider.setStrokeWidth(1);
        divider.setStartX(0);
        divider.setEndX(400); // Adjust based on your layout
        getChildren().add(divider);
    }

    private void createTransactionItems(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            TransactionItem transactionItem = new TransactionItem(transaction);
            getChildren().add(transactionItem);
        }
    }
}

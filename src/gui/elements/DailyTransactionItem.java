package gui.elements;

import transaction.core.Expense;
import transaction.core.Income;
import transaction.core.Transaction;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class DailyTransactionItem extends VBox {

    public DailyTransactionItem(List<Transaction> transactions) {
        //setSpacing(10);
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
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefWidth(400);
        header.setPadding(new Insets(5, 15, 0, 15));

        // Date label (day of the month)
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        // Day of the week label (3 letters)
        Label dayOfWeekLabel = new Label(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        dayOfWeekLabel.setStyle("-fx-background-color: #FFD700; -fx-padding: 2 4 2 4; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        // Total Income
        int totalIncome = transactions.stream()
                .filter(t -> t instanceof Income)
                .mapToInt(Transaction::getAmount)
                .sum();

        // Total Expense
        int totalExpense = transactions.stream()
                .filter(t -> t instanceof Expense)
                .mapToInt(Transaction::getAmount)
                .sum();

        // Income and Expense Labels
        BalanceLabel incomeLabel = new BalanceLabel(totalIncome);
        BalanceLabel expenseLabel = new BalanceLabel(-totalExpense);
        incomeLabel.setPrefWidth(100);
        expenseLabel.setPrefWidth(100);

        // Spacer to push the income and expense labels to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS); // Allow the spacer to grow

        // Add elements to the header
        header.getChildren().addAll(dateLabel, dayOfWeekLabel, spacer, incomeLabel, expenseLabel);
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

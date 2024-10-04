package gui.elements;

import transaction.core.Expense;
import transaction.core.Income;
import transaction.core.Transaction;
import transaction.core.Transfer;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class TransactionItem extends HBox {
    public TransactionItem(Transaction transaction) {
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);
        setPrefWidth(400);
        setPadding(new Insets(0, 15, 0, 15));

        Pane categoryPane = createCategoryPane(transaction);
        VBox noteAndAccountBox = createNoteAndAccountBox(transaction);
        BalanceLabel balanceLabel = createBalanceLabel(transaction);

        getChildren().addAll(categoryPane, noteAndAccountBox, balanceLabel);
        HBox.setHgrow(noteAndAccountBox, javafx.scene.layout.Priority.ALWAYS);
    }

    protected Pane createCategoryPane(Transaction transaction) {
        Pane categoryPane = new Pane();
        Label categoryLabel = new Label();
        categoryLabel.setText(getCategoryName(transaction));
        categoryLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
        categoryLabel.setAlignment(Pos.CENTER);
        categoryLabel.setPrefHeight(50);
        categoryPane.getChildren().add(categoryLabel);
        categoryPane.setPrefHeight(50);
        categoryPane.setPrefWidth(150);
        return categoryPane;
    }

    protected String getCategoryName(Transaction transaction) {
        if (transaction instanceof Income || transaction instanceof Expense) {
            return transaction.getCategory().getEmoji() + " " + transaction.getCategory().getName();
        }
        return "Transfer";
    }

    protected VBox createNoteAndAccountBox(Transaction transaction) {
        VBox noteAndAccountBox = new VBox();
        if (transaction instanceof Income || transaction instanceof Expense) {
            createIncomeOrExpenseLabels(transaction, noteAndAccountBox);
        } else {
            createTransferLabel(transaction, noteAndAccountBox);
        }
        noteAndAccountBox.setAlignment(Pos.CENTER_LEFT);
        noteAndAccountBox.setPrefHeight(50);
        noteAndAccountBox.setPrefWidth(200);
        return noteAndAccountBox;
    }

    private void createIncomeOrExpenseLabels(Transaction transaction, VBox box) {
        Label noteLabel = new Label(transaction.getNote());
        Label accountLabel = new Label(transaction.getSource().getName());
        noteLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 14));
        accountLabel.setFont(Font.font("Montserrat Thin", 14));
        box.getChildren().addAll(noteLabel, accountLabel);
    }

    private void createTransferLabel(Transaction transaction, VBox box) {
        Transfer transfer = (Transfer) transaction;
        String accountInfo = transfer.getSource().getName() + " -> " + transfer.getDestination().getName();
        Label transferInfoLabel = new Label(accountInfo);
        transferInfoLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 12));
        box.getChildren().add(transferInfoLabel);
    }

    protected BalanceLabel createBalanceLabel(Transaction transaction) {
        int amount = transaction.getAmount();
        boolean isExpense = transaction instanceof Expense;
        boolean isTransfer = transaction instanceof Transfer;
        BalanceLabel balanceLabel = new BalanceLabel(isExpense ? -amount : amount, isTransfer);
        balanceLabel.setPrefHeight(50);
        balanceLabel.setPrefWidth(100);
        return balanceLabel;
    }
}

package gui.elements;

import transaction.properties.Account;
import transaction.core.Transfer;
import transaction.core.Expense;
import transaction.core.Transaction;

public class AccountTransactionItem extends TransactionItem {
    private final Account account;

    public AccountTransactionItem(Transaction transaction, Account account) {
        super(transaction);
        this.account = account;
    }

    @Override
    protected BalanceLabel createBalanceLabel(Transaction transaction) {
        int amount = transaction.getAmount();
        boolean isExpense = transaction instanceof Expense;
        boolean isTransfer = transaction instanceof Transfer;
        
        if (isTransfer) {
            Transfer transfer = (Transfer) transaction;
            // Change balance color based on whether the account is the source or destination
            boolean isSourceAccount = transfer.getSource().equals(account);
            BalanceLabel balanceLabel = new BalanceLabel(amount, false); // Not grey
            balanceLabel.setTextFill(isSourceAccount ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.BLUE);
            return balanceLabel;
        }

        // Fallback to default behavior for non-transfer transactions
        return super.createBalanceLabel(transaction);
    }
}

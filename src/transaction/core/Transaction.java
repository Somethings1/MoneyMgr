package transaction.core;

import java.time.LocalDateTime;

import transaction.properties.Account;
import transaction.properties.Category;

public abstract class Transaction {
    private LocalDateTime date;
    private int amount;
    private Account source;
    private String note;
    
    public Transaction () {}

    public Transaction(LocalDateTime date, int amount, Account source, String note) {
        this.date = date;
        this.amount = amount;
        this.source = source;
        this.note = note;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Account getSource() {
        return source;
    }

    public void setSource(Account source) {
        this.source = source;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public abstract void saveToFile();
    
    public Category getCategory () {
    	return new Category ("", "", 0);
    }
}
package transaction.properties;

import javafx.beans.property.*;

public class Account {
    private StringProperty name;
    private IntegerProperty balance;
    private BooleanProperty includedInTotal;
    private StringProperty group;  // New group property

    public Account(String name, String group, int balance, boolean includeInTotal) {
        this.name = new SimpleStringProperty(name);
        this.balance = new SimpleIntegerProperty(balance);
        this.includedInTotal = new SimpleBooleanProperty(includeInTotal);
        this.group = new SimpleStringProperty(group);
    }

    // Getter and Setter for Name
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // Getter and Setter for Balance
    public double getBalance() {
        return balance.get();
    }

    public void setBalance(int balance) {
        this.balance.set(balance);
    }

    public IntegerProperty balanceProperty() {
        return balance;
    }

    // Getter and Setter for IncludedInTotal
    public boolean isIncludedInTotal() {
        return includedInTotal.get();
    }

    public void setIncludedInTotal(boolean includedInTotal) {
        this.includedInTotal.set(includedInTotal);
    }

    public BooleanProperty includedInTotalProperty() {
        return includedInTotal;
    }

    // Getter and Setter for Group
    public String getGroup() {
        return group.get();
    }

    public void setGroup(String group) {
        this.group.set(group);
    }

    public StringProperty groupProperty() {
        return group;
    }
}

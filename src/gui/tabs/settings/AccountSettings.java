package gui.tabs.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import transaction.properties.Account;
import javafx.util.converter.DefaultStringConverter;

import java.util.HashMap;
import java.util.Map;

import app.App;

public class AccountSettings {

    private TreeTableView<Account> accountTreeTable;
    private ObservableList<Account> accounts;

    public AccountSettings() {
        this.accounts = FXCollections.observableArrayList(App.getInstance().getAccounts()); // Retrieve accounts from App class
        initialize();
    }

    private void initialize() {
        TreeItem<Account> rootItem = createRootItem();
        Map<String, TreeItem<Account>> groupMap = createGroupItems(rootItem);
        addAccountsToGroups(groupMap);
        setupAccountTreeTable(rootItem);
        addColumns();
        setColumnEventHandlers();
    }

    private TreeItem<Account> createRootItem() {
        Account rootAccount = new Account("", "All Accounts", 0, false);
        TreeItem<Account> rootItem = new TreeItem<>(rootAccount);
        rootItem.setExpanded(true);
        return rootItem;
    }

    private Map<String, TreeItem<Account>> createGroupItems(TreeItem<Account> rootItem) {
        Map<String, TreeItem<Account>> groupMap = new HashMap<>();
        for (Account account : accounts) {
            createGroupIfAbsent(groupMap, account.getGroup(), rootItem);
        }
        return groupMap;
    }

    private void createGroupIfAbsent(Map<String, TreeItem<Account>> groupMap, String group, TreeItem<Account> rootItem) {
        if (!groupMap.containsKey(group)) {
            TreeItem<Account> groupItem = new TreeItem<>(new Account(group, group, 0, false));
            groupMap.put(group, groupItem);
            rootItem.getChildren().add(groupItem);
        }
    }

    private void addAccountsToGroups(Map<String, TreeItem<Account>> groupMap) {
        for (Account account : accounts) {
            TreeItem<Account> accountItem = new TreeItem<>(account);
            groupMap.get(account.getGroup()).getChildren().add(accountItem);
        }
    }

    private void setupAccountTreeTable(TreeItem<Account> rootItem) {
        accountTreeTable = new TreeTableView<>(rootItem);
        accountTreeTable.setShowRoot(false);
        accountTreeTable.setEditable(true);
    }

    private void addColumns() {
        accountTreeTable.getColumns().addAll(
            createNameColumn(), createBalanceColumn(), createIncludeInTotalColumn(), createGroupColumn(), createDeleteColumn()
        );
    }

    private TreeTableColumn<Account, String> createNameColumn() {
        TreeTableColumn<Account, String> nameColumn = createEditableColumn("Account Name", param -> param.getValue().getValue().nameProperty(), new DefaultStringConverter());
        return nameColumn;
    }

    private TreeTableColumn<Account, Integer> createBalanceColumn() {
        TreeTableColumn<Account, Integer> balanceColumn = createEditableColumn("Balance", param -> param.getValue().getValue().balanceProperty().asObject(), new IntegerStringConverter());
        return balanceColumn;
    }

    private TreeTableColumn<Account, Boolean> createIncludeInTotalColumn() {
        TreeTableColumn<Account, Boolean> includeInTotalColumn = new TreeTableColumn<>("Include in Total");
        includeInTotalColumn.setCellValueFactory(param -> param.getValue().getValue().includedInTotalProperty());
        includeInTotalColumn.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(includeInTotalColumn));
        includeInTotalColumn.setEditable(true);
        return includeInTotalColumn;
    }

    private <T> TreeTableColumn<Account, T> createEditableColumn(String title, javafx.util.Callback<TreeTableColumn.CellDataFeatures<Account, T>, javafx.beans.value.ObservableValue<T>> valueFactory, javafx.util.StringConverter<T> converter) {
        TreeTableColumn<Account, T> column = new TreeTableColumn<>(title);
        column.setCellValueFactory(valueFactory);
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(converter));
        column.setEditable(true);
        return column;
    }

    private TreeTableColumn<Account, String> createGroupColumn() {
        return createEditableColumn("Group", param -> param.getValue().getValue().groupProperty(), new DefaultStringConverter());
    }

    private TreeTableColumn<Account, Void> createDeleteColumn() {
        TreeTableColumn<Account, Void> deleteColumn = new TreeTableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> createDeleteButtonCell());
        return deleteColumn;
    }

    private TreeTableCell<Account, Void> createDeleteButtonCell() {
        return new TreeTableCell<>() {
            private final Button deleteButton = new Button("x");

            {
                deleteButton.setOnAction(event -> {
                    Account account = getTreeTableView().getTreeItem(getIndex()).getValue();
                    showDeleteConfirmation(account);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        };
    }

    private void setColumnEventHandlers() {
        accountTreeTable.getColumns().get(0).setOnEditCommit(event -> 
            showConfirmationDialog(() -> event.getRowValue()
            								  .getValue()
            								  .setName(
            										  event
            										  .getNewValue()
            										  .toString()
            										  )
            					  )
        );
        accountTreeTable.getColumns().get(1).setOnEditCommit(event -> 
            showConfirmationDialog(() -> event.getRowValue().getValue().setBalance(Integer.parseInt(event.getNewValue().toString())))
        );
        accountTreeTable.getColumns().get(2).setOnEditCommit(event -> 
            showConfirmationDialog(() -> event.getRowValue().getValue().setIncludedInTotal(Boolean.parseBoolean(event.getNewValue().toString())))
        );
        accountTreeTable.getColumns().get(3).setOnEditCommit(event -> 
            showConfirmationDialog(() -> event.getRowValue().getValue().setGroup(event.getNewValue().toString()))
        );
    }

    public void show() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Account Settings");
        settingsStage.setScene(new Scene(new VBox(accountTreeTable), 600, 400));
        settingsStage.show();
    }

    private void showConfirmationDialog(Runnable onConfirmAction) {
        Alert confirmDialog = createConfirmationDialog();
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) onConfirmAction.run();
        });
    }

    private Alert createConfirmationDialog() {
        return new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to make this change?", ButtonType.YES, ButtonType.NO);
    }

    private void showDeleteConfirmation(Account account) {
        Alert confirmDialog = createDeleteConfirmationDialog(account);
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) accounts.remove(account);
        });
    }

    private Alert createDeleteConfirmationDialog(Account account) {
        return new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove the account: " + account.getName() + "?", ButtonType.YES, ButtonType.NO);
    }
}

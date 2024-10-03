package transaction.util;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Vector;

import app.App;
import transaction.core.Expense;
import transaction.core.Income;
import transaction.core.Transaction;
import transaction.core.Transfer;
import transaction.properties.Account;
import transaction.properties.Category;

public class TransactionReader {
    private App app;

    public TransactionReader() {
        this.app = App.getInstance();
    }

    // Read methods for each transaction type
    public Vector<Income> readIncomeFromDatetime(LocalDateTime date) {
        return readIncome(PathUtil.get(date));
    }

    public Vector<Expense> readExpenseFromDatetime(LocalDateTime date) {
        return readExpense(PathUtil.get(date));
    }

    public Vector<Transfer> readTransferFromDatetime(LocalDateTime date) {
        return readTransfer(PathUtil.get(date));
    }

    public Vector<Income> readIncomeFromMonth(int year, int month) {
        return readIncome(PathUtil.get(year, month));
    }

    public Vector<Expense> readExpenseFromMonth(int year, int month) {
        return readExpense(PathUtil.get(year, month));
    }

    public Vector<Transfer> readTransferFromMonth(int year, int month) {
        return readTransfer(PathUtil.get(year, month));
    }

    public Vector<Income> readIncomeFromYear(int year) {
        return readIncome(PathUtil.get(year));
    }

    public Vector<Expense> readExpenseFromYear(int year) {
        return readExpense(PathUtil.get(year));
    }

    public Vector<Transfer> readTransferFromYear(int year) {
        return readTransfer(PathUtil.get(year));
    }

    public Vector<Transaction> readTransactionsFromDatetime(LocalDateTime date) {
        return readTransactions(date);
    }

    public Vector<Transaction> readTransactionsFromMonth(int year, int month) {
        return readTransactionsFrom(PathUtil.get(year, month));
    }

    public Vector<Transaction> readTransactionsFromYear(int year) {
        return readTransactionsFrom(PathUtil.get(year));
    }

    // Recursive directory traversal
    private <T> Vector<T> readTransactionFilesRecursively(String directoryPath, FileReaderHandler<T> handler) {
        Vector<T> transactions = new Vector<>();
        File dir = new File(directoryPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Recurse into subdirectory
                        transactions.addAll(readTransactionFilesRecursively(file.getAbsolutePath(), handler));
                    } else if (file.getName().endsWith(".txt")) {
                        transactions.addAll(handler.readFile(file));
                    }
                }
            }
        }
        return transactions;
    }

    // Modified read methods to use recursive traversal
    private Vector<Income> readIncome(String directoryPath) {
        return readTransactionFilesRecursively(directoryPath, this::readIncomesFromFile);
    }

    private Vector<Expense> readExpense(String directoryPath) {
        return readTransactionFilesRecursively(directoryPath, this::readExpensesFromFile);
    }

    private Vector<Transfer> readTransfer(String directoryPath) {
        return readTransactionFilesRecursively(directoryPath, this::readTransfersFromFile);
    }

    // Transaction methods
    private Vector<Transaction> readTransactions(LocalDateTime date) {
        Vector<Transaction> result = new Vector<>();
        result.addAll(readIncomeFromDatetime(date));
        result.addAll(readExpenseFromDatetime(date));
        result.addAll(readTransferFromDatetime(date));
        return result;
    }

    private Vector<Transaction> readTransactionsFrom(String basePath) {
        Vector<Transaction> result = new Vector<>();
        result.addAll(readIncome(basePath));
        result.addAll(readExpense(basePath));
        result.addAll(readTransfer(basePath));
        return result;
    }

    // File reading logic
    private Vector<Income> readIncomesFromFile(File file) {
        return readFromFile(file, "Income", this::parseIncome);
    }

    private Vector<Expense> readExpensesFromFile(File file) {
        return readFromFile(file, "Expense", this::parseExpense);
    }

    private Vector<Transfer> readTransfersFromFile(File file) {
        return readFromFile(file, "Transfer", this::parseTransfer);
    }

    // General file reading method
    private <T> Vector<T> readFromFile(File file, String type, TransactionParser<T> parser) {
        Vector<T> result = new Vector<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(type)) {
                    result.add(parser.parse(reader));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Parsing methods for each transaction type
    private Income parseIncome(BufferedReader reader) throws IOException {
        return createIncome(
                LocalDateTime.parse(reader.readLine()),
                Integer.parseInt(reader.readLine()),
                reader.readLine(),
                reader.readLine(),
                reader.readLine()
        );
    }

    private Expense parseExpense(BufferedReader reader) throws IOException {
        return createExpense(
                LocalDateTime.parse(reader.readLine()),
                Integer.parseInt(reader.readLine()),
                reader.readLine(),
                reader.readLine(),
                reader.readLine()
        );
    }

    private Transfer parseTransfer(BufferedReader reader) throws IOException {
        return createTransfer(
                LocalDateTime.parse(reader.readLine()),
                Integer.parseInt(reader.readLine()),
                reader.readLine(),
                reader.readLine(),
                reader.readLine()
        );
    }

    // Creating methods for each transaction type
    private Income createIncome(LocalDateTime dateTime, int amount, String accountName, String note, String categoryName) {
        Income income = new Income();
        setTransactionData(income, dateTime, amount, accountName, note, categoryName);
        income.setCategory(findCategoryByName(app.getIncomeCategories(), categoryName));
        return income;
    }

    private Expense createExpense(LocalDateTime dateTime, int amount, String accountName, String note, String categoryName) {
        Expense expense = new Expense();
        setTransactionData(expense, dateTime, amount, accountName, note, categoryName);
        expense.setCategory(findCategoryByName(app.getExpenseCategories(), categoryName));
        return expense;
    }

    private Transfer createTransfer(LocalDateTime dateTime, int amount, String sourceAccountName, String destinationAccountName, String note) {
        Transfer transfer = new Transfer();
        transfer.setDate(dateTime);
        transfer.setAmount(amount);
        transfer.setSource(findAccountByName(app.getAccounts(), sourceAccountName));
        transfer.setDestination(findAccountByName(app.getAccounts(), destinationAccountName));
        transfer.setNote(note);
        return transfer;
    }

    // Helper methods to set shared transaction data
    private void setTransactionData(Transaction transaction, LocalDateTime dateTime, int amount, String accountName, String note, String categoryName) {
        transaction.setDate(dateTime);
        transaction.setAmount(amount);
        transaction.setSource(findAccountByName(app.getAccounts(), accountName));
        transaction.setNote(note);
    }

    // Helper interfaces
    @FunctionalInterface
    private interface TransactionParser<T> {
        T parse(BufferedReader reader) throws IOException;
    }

    @FunctionalInterface
    private interface FileReaderHandler<T> {
        Vector<T> readFile(File file);
    }

    private Category findCategoryByName(Vector<Category> categories, String name) {
        return categories.stream().filter(cat -> cat.getName().equals(name)).findFirst().orElse(null);
    }

    private Account findAccountByName(Vector<Account> accounts, String name) {
        return accounts.stream().filter(acc -> acc.getName().equals(name)).findFirst().orElse(null);
    }
}

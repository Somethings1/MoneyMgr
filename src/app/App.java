package app;

import java.io.*;
import java.util.Vector;

import transaction.properties.Account;
import transaction.properties.Category;

public class App {
    private static App instance;
    private Vector<Category> incomeCategories;
    private Vector<Category> expenseCategories;
    private Vector<Account> accounts;
    private final String incomeCategoryFilePath = "income_categories.txt";
    private final String expenseCategoryFilePath = "expense_categories.txt";
    private final String accountFilePath = "accounts.txt";

    // Private constructor for Singleton pattern
    private App() {
        incomeCategories = new Vector<>();
        expenseCategories = new Vector<>();
        accounts = new Vector<>();
        loadIncomeCategories();
        loadExpenseCategories();
        loadAccounts();
    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public Vector<Category> getIncomeCategories() {
        return incomeCategories;
    }

    public Vector<Category> getExpenseCategories() {
        return expenseCategories;
    }

    public Vector<Account> getAccounts() {
        return accounts;
    }

    private <T> void saveToFile(String filePath, Vector<T> items, ItemSerializer serializer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T item : items) {
                writer.write(serializer.serialize(item));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	private <T> void loadFromFile(String filePath, ItemDeserializer deserializer, Vector<T> items) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                items.add((T) deserializer.deserialize(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIncomeCategories() {
        saveToFile(incomeCategoryFilePath, incomeCategories, 
            item -> ((Category) item).getEmoji() + "," + ((Category) item).getName() + "," + ((Category) item).getBudget());
    }

    public void saveExpenseCategories() {
        saveToFile(expenseCategoryFilePath, expenseCategories, 
            item -> ((Category) item).getEmoji() + "," + ((Category) item).getName() + "," + ((Category) item).getBudget());
    }

    public void saveAccounts() {
        saveToFile(accountFilePath, accounts, 
            item -> ((Account) item).getName() + "," + ((Account) item).getGroup() + "," + ((Account) item).getBalance() + "," + ((Account) item).isIncludedInTotal());
    }

    public void loadIncomeCategories() {
        loadFromFile(incomeCategoryFilePath, line -> {
            String[] parts = line.split(",");
            return new Category(parts[0], parts[1], Integer.parseInt(parts[2]));
        }, incomeCategories);
    }

    public void loadExpenseCategories() {
        loadFromFile(expenseCategoryFilePath, line -> {
            String[] parts = line.split(",");
            return new Category(parts[0], parts[1], Integer.parseInt(parts[2]));
        }, expenseCategories);
    }

    public void loadAccounts() {
        loadFromFile(accountFilePath, line -> {
            String[] parts = line.split(",");
            return new Account(parts[0], parts[1], Integer.parseInt(parts[2]), Boolean.parseBoolean(parts[3]));
        }, accounts);
    }

    public void addIncomeCategory(Category category) {
        incomeCategories.add(category);
        saveIncomeCategories();
    }

    public void addExpenseCategory(Category category) {
        expenseCategories.add(category);
        saveExpenseCategories();
    }

    public void modifyIncomeCategory(Category oldCategory, Category newCategory) {
    	int index = findIncomeCategory(oldCategory);
        if (isValidIndex(index, incomeCategories.size())) {
            incomeCategories.set(index, newCategory);
            saveIncomeCategories();
        }
    }

    public void modifyExpenseCategory(Category oldCategory, Category newCategory) {
    	int index = findExpenseCategory(oldCategory);
        if (isValidIndex(index, expenseCategories.size())) {
            expenseCategories.set(index, newCategory);
            saveExpenseCategories();
        }
    }

    public void removeIncomeCategory(Category category) {
    	int index = findIncomeCategory(category);
        if (isValidIndex(index, incomeCategories.size())) {
            incomeCategories.remove(index);
            saveIncomeCategories();
        }
    }

    public void removeExpenseCategory(Category category) {
    	int index = findExpenseCategory(category);
        if (isValidIndex(index, expenseCategories.size())) {
            expenseCategories.remove(index);
            saveExpenseCategories();
        }
    }

    public void addAccount(Account account) {
        accounts.add(account);
        saveAccounts();
    }

    public void modifyAccount(Account oldAccount, Account newAccount) {
    	int index = findAccount(oldAccount);
        if (isValidIndex(index, accounts.size())) {
            accounts.set(index, newAccount);
            saveAccounts();
        }
    }

    public void removeAccount(Account account) {
    	int index = findAccount(account);
        if (isValidIndex(index, accounts.size())) {
            accounts.remove(index);
            saveAccounts();
        }
    }
    
    public Account findAccountByName(String accountName) {
        for (Account account : accounts) {
            if (account.getName().equals(accountName)) {
                return account;
            }
        }
        return null;
    }
    
    public Category findCategoryByName(String categoryName) {
        for (Category category : incomeCategories) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        for (Category category : expenseCategories) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null; // Category not found
    }
    
    private int findAccount (Account account) {
    	for (int i = 0; i < accounts.size(); i++)
    		if (account.equals(accounts.elementAt(i)))
    			return i;
    	return -1;
    }
    
    private int findIncomeCategory (Category category) {
    	for (int i = 0; i < incomeCategories.size(); i++)
    		if (category.equals(incomeCategories.elementAt(i)))
    			return i;
    	return -1;
    }
    
    private int findExpenseCategory (Category category) {
    	for (int i = 0; i < expenseCategories.size(); i++)
    		if (category.equals(expenseCategories.elementAt(i)))
    			return i;
    	return -1;
    }

    private boolean isValidIndex(int index, int size) {
        return index >= 0 && index < size;
    }

    // Functional interfaces for serialization and deserialization
    @FunctionalInterface
    interface ItemSerializer {
        String serialize(Object item);
    }

    @FunctionalInterface
    interface ItemDeserializer {
        Object deserialize(String line);
    }
}


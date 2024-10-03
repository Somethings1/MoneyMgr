package transaction.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import transaction.properties.Account;
import transaction.properties.Category;

public class Income extends Transaction {
    private Category category;
    
    public Income () {}

    public Income(LocalDateTime date, int amount, Account source, String note, Category category) {
        super(date, amount, source, note);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public void saveToFile() {
        LocalDateTime date = getDate();
        String filePath = generateFilePath(date);

        // Increment the seconds until a unique file is found
        while (fileExists(filePath)) {
            date = date.plusSeconds(1); // Increment the second
            filePath = generateFilePath(date); // Generate a new file path
        }

        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write("Income\n");
            writer.write(date + "\n");
            writer.write(getAmount() + "\n");
            writer.write(getSource().getName() + "\n");
            writer.write(getNote() + "\n");
            writer.write(category.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to generate the file path based on the date
    private String generateFilePath(LocalDateTime date) {
        String formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd/HH_mm_ss"));
        return formattedDate + ".txt";
    }

    // Helper method to check if the file exists
    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}

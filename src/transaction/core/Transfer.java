package transaction.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import transaction.properties.Account;

public class Transfer extends Transaction {
    protected Account destination;

    public Account getDestination() {
        return destination;
    }

    public void setDestination(Account destination) {
        this.destination = destination;
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
        file.getParentFile().mkdirs(); // Ensure directories are created

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write("Transfer\n"); // Indicate this is a transfer entry
            writer.write(date + "\n");
            writer.write(getAmount() + "\n");
            writer.write(getSource().getName() + "\n");
            writer.write(destination.getName() + "\n");
            writer.write(getNote() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to generate the file path based on the date
    private String generateFilePath(LocalDateTime date) {
        String formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd/HH_mm_ss"));
        return "./" + formattedDate + ".txt";
    }

    // Helper method to check if the file exists
    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

}

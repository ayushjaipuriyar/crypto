import java.io.*;
import java.util.*;

public class COA {
    public static void main(String[] args) throws IOException {
        // Simple example ciphertext for testing
        String ciphertext = "Xlmw mw xlmw mw!";

        // Read password list from file
        String passwordFile = "passwords.txt";
        List<String> passwords = readPasswords(passwordFile);

        // Iterate through passwords and attempt decryption
        for (String key : passwords) {
            String plaintext = Rotor96Crypto.encdec(2, key, ciphertext);
            if (isEnglish(plaintext)) { // Check if output is valid English text
                System.out.println("Key found: " + key);
                System.out.println("Decoded message: " + plaintext);
                break; // Stop after finding the first valid key
            }
        }
    }

    // Reads passwords into a list
    public static List<String> readPasswords(String filePath) throws IOException {
        List<String> passwords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String password;
            while ((password = br.readLine()) != null) {
                passwords.add(password.trim());
            }
        }
        return passwords;
    }

    // Basic heuristic to check if text contains common English words
    public static boolean isEnglish(String text) {
        String[] commonWords = { "the", "and", "to", "of", "a", "in", "that", "is", "was", "it" };
        for (String word : commonWords) {
            if (text.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }
}

// Placeholder for the Rotor96Crypto class — must be linked to
// Rotor96Crypto.java
// Ensure Rotor96Crypto.java is in the same directory or properly imported.

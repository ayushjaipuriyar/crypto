import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KPA {
    // Assuming Rotor96Crypto is already defined as mentioned in your task
    private static final Rotor96Crypto rotor96Crypto = new Rotor96Crypto();

    public static void main(String[] args) {
        // Load the ciphertext and the passwords
//        String ciphertext = loadCiphertext("ciphertext1.txt");
        List<String> passwords = loadPasswords("passwords");
        String ciphertext = "b]h:m SI9euXf41r32ygPfc.FM@#oWdEPA_$RU*{PG\\xP(T|a,4]+Z@BmLW&uF[OSMH6#HtbOjDeVK%jd]zOFv\"\"!a+/Hc|#d\\<8!yMT Z'>sB:`UfggK4G%A^t,Nvc&Oq+RUq0CgjJZF:!pQ['X'k{]b9t,%IfU%8=Q<sbvx{J<K[/Ir>34vU%HX}6{XY$jHnE/n~,3JZsWvhve~hL,_rsiA[6_$Fps%[=UStmaE2rZp`;~s<c0$k/9oP;e8Gi9oAlss7|qjv{2iu";

        // The known plaintext starts with "We"
        String knownPlaintextStart = "We";

        // List to store all the matching decrypted texts and corresponding keys
        List<String> decryptedMessages = new ArrayList<>();
        List<String> matchingKeys = new ArrayList<>();

        // Loop through all possible passwords in the dictionary
        for (String password : passwords) {
            // Try decrypting with each password
            String decryptedText = Rotor96Crypto.encdec(2, password, ciphertext);

            // Check if the decrypted text starts with "We"
            if (decryptedText.startsWith(knownPlaintextStart)) {
                decryptedMessages.add(decryptedText); // Save the matching decrypted message
                matchingKeys.add(password); // Save the corresponding key
            }
        }

        // Save results to a file
        saveResults(decryptedMessages, matchingKeys);

        // If no matching keys were found, print a message
        if (decryptedMessages.isEmpty()) {
            System.out.println("No valid keys found that start with 'We'.");
        } else {
            // Print a confirmation message
            System.out.println("Decrypted results have been saved to 'decrypted_results.txt'.");
        }
    }

    // Helper method to load the ciphertext from a file
    private static String loadCiphertext(String filename) {
        StringBuilder ciphertext = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ciphertext.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ciphertext.toString();
    }

    // Helper method to load the list of passwords from a file
    private static List<String> loadPasswords(String filename) {
        List<String> passwords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                passwords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return passwords;
    }

    // Helper method to save the results (keys and plaintexts) to a file
    private static void saveResults(List<String> decryptedMessages, List<String> matchingKeys) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("decrypted_results.txt"))) {
            for (int i = 0; i < decryptedMessages.size(); i++) {
                writer.write("Key: " + matchingKeys.get(i));
                writer.newLine();
                writer.write("Decrypted message: " + decryptedMessages.get(i));
                writer.newLine();
                writer.newLine(); // Add extra line between entries
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
import java.io.*;
import java.util.*;

public class COA {
    private static final Rotor96Crypto rotor96Crypto = new Rotor96Crypto();

    public static void main(String[] args) throws IOException {
        // Simple example ciphertext for testing
        String ciphertext = "h}G>gLgNl[F7Bs+tLy&T<`.lY&o3<ijJj|'ruWcmUqe=b:+;L,MI,hs0-a7;bAvQX'<SiaWHX.w`!2=q(J,3lmrCaGl+SuhWUiLnbFPee,rbd.q)w0]!Fjnc}SvItbjNa\\FL>6m~?R\u007F;-A\u007F@E9BK/[I2w.4F(`L6\"@5Tg\u007FX`eoV9!5i5fvGQ{sR^6RF\\SZ8pQm!#b<=]dFBVM]7\\=;ov\"'AZ~\\>~w\"'6L>Z1nE2FqMW3--Z.[N}o/^R6|E`LdvVE\"r#)2?%HXsw8kfJ5fv+3qj}s|Oy7)us//s/|C%c(%Z)8nan[nR]{\\bkW\"G(ms~rUaH\"iaJm:Ikn OV\\-I9-F/{3IwEpofO>W\"7atth-'ei4`;E_Fh49,G.f{m#,DL^/yY=uXb£O2x*,4V|.;xN;CT\"Y`W\"L>]M#?=@I(!";

        // Read password list from file
        String passwordFile = "passwords";
        List<String> passwords = readPasswords(passwordFile);

        // Store scores for each key
        Map<String, Integer> keyScores = new HashMap<>();

        // Iterate through passwords and attempt decryption
        for (String key : passwords) {
            String plaintext = Rotor96Crypto.encdec(2, key, ciphertext);
            int score = analyze(plaintext);
            keyScores.put(key, score);
        }

        // Find key with highest score
        String bestKey = Collections.max(keyScores.entrySet(), Map.Entry.comparingByValue()).getKey();
        String bestPlaintext = Rotor96Crypto.encdec(2, bestKey, ciphertext);

        // Output the best result
        System.out.println("Best key found: " + bestKey);
        System.out.println("Decoded message: " + bestPlaintext);
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

    // Analyzes text and returns a score based on English-like characteristics
    public static int analyze(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // Common English words and patterns
        String[] commonWords = { "the", "and", "for", "are", "but", "not", "you", "all", "any", "can",
                "had", "her", "was", "one", "our", "out", "day", "get", "has", "him",
                "his", "how", "man", "new", "now", "old", "see", "two", "way", "who" };

        char[] frequentLetters = { 'e', 't', 'a', 'o', 'i', 'n', 's', 'h', 'r', 'd', 'l', 'u' };

        // Letter frequency count
        Map<Character, Integer> letterCount = new HashMap<>();
        for (char c : text.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
            }
        }

        // Count occurrences of frequent letters
        int frequentLetterCount = 0;
        for (char letter : frequentLetters) {
            frequentLetterCount += letterCount.getOrDefault(letter, 0);
        }

        // Count occurrences of common words
        int wordMatches = 0;
        for (String word : commonWords) {
            if (text.toLowerCase().contains(word)) {
                wordMatches++;
            }
        }

        // Calculate score: letter frequency + word matches (weighted equally for now)
        int score = frequentLetterCount + (wordMatches * 10);
        System.out.println("Score for text: " + score);
        return score;
    }
}
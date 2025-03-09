import java.io.*;
import java.util.*;
import java.io.FileWriter;
import java.util.concurrent.*;

public class COA {
    private static final Rotor96Crypto rotor96Crypto = new Rotor96Crypto();
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException {
        // Simple example ciphertext for testing
        String ciphertext = "h}G>gLgNl[F7Bs+tLy&T<`.lY&o3<ijJj|'ruWcmUqe=b:+;L,MI,hs0-a7;bAvQX'<SiaWHX.w`!2=q(J,3lmrCaGl+SuhWUiLnbFPee,rbd.q)w0]!Fjnc}SvItbjNa\\FL>6m~?R\u007F;-A\u007F@E9BK/[I2w.4F(`L6\"@5Tg\u007FX`eoV9!5i5fvGQ{sR^6RF\\SZ8pQm!#b<=]dFBVM]7\\=;ov\"'AZ~\\>~w\"'6L>Z1nE2FqMW3--Z.[N}o/^R6|E`LdvVE\"r#)2?%HXsw8kfJ5fv+3qj}s|Oy7)us//s/|C%c(%Z)8nan[nR]{\\bkW\"G(ms~rUaH\"iaJm:Ikn OV\\-I9-F/{3IwEpofO>W\"7atth-'ei4`;E_Fh49,G.f{m#,DL^/yY=uXb£O2x*,4V|.;xN;CT\"Y`W\"L>]M#?=@I(!";

        // Read password list from file
        String passwordFile = "passwords";
        List<String> passwords = readPasswords(passwordFile);
        System.out.println("Number of passwords: " + passwords.size());

        // Run the standard decryption process
//        decryptWithScoring(ciphertext, passwords);

        // Perform experimental approach to find minimum chars for unambiguous decoding
        experimentalApproach(ciphertext, passwords);
    }

    public static void decryptWithScoring(String ciphertext, List<String> passwords) {
        Map<String, Double> keyScores = new HashMap<>();

        for (String key : passwords) {
            String plaintext = Rotor96Crypto.encdec(2, key, ciphertext);
            double score = analyze(plaintext);
            keyScores.put(key, score);
        }

        // Sort by score (higher score means better match)
        String bestKey = Collections.max(keyScores.entrySet(), Map.Entry.comparingByValue()).getKey();
        String bestPlaintext = Rotor96Crypto.encdec(2, bestKey, ciphertext);
        System.out.println("Best key: " + bestKey + " | Score: " + keyScores.get(bestKey));
        System.out.println("Decoded text: " + bestPlaintext);
    }

public static void experimentalApproach(String ciphertext, List<String> passwords) {
    int minCharsRequired = ciphertext.length();
    Map<String, Map<Integer, Double>> passwordScores = new ConcurrentHashMap<>();
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    try (PrintWriter writer = new PrintWriter(new FileWriter("password_scores.csv"))) {
        writer.println("Password,CharsUsed,Score,Plaintext");

        // Collect scores for dynamic threshold calculation
        List<Integer> allScores = Collections.synchronizedList(new ArrayList<>());

        // Iterate through substring lengths of ciphertext
        for (int len = 1; len <= ciphertext.length(); len++) {
//            System.out.println("\nProcessing substring length: " + len);
            String partialCiphertext = ciphertext.substring(0, len);
            CountDownLatch latch = new CountDownLatch(passwords.size());
            final int currentLen = len;
            for (String key : passwords) {
                executor.submit(() -> {
                    try {
//                        System.out.println("Thread started for key: " + key + " | Length: " + currentLen);
                        String plaintext = Rotor96Crypto.encdec(2, key, partialCiphertext);
                        double score = analyze(plaintext);

                        passwordScores.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(currentLen, score);
                        allScores.add((int) score);

                        synchronized (writer) {
                            writer.println(key + "," + currentLen + "," + score + "," + plaintext.replaceAll(",", "comma"));
                        }
//                        System.out.println("Key: " + key + " | Length: " + currentLen + " | Score: " + score);
//                        System.out.println("Partial Plaintext: " + plaintext);

                    } catch (Exception e) {
                        System.err.println("Error processing key: " + key + " | Length: " + currentLen);
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
//                        System.out.println("Thread finished for key: " + key + " | Length: " + currentLen);
                    }
                });
            }
            latch.await(); // Wait for all tasks to complete for this substring length
        }

        // Calculate dynamic threshold
//        double dynamicThreshold = calculateDynamicThreshold(allScores);
//        System.out.println("Dynamic Threshold: " + dynamicThreshold);

        // Find minimum chars needed for unambiguous decoding
        for (int len = 1; len <= ciphertext.length(); len++) {
            for (String key : passwords) {
                double score = passwordScores.getOrDefault(key, new HashMap<>()).getOrDefault(len, 0.0);
                if (score >= 150) {
                    minCharsRequired = Math.min(minCharsRequired, len);
                    System.out.println("Key: " + key + " | Chars used: " + len + " | Score: " + score + " (Above threshold)");
                    break;
                }
            }
        }
    } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
    } finally {
        executor.shutdown();
    }

    System.out.println("Minimum characters needed for unambiguous decoding: " + minCharsRequired);
}
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

    public static double analyze(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

//        char[] frequentLetters = { 'e', 't', 'a', 'o', 'i', 'n', 's', 'h', 'r', 'd', 'l', 'u' };
//        double[] letterWeights = { 2.0, 1.9, 1.8, 1.7, 1.6, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0, 0.9 };

        String[] repeatedLetters = { "ss", "ee", "tt", "ff", "ll", "mm", "oo" };
        String[] shortWords = { "a", "i", "of", "to", "in", "it", "is", "be", "as", "at", "so", "we", "he", "by", "or", "on", "do", "if", "me", "my", "up", "an", "go", "no", "us", "am" };

        String[] oneLetterWords = { "a", "i" };
        String[] commonTwoLetterWords = { "of", "to", "in", "it", "is", "be", "as", "at", "so", "we", "he", "by", "or", "on", "do", "if", "me", "my", "up", "an", "go", "no", "us", "am" };
        String[] commonThreeLetterWords = { "the", "and", "for", "are", "but", "not", "you", "all", "any", "can", "had", "her", "was", "one", "our", "out", "day", "get", "has", "him", "his", "how", "man", "new", "now", "old", "see", "two", "way", "who", "boy", "did", "its", "let", "put", "say", "she", "too", "use" };
        String[] commonFourLetterWords = { "that", "with", "have", "this", "will", "your", "from", "they", "know", "want", "been", "good", "much", "some", "time" };

        String[] commonBigrams = { "th", "er", "on", "an", "re", "he", "in", "ed", "nd", "ha", "at", "en", "es", "of", "or", "nt", "ea", "ti", "to", "it", "st", "io", "le", "is", "ou", "ar", "as", "de", "rt", "ve" };
        String[] commonTrigrams = { "the", "and", "tha", "ent", "ion", "tio", "for", "nde", "has", "nce", "edt", "tis", "oft", "sth", "men" };
        String[] commonQuadgrams = { "tion", "ther", "with", "here", "that", "ould", "ight", "have", "hich", "whic", "this", "thin", "they", "atio" };
        char[] specialCharacters = { '!', '@', '£', '$', '%', '^', '&', '*', '(', ')' };

        double score = 0;
        String lowerText = text.toLowerCase();
        String[] words = lowerText.split("\\s+");

        if (words.length < 5) {
            return 0;
        }
        for (char c : lowerText.toCharArray()) {
            for (char special : specialCharacters) {
                if (c == special) {
                    score -= 0.5; // slight penalty for each special character
                }
            }
        }
        // Letter frequency score
//        for (char c : lowerText.toCharArray()) {
//            for (int i = 0; i < frequentLetters.length; i++) {
//                if (c == frequentLetters[i]) {
//                    score += letterWeights[i];
//                }
//            }
//        }

        for (String word : words) {
            if (Arrays.asList(commonTwoLetterWords).contains(word)) score += 2;
            if (Arrays.asList(commonThreeLetterWords).contains(word)) score += 3;
            if (Arrays.asList(commonFourLetterWords).contains(word)) score += 4;
            if (Arrays.asList(oneLetterWords).contains(word)) score += 1;
        }



        // Bigram and repeated letter scores
        for (String bigram : commonBigrams) {
            score += countOccurrences(lowerText, bigram) * 3;
        }

        for (String trigram : commonTrigrams) {
            score += countOccurrences(lowerText, trigram) * 4;
        }

        for (String repeat : repeatedLetters) {
            score += countOccurrences(lowerText, repeat) * 2;
        }


        for (String word : shortWords) {
            score += countOccurrences(lowerText, word) * 2;
        }

        // Quadgram matches
        for (String quadgram : commonQuadgrams) {
            score += countOccurrences(lowerText, quadgram) * 2;
        }

        // Scale score based on length to favor longer coherent plaintexts
        score *= Math.log10(lowerText.length()+10);

        return score;
    }
    public static int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
    public static double calculateDynamicThreshold(List<Integer> scores) {
        if (scores.isEmpty()) {
            return 0;
        }

        // Calculate mean (average) score
        double mean = scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        // Calculate standard deviation
        double variance = scores.stream()
                .mapToDouble(score -> Math.pow(score - mean, 2))
                .average()
                .orElse(0.0);
        double stdDev = Math.sqrt(variance);

        // Dynamic threshold: mean + (1.5 * standard deviation)
        return mean + (1.5 * stdDev);
    }

}
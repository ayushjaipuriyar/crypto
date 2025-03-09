import java.io.*;
import java.util.*;

public class CSVReader {
	public static void main(String[] args) {
		String csvFile = "password_scores.csv";
		String line;
		String csvSplitBy = ",";
		List<String[]> rows = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			// Read and validate the header
			String header = br.readLine();
			if (header == null || !header.equals("Password,CharsUsed,Score,Plaintext")) {
				System.out.println("Invalid or missing header.");
				return;
			}

			// Read and store rows
			while ((line = br.readLine()) != null) {
				String[] values = line.split(csvSplitBy);
				if (values.length == 4) {
					try {
						Double.parseDouble(values[1]); // CharsUsed
						Double.parseDouble(values[2]); // Score
						rows.add(values);
					} catch (NumberFormatException e) {
						System.out.println("Skipping invalid row: " + line);
					}
				}
			}

			// Sort rows by CharsUsed (descending)
			rows.sort((a, b) -> {
				double charsUsedA = Double.parseDouble(a[1]);
				double charsUsedB = Double.parseDouble(b[1]);
				return Double.compare(charsUsedB, charsUsedA); // Descending order
			});

			// Filter and print rows with Score > 4190
			System.out.println(header); // Print header for clarity
			for (String[] row : rows) {
				double score = Double.parseDouble(row[2]);
				if (score > 674.0) {
					System.out.println(String.join(",", row));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
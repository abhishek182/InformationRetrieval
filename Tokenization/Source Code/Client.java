import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * This is client program that takes the directory and create tokens by reading
 * each and every file present in the directory.
 * 
 * @author Abhishek Gupta (axg137230)
 * 
 */
public class Client {
	/**
	 * This stores the directory path of the file repository.
	 */
	private final static String directory = "/people/cs/s/sanda/cs6322/Cranfield";
	/**
	 * This is the instance of type Token which stores the tokens created.
	 */
	private static Tokens tokens;
	private static StemTokens stemTokens;

	static {
		tokens = new Tokens();
		stemTokens = new StemTokens(tokens);
	}

	/**
	 * This method is the starting point.
	 * 
	 * @param args
	 *            command line arguments.
	 */
	public static void main(String[] args) {
		try {
			long startTime = System.currentTimeMillis();
			File[] listOfFiles = getAllFiles(directory);
			int numberOfFiles = listOfFiles.length;
			if (numberOfFiles != 0) {
				for (File file : listOfFiles) {
					parseFile(file);
				}
			} else {
				System.out.println("Empty folder");
			}
			// tokens.printTokens();
			System.out.println("Number of tokens : " + tokens.numberOfTokens());
			System.out.println("Number of unique tokens : "
					+ tokens.numberOfUniqueTokens());
			System.out.println("Number of tokens that occur only once : "
					+ tokens.numberOfTokenWithFreq(1));
			System.out.println("Average number of tokens per document : "
					+ tokens.averageTokensPerDocument(numberOfFiles));
			System.out.println("30 most frequent words : ");
			tokens.getMostFrequentTokens(30);
			long endTime = System.currentTimeMillis();
			System.out.println("Time taken is : "
					+ (double) (endTime - startTime) / 1000);

			System.out
					.println("*****************************************************");

			stemTokens.stemAllTokens();
			System.out.println("Number of stems : "
					+ stemTokens.numberOfStems());
			System.out.println("Number of distinct stems : "
					+ stemTokens.numberOfUniqueStems());
			System.out.println("Number of stems that occur only once : "
					+ stemTokens.numberOfStemsWithFreq(1));
			System.out.println("Average number of stems per document : "
					+ stemTokens.averageStemsPerDocument(numberOfFiles));
			System.out.println("30 Most frequent stems : ");
			stemTokens.getMostFrequentStems(30);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method parses single file and creates tokens from it.
	 * 
	 * @param file
	 *            a File type
	 * @throws FileNotFoundException
	 */
	private static void parseFile(File file) throws FileNotFoundException {
		ArrayList<String> listOfRawTokensInFile = Util.removeSGMLTags(file);
		for (String rawToken : listOfRawTokensInFile) {
			ArrayList<String> processedTokens = Util.processToken(rawToken);
			if (processedTokens != null) {
				for (String processedToken : processedTokens) {
					tokens.insertToken(processedToken);
				}
			}

		}

	}

	/**
	 * This method returns the list of files in a directory.
	 * 
	 * @param directory
	 *            a String type.
	 * @return An Array of File type.
	 */
	private static File[] getAllFiles(String directory) {
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		// System.out.println("Number of files : " + listOfFiles.length);
		return listOfFiles;
	}

}

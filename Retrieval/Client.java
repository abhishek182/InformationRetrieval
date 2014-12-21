import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Client {
	/**
	 * This stores the directory path of the file repository.
	 */
	private static String cranfieldPrefix;

	/**
	 * This stores the location of index file.
	 */
	private static final String indexFile = "resources/Index_Version2.uncompress";

	/**
	 * This stores the location of doc.info file.
	 */
	private static final String docInfoFile = "resources/docs.info";

	/**
	 * This stores the location of file containing queries..
	 */
	private static String queryFile;

	/**
	 * This constant stores the path of the file containing stop words.
	 */
	private static String stopWordFile;

	/**
	 * This holds the index of the document collection we are using.
	 */
	private static Index index;

	/**
	 * Instance of scanner to read the file.
	 */
	public static Scanner scanner;

	/**
	 * Initializing the index.
	 */
	static {
		index = new Index();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		cranfieldPrefix = args[0];
		queryFile = args[1];
		stopWordFile = args[2];
		long starTime = System.currentTimeMillis();
		// Constructing index.
		index.constructIndex(indexFile);
		// Loading document information.
		index.loadDocInfo(docInfoFile);
		// Getting all stop words.
		TreeMap<String, String> stopWords = Util
				.getStopWordsFromFile(stopWordFile);
		// Getting all the queries.
		String[] queries = getQueries(queryFile);
		int queryCount = 1;
		// Iterating over all queries.
		for (String query : queries) {
			if (query.length() != 0) {
				Query q = new Query();
				// Create index of the query and removing stop words.
				q.indexQuery(query, stopWords);
				// Calculate score of this query with each document and rank
				// them.
				q.generateRaking(index);
				// Print the top 10 ranked documents using both weighing
				// functions.
				printQueryRanking(query, queryCount++, q.getRankingByW1(),
						q.getRankingByW2(), cranfieldPrefix);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.printf("\n%s : %.2f secs\n", "Total time taken",
				(endTime - starTime) / 1000.0d);
	}

	/**
	 * Method prints the top 10 documents with highest scores for a query along
	 * with the headline of the document.
	 * 
	 * @param query
	 * @param queryCount
	 * @param rankingByW1
	 * @param rankingByW2
	 * @param cranfieldPrefix
	 * @throws IOException
	 */
	private static void printQueryRanking(String query, int queryCount,
			Map<Integer, Double> rankingByW1, Map<Integer, Double> rankingByW2,
			String cranfieldPrefix) throws IOException {
		System.out
				.println("***************************************************************************************");
		System.out.println("Q" + queryCount + ": " + query);
		System.out
				.println("\n************************* Using Weighing Function 1 ***********************************");
		System.out.printf("%4s\t%5s\t%5s\t%s\n", "Rank", "DocId", "Score",
				"Document Headline");
		Set<Integer> topDocsByW1 = rankingByW1.keySet();
		Iterator<Integer> topDocByW1 = topDocsByW1.iterator();
		for (int i = 0; i < 10 && topDocByW1.hasNext(); i++) {
			int docId = topDocByW1.next();
			System.out
					.printf("%4d\t%5d\t%.2f\t%s\n",
							i + 1,
							docId,
							rankingByW1.get(docId),
							Util.getDocTitle(cranfieldPrefix, docId).replace(
									"\n", " "));
		}
		System.out
				.println("\n************************* Using Weighing Function 2 ***********************************");
		System.out.printf("%4s\t%5s\t%5s\t%s\n", "Rank", "DocId", "Score",
				"Document Headline");
		Set<Integer> topDocsByW2 = rankingByW2.keySet();
		Iterator<Integer> topDocByW2 = topDocsByW2.iterator();
		for (int i = 0; i < 10 && topDocByW2.hasNext(); i++) {
			int docId = topDocByW2.next();
			System.out
					.printf("%4d\t%5d\t%.2f\t%s\n",
							i + 1,
							docId,
							rankingByW2.get(docId),
							Util.getDocTitle(cranfieldPrefix, docId).replace(
									"\n", " "));
		}
	}

	/**
	 * This methods returns list of queries from the given file.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	private static String[] getQueries(String file)
			throws FileNotFoundException {
		scanner = new Scanner(new File(file));
		StringBuilder allQueries = new StringBuilder();
		while (scanner.hasNextLine()) {
			allQueries.append(scanner.nextLine()).append(" ");
		}
		return allQueries.toString().split("Q[0-9:]+");
	}

}

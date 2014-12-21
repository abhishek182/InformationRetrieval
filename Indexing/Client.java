import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is client program that takes the directory and create tokens by reading
 * each and every file present in the directory.
 * 
 * @author Abhishek Gupta (axg137230)
 * 
 */
public class Client {
	/**
	 * This constant stores the directory path of the file repository.
	 */
	private static String directory;

	/**
	 * This constant stores the path of the file containing stopwords.
	 */
	private final static String stopWordFile = "/people/cs/s/sanda/cs6322/resourcesIR/stopwords";// "resources/stopwords";//

	/**
	 * This variable holds the path of the directory where output files are to
	 * be stored.
	 */
	private static String outputFilePath;

	/**
	 * This constant specifies the list of annotators required for creating
	 * lemma.
	 */
	private final static String annotators = "tokenize, ssplit, pos, lemma";

	/**
	 * This instance of Index represent the version 1.
	 */
	private static Index indexVersion1;

	/**
	 * This instance of Index represent the version 2.
	 */
	private static Index indexVersion2;

	/**
	 * This instance of Stemmer is needed for stemming.
	 */
	private static Stemmer stemmer;

	/**
	 * This instance of Lemmatizer is needed for creating lemmas.
	 */
	private static Lemmatizer lemmatizer;

	/**
	 * Static block initializing all the member variables.
	 */
	static {
		indexVersion1 = new Index();
		indexVersion2 = new Index();
		stemmer = new Stemmer();
		lemmatizer = new Lemmatizer(annotators);
	}

	/**
	 * This method is the starting point. It expects two command line arguments.
	 * 1. Path of document database. 2. Path of the output directory.
	 * 
	 * @param args
	 *            command line arguments.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err
					.println("You need to give database directory path and output directory path as arguments.");
			System.exit(0);
		}
		directory = args[0];
		outputFilePath = args[1];
		// Get list of all files.
		File[] listOfFiles = Util.getAllFiles(directory);
		// Get all the stop words in the sorted order.
		TreeMap<String, String> stopWords = Util
				.getStopWordsFromFile(stopWordFile);
		int numberOfFiles = listOfFiles.length;
		// 2D array to store the required information about each doc.
		Integer[][] docInfo = new Integer[numberOfFiles + 1][2];
		double timeForIndexVersion1 = 0.0d, timeForIndexVersion2 = 0.0d;
		long startTime, endTime;
		if (numberOfFiles != 0) {

			// Starting creating Index version 1.
			startTime = System.currentTimeMillis();
			for (File file : listOfFiles) {
				// Get the doc id.
				int docId = Integer.parseInt(file.getName().substring(9));
				// For version 1:
				// do lemmatization
				Map<String, Integer> lemmasFromFile = lemmatizer
						.lemmatize(file);
				// remove stop words,
				Map<String, Integer> lemmasWoStopWords = Util.removeStopWords(
						lemmasFromFile, stopWords);
				// create index.
				indexVersion1.creatIndex(docId, lemmasWoStopWords);
			}
			endTime = System.currentTimeMillis();
			// calculate total time taken
			timeForIndexVersion1 = (endTime - startTime) / 1000.0d;

			// indexVersion1.printIndex();
			// write uncompressed index of version 1 to a binary file.
			indexVersion1.writeIndexUncompressed(outputFilePath,
					"Index_Version1.uncompress");
			// compress index
			indexVersion1.compressIndex();
			// write compressed index of version 1 to a binary file.
			indexVersion1.writeIndexCompressed(outputFilePath,
					"Index_Version1.compress");

			// Starting creating Index version 2.
			startTime = System.currentTimeMillis();
			for (File file : listOfFiles) {
				int docId = Integer.parseInt(file.getName().substring(9));
				// For version 2:
				// generate tokens from the file
				Map<String, Integer> tokensFromFile = Util
						.getTokensFromFile(file);
				// do stemming
				Map<String, Integer> stemmedTokens = Util
						.getStemmedTokens(tokensFromFile);
				// remove stop words
				Map<String, Integer> stemmedTokensWoStopWords = Util
						.removeStopWords(stemmedTokens, stopWords);
				// update the information about current document.
				Util.updateDocInfo(docInfo, docId, stemmedTokens,
						stemmedTokensWoStopWords);
				// System.out.println("DocId: " + docId + " Max Freq: "
				// + docInfo[docId][0].toString() + " Doc length: "
				// + docInfo[docId][1].toString());
				// create index
				indexVersion2.creatIndex(docId, stemmedTokensWoStopWords);

			}
			endTime = System.currentTimeMillis();
			// calculate total time taken
			timeForIndexVersion2 = (endTime - startTime) / 1000.0d;
			// write documents information to a binary file
			writeDocInfo(docInfo, outputFilePath, "docs.info");
			// write uncompressed index of version 2 to a binary file.

			// indexVersion2.printIndex();
			indexVersion2.writeIndexUncompressed(outputFilePath,
					"Index_Version2.uncompress");
			// compress index
			indexVersion2.compressIndex();
			// write compressed index of version 2 to a binary file.
			indexVersion2.writeIndexCompressed(outputFilePath,
					"Index_Version2.compress");

			// display the required information.
			displayOutput(timeForIndexVersion1, timeForIndexVersion2);
		} else {
			System.err.println("Empty folder");
		}
	}

	/**
	 * This method writes the required information about the documents to a
	 * given binary file present at a given path.
	 * 
	 * @param docInfo
	 * @param filePath
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void writeDocInfo(Integer[][] docInfo, String filePath,
			String fileName) throws FileNotFoundException, IOException {
		File docInfoFile = new File(filePath + fileName);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				docInfoFile));
		oos.writeObject(docInfo);
		oos.flush();
		oos.close();
	}

	/**
	 * This method displays all the required information on the console.
	 * 
	 * @param timeForIndexVersion1
	 * @param timeForIndexVersion2
	 * @throws IOException
	 */
	private static void displayOutput(double timeForIndexVersion1,
			double timeForIndexVersion2) throws IOException {
		System.out
				.println("\n********************Index Version 1 Information********************\n");

		System.out.printf(
				"Elapsed time to create Index Version 1 : %.2f secs\n",
				timeForIndexVersion1);
		System.out.println("Size of Index Version 1 Uncompressed : "
				+ indexVersion1.getSize("uncompressed") / 1024 + " KB");
		System.out.println("Size of Index Version 1 Compressed : "
				+ indexVersion1.getSize("compressed") / 1024 + " KB");
		System.out.println("Number of inverted lists in Version 1 : "
				+ indexVersion1.size());

		System.out
				.println("\n********************Index Version 2 Information********************\n");

		System.out.printf(
				"Elapsed time to create Index Version 2 : %.2f secs\n",
				timeForIndexVersion2);
		System.out.println("Size of Index Version 2 Uncompressed : "
				+ indexVersion2.getSize("uncompressed") / 1024 + " KB");
		System.out.println("Size of Index Version 2 Compressed : "
				+ indexVersion2.getSize("compressed") / 1024 + " KB");
		System.out.println("Number of inverted lists in Version 2 : "
				+ indexVersion2.size());

		System.out
				.println("\nInformation of following terms in Index Version 1 : \n");
		System.out.format("%10s%20s%20s%25s\n", "Term", "Document Freq",
				"Term Freq", "Inverted List length");

		String l1 = lemmatizer.lemmatize("Reynolds".toLowerCase());
		System.out.format("%10s%20d%20d%25d B\n", "Reynolds",
				indexVersion1.documentFreq(l1), indexVersion1.termFreq(l1),
				indexVersion1.getPostingSize(l1));

		String l2 = lemmatizer.lemmatize("NASA".toLowerCase());
		System.out.format("%10s%20d%20d%25d B\n", "NASA",
				indexVersion1.documentFreq(l2), indexVersion1.termFreq(l2),
				indexVersion1.getPostingSize(l2));

		String l3 = lemmatizer.lemmatize("Prandtl".toLowerCase());
		System.out.format("%10s%20d%20d%25d B\n", "Prandtl",
				indexVersion1.documentFreq(l3), indexVersion1.termFreq(l3),
				indexVersion1.getPostingSize(l3));

		String l4 = lemmatizer.lemmatize("flow".toLowerCase());
		System.out.format("%10s%20d%20d%25d B\n", "flow",
				indexVersion1.documentFreq(l4), indexVersion1.termFreq(l4),
				indexVersion1.getPostingSize(l4));

		String l5 = lemmatizer.lemmatize("pressure".toLowerCase());
		System.out.format("%10s%20d%20d%25d B\n", "pressure",
				indexVersion1.documentFreq(l5), indexVersion1.termFreq(l5),
				indexVersion1.getPostingSize(l5));

		String l6 = lemmatizer.lemmatize("boundary".toLowerCase());
		System.out.format("%10s%20d%20d%25d B\n", "boundary",
				indexVersion1.documentFreq(l6), indexVersion1.termFreq(l6),
				indexVersion1.getPostingSize(l6));

		String l7 = lemmatizer.lemmatize("shock".toLowerCase());
		System.out.format("%10s%20d%20d%25d B\n", "shock",
				indexVersion1.documentFreq(l7), indexVersion1.termFreq(l7),
				indexVersion1.getPostingSize(l7));

		System.out
				.println("\nInformation of following terms in Index Version 2 : \n");
		System.out.format("%10s%20s%20s%25s\n", "Term", "Document Freq",
				"Term Freq", "Inverted List length");

		stemmer.add("Reynolds".toLowerCase().toCharArray(), "Reynolds".length());
		stemmer.stem();
		String s1 = stemmer.toString();

		System.out.format("%10s%20d%20d%25d B\n", "Reynolds",
				indexVersion2.documentFreq(s1), indexVersion2.termFreq(s1),
				indexVersion2.getPostingSize(s1));

		stemmer.add("NASA".toLowerCase().toCharArray(), "NASA".length());
		stemmer.stem();
		String s2 = stemmer.toString();

		System.out.format("%10s%20d%20d%25d B\n", "NASA",
				indexVersion2.documentFreq(s2), indexVersion2.termFreq(s2),
				indexVersion2.getPostingSize(s2));

		stemmer.add("Prandtl".toLowerCase().toCharArray(), "Prandtl".length());
		stemmer.stem();
		String s3 = stemmer.toString();

		System.out.format("%10s%20d%20d%25d B\n", "Prandtl",
				indexVersion2.documentFreq(s3), indexVersion2.termFreq(s3),
				indexVersion2.getPostingSize(s3));

		stemmer.add("flow".toLowerCase().toCharArray(), "flow".length());
		stemmer.stem();
		String s4 = stemmer.toString();

		System.out.format("%10s%20d%20d%25d B\n", "flow",
				indexVersion2.documentFreq(s4), indexVersion2.termFreq(s4),
				indexVersion2.getPostingSize(s4));

		stemmer.add("pressure".toLowerCase().toCharArray(), "pressure".length());
		stemmer.stem();
		String s5 = stemmer.toString();

		System.out.format("%10s%20d%20d%25d B\n", "pressure",
				indexVersion2.documentFreq(s5), indexVersion2.termFreq(s5),
				indexVersion2.getPostingSize(s5));

		stemmer.add("boundary".toLowerCase().toCharArray(), "boundary".length());
		stemmer.stem();
		String s6 = stemmer.toString();

		System.out.format("%10s%20d%20d%25d B\n", "boundary",
				indexVersion2.documentFreq(s6), indexVersion2.termFreq(s6),
				indexVersion2.getPostingSize(s6));

		stemmer.add("shock".toLowerCase().toCharArray(), "shock".length());
		stemmer.stem();
		String s7 = stemmer.toString();

		System.out.format("%10s%20d%20d%25d B\n", "shock",
				indexVersion2.documentFreq(s7), indexVersion2.termFreq(s7),
				indexVersion2.getPostingSize(s7));
	}
}

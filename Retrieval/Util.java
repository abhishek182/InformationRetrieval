import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class implement methods for manipulating/processing tokens and files..
 * 
 * @author Abhishek Gupta (axg137230)
 *
 */
public class Util {
	/**
	 * This is the constant storing the default token count.
	 */
	private static final int DEFAULT_TOKEN_COUNT = 1;

	/**
	 * This method takes a raw token and process it and returns a list of
	 * processed tokens.
	 * 
	 * @param rawToken
	 *            a String type.
	 * @return ArrayList of type String.
	 */
	public static ArrayList<String> processToken(String rawToken) {
		ArrayList<String> processedTokens = new ArrayList<String>();
		ArrayList<String> processedTokensTemp = new ArrayList<String>();

		// splitting tokens if it contains "-"
		if (rawToken.contains("-")) {
			for (String hypenRemovedString : processHypens(rawToken)) {
				processedTokens.add(hypenRemovedString.toLowerCase());
			}
		} else {
			processedTokens.add(rawToken.toLowerCase());
		}

		// removing "." from abbreviations like "U.N."
		for (String token : processedTokens) {
			if (token.contains(".") && token.length() != 1)
				processedTokensTemp.add(token.replaceAll("\\.", ""));
			else if (!token.equals("."))
				processedTokensTemp.add(token);
		}
		processedTokens.clear();

		// removing "'s" from the end of the tokens
		for (String token : processedTokensTemp) {
			if (token.endsWith("'s"))
				processedTokens.add(token.substring(0, token.length() - 2));
			else {
				String freshToken = token.replace("'", "");
				if (freshToken.length() > 0)
					processedTokens.add(freshToken);
			}

		}
		processedTokensTemp.clear();
		return processedTokens;
	}

	/**
	 * This method splits a string by hyphens in it and returns the list of
	 * strings.
	 * 
	 * @param rawToken
	 *            a String type
	 * @return List of type String.
	 */
	private static List<String> processHypens(String rawToken) {
		return Arrays.asList((rawToken.split("-")));
	}

	/**
	 * This method removes all the SGML tags in the file, remove everything
	 * except letters(lower case, upper case), numbers, dots, "'" and returns
	 * list of string by splitting them by spaces.
	 * 
	 * @param file
	 *            a File type.
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> removeSGMLTags(File file) {
		ArrayList<String> listOfTokensInFile = new ArrayList<String>();
		try {
			String fileToString = Jsoup.parse(file, "UTF-8").text();
			String[] tokensWithOutSpaces = fileToString
					.replaceAll("[^a-zA-Z0-9'.]", " ").replaceAll("\\s+", " ")
					.trim().split(" ");
			for (String rawToken : tokensWithOutSpaces) {
				if (rawToken.length() > 0)
					listOfTokensInFile.add(rawToken.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listOfTokensInFile;
	}

	/**
	 * This method sorts the map in the descending order of values and returns a
	 * sorted map.
	 * 
	 * @param map
	 *            a HashMap type
	 * @return Map type
	 */
	public static Map<Integer, Double> sortByValue(Map<Integer, Double> map) {
		// List of entries of map
		List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(
				map.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// creating a sorted map.
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
		for (Entry<Integer, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	/**
	 * This method parses single file and creates tokens from it.
	 * 
	 * @param file
	 *            a File type
	 * @throws FileNotFoundException
	 */
	public static Map<String, Integer> getTokensFromFile(File file)
			throws FileNotFoundException {
		Map<String, Integer> tokens = new HashMap<String, Integer>();
		ArrayList<String> listOfRawTokensInFile = Util.removeSGMLTags(file);
		for (String rawToken : listOfRawTokensInFile) {
			ArrayList<String> processedTokens = processToken(rawToken);
			if (processedTokens != null) {
				for (String processedToken : processedTokens) {
					if (tokens.get(processedToken) == null)
						tokens.put(processedToken, DEFAULT_TOKEN_COUNT);
					else
						tokens.put(processedToken,
								tokens.get(processedToken) + 1);

				}
			}
		}
		return tokens;
	}

	/**
	 * This method returns the list of files in a directory.
	 * 
	 * @param directory
	 *            a String type.
	 * @return An Array of File type.
	 */
	public static File[] getAllFiles(String directory) {
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		// System.out.println("Number of files : " + listOfFiles.length);
		Arrays.sort(listOfFiles);
		return listOfFiles;
	}

	/**
	 * This method returns the sorted list of words from given a file.
	 * 
	 * @param stopwordfile
	 * @return
	 * @throws FileNotFoundException
	 */
	public static TreeMap<String, String> getStopWordsFromFile(
			String stopwordfile) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(stopwordfile));
		TreeMap<String, String> list = new TreeMap<String, String>();
		while (scanner.hasNext()) {
			String stopWord = scanner.next();
			list.put(stopWord, stopWord);
		}
		scanner.close();
		return list;
	}

	/**
	 * This method removes stop words from the keys of map and a map containing
	 * no stop words in keys.
	 * 
	 * @param stemmedTokensFromFile
	 * @param stopWords
	 * @return
	 */
	public static Map<String, Integer> removeStopWords(
			Map<String, Integer> stemmedTokensFromFile,
			TreeMap<String, String> stopWords) {
		Map<String, Integer> stemmedTokensWoStopWords = new HashMap<String, Integer>();
		Set<String> stemmedTokens = stemmedTokensFromFile.keySet();
		for (String token : stemmedTokens) {
			if (!stopWords.containsKey(token))
				stemmedTokensWoStopWords.put(token,
						stemmedTokensFromFile.get(token));
		}
		return stemmedTokensWoStopWords;
	}

	/**
	 * This method updates the document info. It finds out the doc length of the
	 * document and find out the frequency of most frequent word in the document
	 * excluding stop words.
	 * 
	 * @param docInfo
	 *            2D array of integers
	 * @param docId
	 * @param stemmedTokensFromFile
	 *            map containing all the stems present in the document
	 * @param stemmedTokensWoStopWords
	 *            map containing all the stems excluding stop words in the
	 *            document.
	 */
	public static void updateDocInfo(Integer[][] docInfo, int docId,
			Map<String, Integer> stemmedTokensFromFile,
			Map<String, Integer> stemmedTokensWoStopWords) {
		int totalWordsInFile = 0, maxFreq = 0;
		Set<String> stemmedTokens = stemmedTokensFromFile.keySet();
		for (String token : stemmedTokens) {
			int freqOfToken = stemmedTokensFromFile.get(token);
			totalWordsInFile += freqOfToken;
		}
		Set<String> stemmedTokensWoStopWordsSet = stemmedTokensWoStopWords
				.keySet();
		for (String token : stemmedTokensWoStopWordsSet) {
			int freqOfToken = stemmedTokensWoStopWords.get(token);
			maxFreq = Math.max(maxFreq, freqOfToken);
		}
		docInfo[docId][0] = maxFreq;
		docInfo[docId][1] = totalWordsInFile;
	}

	/**
	 * This method do stemming on each term of the map and constructs a new map
	 * with stemmed tokens.
	 * 
	 * @param tokensFromFile
	 * @return
	 */
	public static Map<String, Integer> getStemmedTokens(
			Map<String, Integer> tokensFromFile) {
		Stemmer stemmer = new Stemmer();
		Map<String, Integer> stemmedTokens = new HashMap<String, Integer>();
		for (String token : tokensFromFile.keySet()) {
			stemmer.add(token.toCharArray(), token.length());
			stemmer.stem();
			String stemmedToken = stemmer.toString();
			if (stemmedTokens.get(stemmedToken) == null)
				stemmedTokens.put(stemmedToken, tokensFromFile.get(token));
			else
				stemmedTokens.put(stemmedToken, stemmedTokens.get(stemmedToken)
						+ tokensFromFile.get(token));
		}
		return stemmedTokens;
	}

	/**
	 * This method encodes an integer using delta encoding.
	 * 
	 * @param gaps
	 * @return
	 */
	public static short deltaEncoding(Integer gaps) {
		int count = 1;
		// int unary = 0;
		int n = gaps;
		while (n > 1) {
			n = n / 2;
			count++;

		}
		// System.out.println(count);
		int code = gammaEncoding(count);
		int count1 = 0;
		// System.out.println(code);
		// int unary1=0;
		int n1 = gaps;
		while (n1 != 1) {
			n1 = n1 / 2;
			count1++;

		}
		// System.out.println(count1);
		// int holdcnt1= count1;
		int k = gaps;
		int y = 1 << count1;
		y = y - 1;
		// System.out.println(y);
		k = k & y;
		// System.out.println(k);
		// System.out.println(k);
		// System.out.println(code);
		code = code << count1;
		code = k | code;
		// System.out.println(Integer.toBinaryString(code));
		return (short) code;
	}

	/**
	 * This method encodes an integer using delta encoding.
	 * 
	 * @param gaps
	 * @return
	 */
	public static short gammaEncoding(Integer frequency) {
		int count = 0;
		int unary = 0;
		int n = frequency;
		while (n != 1) {
			n = n / 2;
			count++;

		}
		int holdcnt = count;
		int k = frequency;
		int y = 1 << count;
		y = y - 1;
		k = frequency & y;
		// System.out.println(k);
		while (count != 0) {
			unary = unary << 1;
			unary |= 1;
			count--;
		}
		unary = unary << 1;
		// System.out.println(unary);
		unary = unary << holdcnt;
		// System.out.println();
		k = k | unary;
		return (short) k;
	}

	/**
	 * This method retrieves the text between the TITLE tag present in the
	 * document.
	 * 
	 * @param cranfieldPrefix
	 * @param docId
	 * @return
	 * @throws IOException
	 */
	public static String getDocTitle(String cranfieldPrefix, int docId)
			throws IOException {
		File file = new File(cranfieldPrefix + String.format("%04d", docId));
		Document doc = Jsoup.parse(file, "UTF-8");
		return doc.select("TITLE").text();
	}
}

import java.io.File;
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

import org.jsoup.Jsoup;

/**
 * This class implement methods for manipulating/processing tokens and files..
 * 
 * @author Abhishek Gupta (axg137230)
 *
 */
public class Util {
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
			else
				processedTokens.add(token);
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
			String[] tokensWithoutSGMLTags = Jsoup.parse(file, "UTF-8").text()
					.replaceAll("[^a-zA-Z'.0-9]", " ").split(" ");
			for (String rawToken : tokensWithoutSGMLTags) {
				if (rawToken.trim().length() > 0)
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
	public static Map<String, Integer> sortByValue(HashMap<String, Integer> map) {
		// List of entries of map
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(
				map.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// creating a sorted map.
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}

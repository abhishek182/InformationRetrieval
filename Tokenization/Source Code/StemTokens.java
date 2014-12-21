import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class stores the stems and implements the methods regarding it.
 * 
 * @author Abhishek Gupta (axg137230)
 *
 */
public class StemTokens {
	/**
	 * This is the constant storing the default stem count.
	 */
	private static final int DEFAULT_STEM_COUNT = 1;
	/**
	 * This is Tokens instance storing all the tokens.
	 */
	private Tokens tokens;
	/**
	 * This is map storing stemmed token strings and their frequency.
	 */
	private HashMap<String, Integer> stemmedTokens;
	/**
	 * This variable stores to the total number of stems seen so far.
	 */
	private Long stemmedTokensCount;
	/**
	 * This is a Stemmer instance which implements the Porter's algorithm.
	 */
	private Stemmer stemmer;

	/**
	 * This is the constructor which uses token instance to instantiate the
	 * class variable.
	 * 
	 * @param tokens
	 *            a Tokens type.
	 */
	public StemTokens(Tokens tokens) {
		this.tokens = tokens;
		this.stemmer = new Stemmer();
		this.stemmedTokens = new HashMap<String, Integer>();
		this.stemmedTokensCount = 0L;
	}

	/**
	 * This method insert the new stem or update the frequency of old stem.
	 * 
	 * @param stemToken
	 *            a String type.
	 */
	private void insertStem(String stemToken) {
		if (stemmedTokens.get(stemToken) == null)
			stemmedTokens.put(stemToken, DEFAULT_STEM_COUNT);
		else
			stemmedTokens.put(stemToken, stemmedTokens.get(stemToken) + 1);
		stemmedTokensCount++; // for every insert increase the total stem
								// count.
	}

	/**
	 * This method returns the total number of stems count seen so far.
	 * 
	 * @return
	 */
	public long numberOfStems() {
		return this.stemmedTokensCount;
	}

	/**
	 * This method applies Porter's algorithm to each token and insert into the
	 * map.
	 */
	public void stemAllTokens() {
		Set<String> allTokens = tokens.getAlltokens();
		for (String string : allTokens) {
			stemmer.add(string.toCharArray(), string.length());
			stemmer.stem();
			String stemmedToken = stemmer.toString();
			// if stemmed token is different from the token
			if (!stemmedToken.equals(string))
				insertStem(stemmedToken);
		}
	}

	/**
	 * This method returns the total number of unique stems.
	 * 
	 * @return int type.
	 */
	public int numberOfUniqueStems() {
		return stemmedTokens.size();
	}

	/**
	 * This method gives the number of stems that occurs for certain number of
	 * times.
	 * 
	 * @param freq
	 *            int type
	 * @return int type
	 */
	public int numberOfStemsWithFreq(int freq) {
		int count = 0;
		for (String token : stemmedTokens.keySet()) {
			if ((Integer) stemmedTokens.get(token) == freq)
				count++;
		}
		return count;
	}

	/**
	 * This method calculated the average number of stems per document.
	 * 
	 * @param docCount
	 *            int type
	 * @return long type
	 */
	public long averageStemsPerDocument(int docCount) {
		return stemmedTokensCount / docCount;
	}

	/**
	 * This method returns certain number of most occurring stems in the map.
	 * 
	 * @param topMost
	 *            int type
	 * @return List of type string.
	 */
	public List<String> getMostFrequentStems(int topMost) {
		Map<String, Integer> sortedTokensByValue = Util
				.sortByValue(stemmedTokens);
		List<String> frequentTokens = new LinkedList<String>();
		int i = 0;
		Iterator<String> tokenIterator = sortedTokensByValue.keySet()
				.iterator();
		if (tokenIterator.hasNext()) {
			String token = tokenIterator.next();
			for (; i < topMost && tokenIterator.hasNext(); i++, token = tokenIterator
					.next()) {
				System.out.println("Stem: \"" + token + "\" freq: "
						+ sortedTokensByValue.get(token));
				frequentTokens.add(token);
			}
		}
		return frequentTokens;
	}

}

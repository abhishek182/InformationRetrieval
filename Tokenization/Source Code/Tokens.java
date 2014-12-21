import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class stores the tokens and implements the methods regarding it.
 * 
 * @author Abhishek Gupta (axg137230)
 *
 */
public class Tokens {
	/**
	 * This is the constant storing the default token count.
	 */
	private static final int DEFAULT_TOKEN_COUNT = 1;
	/**
	 * This is map storing token strings and their frequency.
	 */
	private HashMap<String, Integer> tokens;
	/**
	 * This variable stores to the total number of tokens seen so far.
	 */
	private Long tokenCount;

	/**
	 * The constructor initializes the map and token count.
	 */
	public Tokens() {
		tokens = new HashMap<String, Integer>();
		tokenCount = 0L;
	}

	/**
	 * This method insert the new token or update the frequency of old token.
	 * 
	 * @param token
	 *            string type
	 */
	public void insertToken(String token) {
		if (tokens.get(token) == null)
			tokens.put(token, DEFAULT_TOKEN_COUNT);
		else
			tokens.put(token, tokens.get(token) + 1);
		tokenCount++; // for every insert increase the total token count.
	}

	/**
	 * This method gives the total number of tokens in the map.
	 * 
	 * @return long type
	 */
	public Long numberOfTokens() {
		return tokenCount;
	}

	/**
	 * This method gives the number of unique tokens.
	 * 
	 * @return int type.
	 */
	public int numberOfUniqueTokens() {
		return tokens.size();
	}

	/**
	 * This method gives the number of tokens that occurs for certain number of
	 * times.
	 * 
	 * @param freq
	 *            int type
	 * @return int type
	 */
	public int numberOfTokenWithFreq(int freq) {
		int count = 0;
		for (String token : tokens.keySet()) {
			if ((Integer) tokens.get(token) == freq)
				count++;
		}
		return count;
	}

	/**
	 * This method returns certain number of most occurring tokens in the map.
	 * 
	 * @param topMost
	 *            int type
	 * @return List of type string.
	 */
	public List<String> getMostFrequentTokens(int topMost) {
		Map<String, Integer> sortedTokensByValue = Util.sortByValue(tokens);
		List<String> frequentTokens = new LinkedList<String>();
		int i = 0;
		Iterator<String> tokenIterator = sortedTokensByValue.keySet()
				.iterator();
		if (tokenIterator.hasNext()) {
			String token = tokenIterator.next();
			for (; i < topMost && tokenIterator.hasNext(); i++, token = tokenIterator
					.next()) {
				System.out.println("Token: \"" + token + "\" freq: "
						+ sortedTokensByValue.get(token));
				frequentTokens.add(token);
			}
		}
		return frequentTokens;
	}

	/**
	 * This method calculated the average number of tokens per document.
	 * 
	 * @param docCount
	 *            int type
	 * @return long type
	 */
	public long averageTokensPerDocument(int docCount) {
		return tokenCount / docCount;
	}

	/**
	 * This method print all the tokens in the map.
	 */
	public void printTokens() {
		for (String string : tokens.keySet()) {
			System.out.println(string + " - " + tokens.get(string));
		}
	}

	public Set<String> getAlltokens() {
		return this.tokens.keySet();
	}

}

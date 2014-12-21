import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class contains all the properties of a query and method to process the
 * query.
 * 
 * @author Abhishek Gupta (axg137230)
 *
 */
public class Query {
	/**
	 * This is the constant storing the default token count.
	 */
	private static final int DEFAULT_TOKEN_COUNT = 1;

	/**
	 * This map stores the tokens in the query and its occurrence.
	 */
	private Map<String, Integer> queryIndex;

	/**
	 * This stores the score of the document-query (this query) pair using
	 * weighing function 1.
	 */
	private Map<Integer, Double> docRankingByW1;

	/**
	 * This stores the score of the document-query (this query) pair using
	 * weighing function 2.
	 */
	private Map<Integer, Double> docRankingByW2;

	/**
	 * This is the constructor to initialize all the member variables./
	 */
	public Query() {
		this.queryIndex = new TreeMap<String, Integer>();
		this.docRankingByW1 = new TreeMap<Integer, Double>();
		this.docRankingByW2 = new TreeMap<Integer, Double>();
	}

	/**
	 * Method create and index for this query.
	 * 
	 * @param query
	 * @param stopWords
	 */
	public void indexQuery(String query, TreeMap<String, String> stopWords) {
		String[] tokens = query.replaceAll("[^a-zA-Z0-9'.]", " ")
				.replaceAll("\\s+", " ").trim().split(" ");
		ArrayList<String> listOfTokensInQuery = new ArrayList<String>();
		for (String rawToken : tokens) {
			if (rawToken.length() > 0)
				listOfTokensInQuery.add(rawToken.trim());
		}
		Map<String, Integer> tokenMap = new HashMap<String, Integer>();
		for (String rawToken : listOfTokensInQuery) {
			ArrayList<String> processedTokens = Util.processToken(rawToken);
			if (processedTokens != null) {
				for (String processedToken : processedTokens) {
					if (tokenMap.get(processedToken) == null)
						tokenMap.put(processedToken, DEFAULT_TOKEN_COUNT);
					else
						tokenMap.put(processedToken,
								tokenMap.get(processedToken) + 1);

				}
			}
		}
		Map<String, Integer> stemmedTokens = Util.getStemmedTokens(tokenMap);
		this.queryIndex = Util.removeStopWords(stemmedTokens, stopWords);
	}

	/**
	 * Method prints the query index.
	 */
	public void printQueryIndex() {
		Set<String> queryTerms = this.queryIndex.keySet();
		for (String string : queryTerms) {
			System.out.print(string + ",");
		}
		System.out.println();
	}

	/**
	 * This method calculates the score of this query with all the documents and
	 * sort the score map in the reverse order of value.
	 * 
	 * @param index
	 */
	public void generateRaking(Index index) {
		for (int i = 1; i < index.collectionSize() + 1; i++) {
			calculateByW1(i, index);
			calculateByW2(i, index);
		}
		this.docRankingByW1 = Util.sortByValue(docRankingByW1);
		this.docRankingByW2 = Util.sortByValue(docRankingByW2);
	}

	/**
	 * This method uses weighing function 1 to calculate the score.
	 * 
	 * @param docId
	 * @param index
	 */
	private void calculateByW1(int docId, Index index) {
		double w1 = 0.0d;
		int maxF = index.maxF(docId);
		int cs = index.collectionSize();
		for (String queryToken : this.queryIndex.keySet()) {
			int tf = index.termFreqInDoc(queryToken, docId);
			int df = index.documentFreq(queryToken) + 1;
			double w = (0.4d + 0.6d * Math.log(tf + 0.5d)
					/ Math.log(maxF + 1.0d))
					* (Math.log(cs / df) / Math.log(cs));
			w1 += w;
		}
		this.docRankingByW1.put(docId, w1);
	}

	/**
	 * This method uses weighing function 2 to calculate the score.
	 * 
	 * @param docId
	 * @param index
	 */
	private void calculateByW2(int docId, Index index) {
		double w2 = 0.0d;
		int docLen = index.docLen(docId);
		int cs = index.collectionSize();
		double avgDocLen = index.avgDocLen();
		for (String queryToken : this.queryIndex.keySet()) {
			int tf = index.termFreqInDoc(queryToken, docId);
			int df = index.documentFreq(queryToken) + 1;
			double w = 0.4d + 0.6
					* (tf / (tf + 0.5d + 1.5 * (docLen / avgDocLen)))
					* (Math.log(cs / df) / Math.log(cs));
			w2 += w;
		}
		this.docRankingByW2.put(docId, w2);
	}

	/**
	 * This method returns the score of this query with each document calculated
	 * using weighing function 1.
	 * 
	 * @return
	 */
	public Map<Integer, Double> getRankingByW1() {
		return this.docRankingByW1;
	}

	/**
	 * This method returns the score of this query with each document calculated
	 * using weighing function 1.
	 * 
	 * @return
	 */
	public Map<Integer, Double> getRankingByW2() {
		return this.docRankingByW2;
	}

}

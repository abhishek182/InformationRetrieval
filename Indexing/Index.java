import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class contains all the properties of an index and methods to process the
 * index.
 * 
 * @author Abhishek Gupta (axg137230)
 *
 */
public class Index {

	/**
	 * This map stores uncompressed index. Map has term as key and sorted
	 * posting list as value. A posting list contains docId and term frequency
	 * pair as key value.
	 */
	private Map<String, TreeMap<Integer, Integer>> index;

	/**
	 * This map stores compressed index. Map has term as key and sorted posting
	 * list as value. A posting list contains docId and term frequency pair as
	 * key value.
	 */
	private Map<String, LinkedHashMap<Short, Short>> indexCompressed;

	/**
	 * These files stores the uncompressed and compressed index.
	 */
	private File indexUncompressedFile, indexCompressedFile;

	/**
	 * The constructor initializes both the indices.
	 */
	public Index() {
		this.index = new TreeMap<String, TreeMap<Integer, Integer>>();
		this.indexCompressed = new TreeMap<String, LinkedHashMap<Short, Short>>();
	}

	/**
	 * This method get the old posting list for a given term and add new
	 * docId,termFreq pair to the list else create new postingList and puts it
	 * into the index map.
	 * 
	 * @param term
	 * @param docId
	 * @param termFreq
	 */
	private void insert(String term, int docId, int termFreq) {
		TreeMap<Integer, Integer> oldPostingList = index.get(term);
		if (oldPostingList != null) {
			oldPostingList.put(docId, termFreq);
			index.put(term, oldPostingList);
		} else {
			TreeMap<Integer, Integer> newPostingList = new TreeMap<Integer, Integer>();
			newPostingList.put(docId, termFreq);
			index.put(term, newPostingList);
		}
	}

	/**
	 * This method iterates through map of tokens of one doc and insert each
	 * token into the index map.
	 * 
	 * @param docId
	 * @param stemmedTokens
	 */
	public void creatIndex(int docId, Map<String, Integer> stemmedTokens) {
		Set<String> tokens = stemmedTokens.keySet();
		for (String token : tokens) {
			insert(token, docId, stemmedTokens.get(token));
		}
	}

	/**
	 * This method gets the number of documents in which the term is present.
	 * 
	 * @param term
	 * @return
	 */
	public int documentFreq(String term) {
		TreeMap<Integer, Integer> postings = index.get(term);
		if (postings != null)
			return postings.size();
		return 0;
	}

	/**
	 * This method gets the number of times a term occur in all the documents.
	 * 
	 * @param term
	 * @return
	 */
	public int termFreq(String term) {
		TreeMap<Integer, Integer> postings = index.get(term);
		int termFreq = 0;
		for (Map.Entry<Integer, Integer> entry : postings.entrySet()) {
			termFreq += entry.getValue();
		}
		return termFreq;
	}

	/**
	 * This method returns the size of the posting list in bytes.
	 * 
	 * @param term
	 * @return
	 * @throws IOException
	 */
	public long getPostingSize(String term) throws IOException {
		TreeMap<Integer, Integer> postings = index.get(term);
		File temp = new File("TempPosting");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				temp));
		oos.writeObject(postings);
		oos.flush();
		oos.close();
		return temp.length();
	}

	/**
	 * This method returns number of distinct terms present in the index.
	 * 
	 * @return
	 */
	public int size() {
		return index.size();
	}

	/**
	 * This method prints all index entries along with there posting list to
	 * console.
	 */
	public void printIndex() {
		Set<String> allLemmas = this.index.keySet();
		for (String lemma : allLemmas) {
			TreeMap<Integer, Integer> postingList = this.index.get(lemma);
			System.out.print(lemma + ": " + postingList.size());
			for (Map.Entry<Integer, Integer> entry : postingList.entrySet()) {
				System.out.print(" " + entry.getKey() + "-" + entry.getValue());
			}
			System.out.println();
		}
	}

	/**
	 * This method initiate index compression process by calculating gaps in
	 * each posting list and do delta encoding on docId gaps and gamma encoding
	 * on termFreq and stores the new compressed index in a map.
	 */
	public void compressIndex() {
		Set<String> terms = this.index.keySet();
		for (String term : terms) {
			TreeMap<Integer, Integer> postingList = index.get(term);
			LinkedHashMap<Short, Short> postingListWithGaps = new LinkedHashMap<Short, Short>();
			int initialGap = 0;
			for (Map.Entry<Integer, Integer> entry : postingList.entrySet()) {
				int currentGap = entry.getKey() - initialGap;
				postingListWithGaps.put(Util.deltaEncoding(currentGap),
						Util.gammaEncoding(entry.getValue()));
				initialGap = entry.getKey();
			}
			this.indexCompressed.put(term, postingListWithGaps);
		}
	}

	/**
	 * This method writes the compressed index to a binary file by given name at
	 * given path.
	 * 
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	public void writeIndexCompressed(String filePath, String fileName)
			throws IOException {
		indexCompressedFile = new File(filePath + fileName);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				indexCompressedFile));
		oos.writeObject(this.indexCompressed);
		oos.flush();
		oos.close();
	}

	/**
	 * This method writes the uncompressed index to a binary file by given name
	 * at given path.
	 * 
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	public void writeIndexUncompressed(String filePath, String fileName)
			throws IOException {
		indexUncompressedFile = new File(filePath + fileName);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				indexUncompressedFile));
		oos.writeObject(this.index);
		oos.flush();
		oos.close();
	}

	/**
	 * This method returns the size of the index file in bytes.
	 * 
	 * @param type
	 *            defines type of index i.e. compressed or uncompressed.
	 * @return
	 */
	public long getSize(String type) {
		if (type.equalsIgnoreCase("uncompressed"))
			return this.indexUncompressedFile.length();
		else if (type.equalsIgnoreCase("compressed"))
			return this.indexCompressedFile.length();
		return 0;
	}

}

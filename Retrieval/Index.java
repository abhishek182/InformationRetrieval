import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
	 * This 2D array stores the frequency of most frequent term and doc length
	 * of a particular document.
	 */
	private Integer[][] docInfo;

	/**
	 * Method constructs the index map using the binary file provided.
	 * 
	 * @param fileName
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void constructIndex(String fileName) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(new File(fileName));
		ObjectInputStream ois = new ObjectInputStream(fis);
		this.index = (Map<String, TreeMap<Integer, Integer>>) ois.readObject();
		ois.close();
	}

	/**
	 * Method loads the document info into the 2D array using a binary file.
	 * Loading the maxF and docLen of each document.
	 * 
	 * @param fileName
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadDocInfo(String fileName) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(new File(fileName));
		ObjectInputStream ois = new ObjectInputStream(fis);
		this.docInfo = (Integer[][]) ois.readObject();
		ois.close();
	}

	/**
	 * Method print the whole index. Terms and whole posting list associated to
	 * that term.
	 */
	public void printIndex() {
		Set<String> terms = this.index.keySet();
		for (String term : terms) {
			TreeMap<Integer, Integer> postingList = this.index.get(term);
			System.out.print(term + ": " + postingList.size());
			for (Map.Entry<Integer, Integer> entry : postingList.entrySet()) {
				System.out.print(" " + entry.getKey() + "-" + entry.getValue());
			}
			System.out.println();
		}
	}

	/**
	 * Method print document info i.e. maxF and docLen for all the documents.
	 */
	public void printDocInfo() {
		for (int i = 1; i < this.docInfo.length; i++) {
			System.out.println("Doc " + i + " Max Freq " + this.docInfo[i][0]
					+ " docLen " + this.docInfo[i][1]);
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
	public int termFreqInDoc(String term, int docId) {
		TreeMap<Integer, Integer> postings = index.get(term);
		int termFreq = 0;
		if (postings != null) {
			if (postings.containsKey(docId)) {
				termFreq = postings.get(docId);
			}
		}
		return termFreq;
	}

	/**
	 * Method return the docLen of the given document id.
	 * 
	 * @param docId
	 * @return
	 */
	public int docLen(int docId) {
		return this.docInfo[docId][1];
	}

	/**
	 * Method returns the average document length in the collection.
	 * 
	 * @return
	 */
	public double avgDocLen() {
		double avg = 0.0d;
		for (int i = 1; i < collectionSize() + 1; i++) {
			avg += this.docInfo[i][1];
		}
		return avg / collectionSize();
	}

	/**
	 * Method return the maxF of the given document id.
	 * 
	 * @param docId
	 * @return
	 */
	public int maxF(int docId) {
		return this.docInfo[docId][0];
	}

	/**
	 * Method returns the total number of documents in the collection.
	 * 
	 * @return
	 */
	public int collectionSize() {
		return this.docInfo.length - 1;
	}
}

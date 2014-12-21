import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Jsoup;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class contains method to create lemma out of a give file.
 * 
 * @author Abhishek Gupta (axg137230)
 *
 */
public class Lemmatizer {

	/**
	 * This is the default value of the token count.
	 */
	private static final int DEFAULT_TOKEN_COUNT = 1;

	/**
	 * This variable holds the properties. Basically key value pair.
	 */
	private Properties props;

	/**
	 * This variable is an object of StanfordCoreNLP API which creates lemmas
	 * from a file content.
	 */
	StanfordCoreNLP pipeline;

	/**
	 * This constructor initializes the member variables.
	 * 
	 * @param annotators
	 */
	public Lemmatizer(String annotators) {
		props = new Properties();
		props.put("annotators", annotators);
		pipeline = new StanfordCoreNLP(props);
	}

	/**
	 * This method apply lemmatization on a file content and returns back the
	 * lemmas present in the file along with their frequency in a map.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Map<String, Integer> lemmatize(File file) throws IOException {
		Map<String, Integer> lemmasInFile = new HashMap<String, Integer>();
		String text = Jsoup.parse(file, "UTF-8").text();
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String lemma = token.get(LemmaAnnotation.class).toLowerCase();
				// System.out.println(lemma);
				if (lemma.matches("[a-zA-Z0-9]+")) {
					if (lemmasInFile.get(lemma) == null)
						lemmasInFile.put(lemma, DEFAULT_TOKEN_COUNT);
					else
						lemmasInFile.put(lemma, lemmasInFile.get(lemma) + 1);
				}
			}
		}
		return lemmasInFile;
	}

	public String lemmatize(String string) {
		Annotation document = new Annotation(string);
		// run all Annotators on this text
		pipeline.annotate(document);
		String lemma = "";
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				lemma = token.get(LemmaAnnotation.class).toLowerCase();
			}
		}
		return lemma;
	}
}

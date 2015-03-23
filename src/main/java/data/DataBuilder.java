package data;


import org.apache.commons.codec.language.bm.Lang;
import preprocessing.TextProcessor;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class create a new vector from a website
 * or read a set of websites and add them to the current data
 */
public class DataBuilder {

    private String ATTR = "ATTR";
    private Dictionary dictionary;
    private TextProcessor textProcessor;

    public DataBuilder(Dictionary dictionary, String modelDirectory) {
        this.dictionary = dictionary;
        textProcessor = new TextProcessor(modelDirectory);
    }

    /**
     * Create new empty set of train data
     *
     * @param dataName:  Name of train data
     * @param className: Set of class name
     * @return new Data
     */
    public Instances newData(String dataName, List<String> className) {

        ArrayList<Attribute> attrs = new ArrayList<Attribute>();
        for (int i = 0; i < dictionary.getWords().size(); i++) {
            attrs.add(new Attribute(ATTR + " " + i));
        }

        Attribute classAttr = new Attribute("Class", className);
        attrs.add(classAttr);

        Instances instances = new Instances(dataName, attrs, 0);
        instances.setClass(classAttr);

        return instances;
    }


    /**
     * Create new instance from a website
     *
     * @param website
     * @param language
     * @return
     */
    public Instance build(Website website, TextProcessor.Language language) {
        if (website == null || dictionary == null || dictionary.getWords().size() == 0) {
            System.out.println("You don't put any website or dictionary is zero");
            return null;
        } else {
            String text = textProcessor.preProcessing(website.getText(), language);

            return build(text);
        }
    }


    /**
     * Create new instance of website but not know its language
     * Option by auto-dectect language
     *
     * @param website
     * @return
     */
    public Instance build(Website website) {
        if (website == null || dictionary == null || dictionary.getWords().size() == 0) {
            System.out.println("You don't put any website or dictionary is zero");
            return null;
        } else {

            String text = website.getText();

            TextProcessor.Language language = TextProcessor.detectLanguage(text);
            text = textProcessor.preProcessing(website.getText(), language);

            return build(text);
        }
    }


    /**
     * Build a preprocessed text which was tokenized, ...
     * @param preprocessedText
     * @return
     */
    public Instance build(String preprocessedText) {
        Map<String, Integer> words = textProcessor.countWords(preprocessedText);
        Instance instance = new SparseInstance(dictionary.getWords().size());

        for (String word : words.keySet()) {
            if (dictionary.getWords().contains(word.trim())) {
                instance.setValueSparse(dictionary.getWords().indexOf(word), words.get(word));
            }
        }
        return instance;
    }


    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public TextProcessor getTextProcessor() {
        return textProcessor;
    }
}

package data;

import preprocessing.TextProcessor;
import utils.NumberUtils;

import java.io.*;
import java.util.*;

public class Dictionary {
    private List<String> words;

    public static Dictionary createDictionary(String folderName, int numberWord, String outName) {
        int i = 0;

        // LIST OF URLS: urls contains many information of the website
        List<String> urls = new ArrayList<String>();

        // Structure of map: <url, <word, time>>
        Map<String, Map<String, Integer>> reducedWebsites = new HashMap<String, Map<String, Integer>>();

        // List of words in dictionary
        List<String> words = new ArrayList<String>();


        File folder = new File(folderName);

        for (File urlFolder : folder.listFiles()) {
            String url = urlFolder.getName();
            urls.add(url);

            String text = "";
            for (File file : urlFolder.listFiles()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Scanner scanner = new Scanner(fileInputStream);

                    String subUrl = scanner.nextLine();
                    text = text + " " + scanner.nextLine();
                    scanner.close();
                } catch (Exception e) {
                    System.out.println(urlFolder.getName()+"/"+file.getName()+"is missing line");
                    e.printStackTrace();
                }
            }

            // Remove some unwanted characters
            text = text.replaceAll("[0-9]", "");
            text = text.replaceAll("_ ", " ");
            text = text.replaceAll(" _ ", " ");

            // count number of times in words
            Map<String, Integer> listWords = TextProcessor.countWords(text);

            reducedWebsites.put(url, listWords);

            for (String word : listWords.keySet()) {
                if (!words.contains(word)) {
                    // if this word have not in dictionary
                    words.add(word);
                }
            }


        }

        // finish reading all the html
        // start choose the most frequent words in the websites
        double matrix[][] = new double[reducedWebsites.size()][words.size()];

        int m = 0;
        for (String url : reducedWebsites.keySet()) {
            Map<String, Integer> reducedContent = reducedWebsites.get(url);

            Arrays.fill(matrix[m], 0);
            if (reducedContent == null) {
                Arrays.fill(matrix[m], 1.0 / words.size());
            } else {

                for (int n = 0; n < words.size(); n++) {
                    if (reducedContent.containsKey(words.get(n))) {
                        matrix[m][n] = reducedContent.get(words.get(n));
                    }
                }
            }

            m++;
        }


        for (int n = 0; n < matrix.length; n++) {
            double sum = 0;
            for (int p = 0; p < matrix[n].length; p++) {
                sum = sum + matrix[n][p];
            }

            for (int p = 0; p < matrix[n].length; p++) {
                matrix[n][p] = matrix[n][p] / sum;
            }

        }

        Map<String, Double> map = new HashMap<String, Double>();

        for (int n = 0; n < words.size(); n++) {

            double value = 0;

            for (int p = 0; p < matrix.length; p++) {
                value = value + matrix[p][n];
            }

            map.put(words.get(n), value);
        }

        System.out.println(map);
        List<String> listMax = NumberUtils.sort(map, numberWord);

        try {
            PrintWriter printWriter = new PrintWriter(outName, "UTF-8");
            for (int j = 0; j < listMax.size(); j++) {
                printWriter.write(listMax.get(j) + "\n");
            }

            printWriter.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return new Dictionary(listMax);
    }


    public Dictionary(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }

}

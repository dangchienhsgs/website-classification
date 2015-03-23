package data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * This class describe an website which have url and content (which describe in class Document of Jsoup
 * @author Dang Chien Nguyen
 * @version 1.0
 */
public class Website {
    private Document document;
    private String url;

    /**
     * Constructor use when we don't have the url of this website
     * @param document
     */
    public Website(Document document) {
        this.document = document;
    }


    /**
     * Auto connect and stream data, so we convert it to this class
     * @param url
     * @return an Website instance, null if this url is fail
     */
    public static Website connect(String url) {
        int times = 0;

        Document document = null;
        while (document == null && times < 20) {
            // If it can not more than 20 times, it is fail
            times = times + 1;

            try {
                document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .timeout(2000)
                        .get();

            } catch (Exception e) {
                // do nothing
                System.out.println (url+" missing times "+times);
            }

        }

        if (document == null) {
            // if can not connect
            return null;
        } else {
            return new Website(document);
        }

    }


    /**
     * Static method use to parse an html string and convert it to html
     * @param html
     * @param url
     * @return an Website Object
     */
    public static Website parse(String html, String url) {
        Document doc = Jsoup.parse(html);
        Website handler = new Website(doc);
        handler.setUrl(url);

        return handler;
    }


    /**
     * Static method same as above, use if we do not have the url
     * @param html
     * @return
     */
    public static Website parse(String html) {
        Document doc = Jsoup.parse(html);
        return new Website(doc);
    }


    /**
     * get all links in the content of this website
     * @return List of Links
     */
    public List<String> getAllLinks() {
        Elements elements = document.select("a[href]");
        List<String> links = new ArrayList<String>();

        for (int i = 0; i < elements.size(); i++) {
            String link = elements.get(i).attr("abs:href");

            if (!links.contains(link)) {
                links.add(elements.get(i).attr("abs:href"));

            }
        }

        return links;
    }


    /**
     * @param url
     * @return Filter to get all links which are in same host with the website
     * @throws java.net.MalformedURLException if the url is fail
     */
    public List<String> getLinkSameHost(String url) throws MalformedURLException {
        List<String> links = getAllLinks();

        System.out.println(url);
        String host = new URL(url).getHost();

        for (int i = 0; i < links.size(); i++) {
            String link = links.get(i);
            try {
                URL u = new URL(link);
                if (!u.getHost().equals(host)) {
                    links.remove(link);
                }
            } catch (MalformedURLException e) {
                //System.out.println (link);
                links.remove(link);
            }

        }

        return links;
    }

    /**
     * Read the website file with format:
     * first-line: url
     * next: html content
     * all train data is saved under this format
     * @param file
     * @param urlInFirstLine
     * @return Website instance
     * @throws java.io.FileNotFoundException
     */
    public static Website readHtmlFile(String file, boolean urlInFirstLine) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);

        Scanner scanner = new Scanner(fileInputStream);
        if (scanner.hasNextLine()) {

            String url = "";
            String html = "";

            if (urlInFirstLine) {
                // first line is the url
                url = scanner.nextLine().trim();
            }

            while (scanner.hasNextLine()) {
                html = html + scanner.nextLine();
            }

            Document document = Jsoup.parse(html);

            Website handler = new Website(document);

            handler.setUrl(url);

            return handler;
        } else {
            throw new FileNotFoundException(file + " does not have any line");
        }
    }

    public Document getDocument() {
        return document;
    }

    public String getText() {
        return document.text();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}

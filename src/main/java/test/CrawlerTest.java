package test;

import crawler.Crawler;
import org.junit.Test;

/**
 * Created by dangchienhsgs on 1/14/15.
 */


public class CrawlerTest {
    @Test
    public void check(){
        try{
            System.out.println (new Crawler().getURLFromLink("http://www.google.com.vn/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&cad=rja&uact=8&ved=0CBsQFjAA&url=http%3A%2F%2Fwww.theguardian.com%2Ftechnology%2Fappsblog&ei=Bua1VJTDJM2WuASo_YHYBg&usg=AFQjCNF_Xb7zz3tgYwUF8SuVzhWmOUBL-A&sig2=TgIpbwz651yUSdCTxHw7yQ&bvm=bv.83640239,d.c2E"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}

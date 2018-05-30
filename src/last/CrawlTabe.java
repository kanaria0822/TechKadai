package last;

import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.URL;
import java.net.URLConnection;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;

public class CrawlTabe {
    
    private String baseUrl;
    
    CrawlTabe(String url){
        this.baseUrl = url;
    }
    
    public void run(){
        
        List<Store> stores = extractStoresUrlListFrom(this.baseUrl);
        
        System.out.println("URL get!!");
        
        for(Store store : stores){
            scrapeStorePage(store);
        }
        
        DrawHTML dhtml = new DrawHTML(stores);
        dhtml.runDrawHTML();
    
    }
    
    private static List<Store> extractStoresUrlListFrom(String url){
        
        List<Store> storesList = new ArrayList<>();
        
        String nextUrl = url;
        
        do{
            
            try{
                
                sleepOneSec();
                Document doc = Jsoup.connect(nextUrl).get();
                
                Elements storeElems = doc.select("div.list-rst__wrap");
                for(Element storeElem : storeElems){
                    if(storeElem.select("em.cpy-review-count").text().equals("-")){
                        continue;
                    }
                    Store store = new Store();
                    store.setUrl(storeElem.select("a.list-rst__rst-name-target").attr("href"))
                        .setImageFileUrl(storeElem.select("img.cpy-main-image").attr("data-original"));
                    storesList.add(store);
                }
                
                nextUrl = doc.select("a.c-pagination__arrow--next").attr("href");
                
            }catch(IOException e){
                e.printStackTrace();
            }
            
        }while(nextUrl.length() != 0);
        
        return storesList;
    
    }
    
    private static void sleepOneSec(){
        
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        
    }
    
    public static void scrapeStorePage(Store store){
        
        try{
        
            sleepOneSec();
            String conUrl = store.getUrl();
            Document doc = Jsoup.connect(conUrl+"dtlratings/").get();
            
            String name = doc.select("span.rstdtl-crumb").text();
            store.setName(name);
            
            String date = doc.select("p.rstinfo-opened-date").text();
            store.setOpenDate(date);
            
            Elements elems = doc.select("div.rating-box");
            for(Element ratebox : elems){
                
                if(!ratebox.text().contains("すべての点数")){
                    continue;
                }
                
                Elements rows = ratebox.select("li.clearfix");
                
                String score = ratebox.select("strong.score").text();
                store.setNotaScore(score);
                
                Map<String, String> eval = new LinkedHashMap<>();
                
                for(Element elem : rows){
                    String range = elem.select("p.score").text();
                    String count = elem.select("p.num").text().replaceAll("[\\[\\]]", "");
                    eval.put(extractMidScoreFrom(range), count);
                }
                
                store.setEvalMap(eval).setAve();
            
            }
            
            store.print();
            
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * expected range format
     * 5.0
     * or
     * 4.5〜4.9, 4.0〜4.4, ...
     *
     * return format
     * "5.0"
     * or
     * "4.7", "4.2", ...
     *
     */
    private static String extractMidScoreFrom(String range){
        
        /* expect 5.0 */
        if(range.length() == 3){
            return range;
        }
        
        if(range.length() == 7){
            return String.valueOf(Double.parseDouble(range.substring(0, 3)) + 0.2);
        }
        
        return "0";
    }
    
}

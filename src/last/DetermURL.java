package last;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;

public class DetermURL {
    
    private static final String END_COMMAND = "end";
    private static String nowUrl = "https://tabelog.com/rstLst/?Srt=D&SrtT=rvcn&svd=20180529&svt=1900&svps=2";
    private static final String rootUrl = "https://tabelog.com/rstLst/";
    private String url;
    private static List<String> history;
    
    public String getUrl(){ return this.url; }
    
    public void setUrl(String url){
        this.url = url;
    }
    
    DetermURL(){
        history = new ArrayList<>();
    }
    
    public String select(){
    
        try(InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);){
            
            selectRegion(br);
            selectGenre(br);
            
        }catch(IOException e){
            e.printStackTrace();
        }
    
        return "";
    }
    
    public String selectGenre(BufferedReader br){
        
        Map<String, String> genreMap = new HashMap<>();
        Map<String, String> genreMap2 = new HashMap<>();
        
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(nowUrl).get();
            Elements elems = doc.select("dl.list-balloon__table--genre");
            
            for(Element elem : elems){
                Elements eles = elem.select("li.list-balloon__text-item");
                for(Element ele : eles){
                    System.out.println(ele.text()+" : "+ele.select("a").attr("href"));
                    genreMap.put(elem.text(), elem.select("a").attr("href"));
                }
            }
            
            String genre = "";
            String key = "";
            
            while(true){
                System.out.print("select genre -> ");
                genre = br.readLine();
                
                if(genre.equals(END_COMMAND)){
                    /* change mode */
                    return nowUrl;
                }
                
                for(String str : genreMap.keySet()){
                    if(str.contains(genre)){
                        key = str;
                        break;
                    }
                }
                
                if(key.length()!=0){
                    break;
                }
                
                System.out.println("you missed input.");
                System.out.println("retry.");
                
            }
            
            nowUrl = genreMap.get(key);
            
            sleepOneSec();
            doc = Jsoup.connect(nowUrl).get();
            elems = doc.select("#js-leftnavi-genre-balloon li.list-balloon__list-item");
            for(Element elem : elems){
                System.out.println(elem.text()+" : "+elem.select("a").attr("href"));
                genreMap2.put(elem.text(), elem.select("a").attr("href"));
                
            }
            
            
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return "";
        
    }
    
    public String selectRegion(BufferedReader br){
        
        /* you mustn't select KYOTO!!! */
        
        Map<String, String> prefLink = setPrefLink();
        String area = "";
        
        try{
            
            
            while(true){
                System.out.print("select prefecture -> ");
                area = br.readLine();
                
                if(area.equals(END_COMMAND)){
                    /* change mode */
                    return nowUrl;
                }
                
                if(prefLink.containsKey(area)){
                    break;
                }
                
                System.out.println("you missed input.");
                System.out.println("retry.");
                
            }
            
            String next = prefLink.get(area);
            
            
            while(!next.equals(nowUrl)){
                next = selectUrl(next, br);
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        
        
        System.out.println("selected URL : " + nowUrl);
        
        return nowUrl;
    }
    
    
    private String selectUrl(String url, BufferedReader br){
        
        Map<String, String> region = new HashMap<>();
        
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(url).get();
            nowUrl = url;
            
            Elements elems = doc.select("#tabs-panel-balloon-pref-area li.list-balloon__list-item");
            for(Element elem : elems){
                System.out.println(elem.text()+" : "+elem.select("a").attr("href"));
                region.put(elem.text(), elem.select("a").attr("href"));
            }
            
            
            String key  = "";
            String area = "";
            while(true){
                System.out.print("select area -> ");
                area = br.readLine();
                
                if(area.equals(END_COMMAND)){
                    /* change mode */
                    return nowUrl;
                }
                
                for(String str : region.keySet()){
                    if(str.contains(area)){
                        key = str;
                        break;
                    }
                }
                
                if(key.length()!=0){
                    break;
                }
                
                System.out.println("you missed input.");
                System.out.println("retry.");
                
            }
            
            
            return region.get(key);
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return "";
    }
    

    
    private Map<String, String> setPrefLink(){
        
        Map<String, String> prefLink = new HashMap<>();
    
        try{
            
            sleepOneSec();
            String conUrl = rootUrl;
            Document doc = Jsoup.connect(conUrl).get();
            history.add(conUrl);
            
            Elements prefs = doc.select("a.list-balloon__recommend-target");
            for(Element elem : prefs){
                if(!elem.toString().contains("rstLst")){
                    System.out.println("pref -> link : "+elem.text()+" -> "+elem.attr("href"));
                    prefLink.put(elem.text(), elem.attr("href"));
                }
                
            }
            
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return prefLink;
    }
    
    private static void sleepOneSec(){
        
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        
    }
}

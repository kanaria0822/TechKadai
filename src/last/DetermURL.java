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
    private static final String rootUrl = "https://tabelog.com/rstLst/";
    private static String nowUrl = "";

    DetermURL(){
    }
    
    public String select(){
    
        try(InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);){
            
            selectRegion(br);
            selectGenre(br);
            
        }catch(IOException e){
            e.printStackTrace();
        }
    
        System.out.println("Selected URL : " + nowUrl);
        return nowUrl;
    }
    
    public String selectGenre(BufferedReader br){
        
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(nowUrl).get();
            
            checkCount(doc);
            
            Elements elems = doc.select("dl.list-balloon__table--genre");
            
            Map<String, String> genreMap = new HashMap<>();
            
            for(Element elem : elems){
                Elements eles = elem.select("li.list-balloon__text-item");
                for(Element ele : eles){
                    String genrename = ele.text();
                    String genreurl  = ele.select("a").attr("href");
                    if(genreurl.isEmpty()){
                        continue;
                    }
                    System.out.println(genrename + " : " + genreurl);
                    genreMap.put(genrename, genreurl);
                }
            }
            
            String genre = "";
            String key = "";
            
            while(true){
                System.out.print("select genre -> ");
                genre = br.readLine();
                
                if(genre.equals(END_COMMAND)){
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
            
            String next = genreMap.get(key);
            
            while(!next.equals(nowUrl)){
                next = selectDetailGenre(next, br);
            }
            
            
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return "";
        
    }
    
    public String selectDetailGenre(String url, BufferedReader br){
        
        Map<String, String> genreMap = new HashMap<>();
    
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(url).get();
            checkCount(doc);
            nowUrl = url;
            Elements elems = doc.select("#js-leftnavi-genre-balloon li.list-balloon__list-item");
            for(Element elem : elems){
                String genrename = elem.text();
                String genreurl  = elem.select("a").attr("href");
                if(genreurl.isEmpty()){
                    continue;
                }
                System.out.println(genrename + " : " + genreurl);
                genreMap.put(genrename, genreurl);
            }
            
            /* If the genreMap is empty, you have to make new method to scrape area at here.*/
            
            String key  = "";
            String genre = "";
            while(true){
                System.out.print("select genre -> ");
                genre = br.readLine();
                
                if(genre.equals(END_COMMAND)){
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
            
            return genreMap.get(key);
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return "";
    
    }
    
    private void checkCount(Document doc){
        String count = doc.select(".list-condition__count").text();
        System.out.println("Now count : " + count);
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
                next = selectDetailArea(next, br);
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        
        
        System.out.println("selected URL : " + nowUrl);
        
        return nowUrl;
    }
    
    
    private String selectDetailArea(String url, BufferedReader br){
        
        Map<String, String> region = new HashMap<>();
        
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(url).get();
            checkCount(doc);
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
            Document doc = Jsoup.connect(rootUrl).get();
            nowUrl = rootUrl;
            
            Elements prefs = doc.select("a.list-balloon__recommend-target");
            for(Element elem : prefs){
                if(!elem.toString().contains("rstLst")){
                    String pref = elem.text();
                    String link = elem.attr("href");
                    System.out.println(pref+" : "+link);
                    prefLink.put(pref, link);
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

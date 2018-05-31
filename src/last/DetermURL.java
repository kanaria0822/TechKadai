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
    private static final String BACK_COMMAND = "back";
    private static List<String> areaUrlHist;
    private static List<String> genreUrlHist;
    private static final String rootUrl = "https://tabelog.com/rstLst/";
    private static String nowUrl = "";
    private static List<String> areaChain;
    private static List<String> genreChain;
    
    public List<String> getAreaChain(){ return areaChain; }
    public List<String> getGenreChain(){ return genreChain; }

    DetermURL(){
        areaChain = new ArrayList<>();
        genreChain = new ArrayList<>();
        areaUrlHist = new ArrayList<>();
        genreUrlHist = new ArrayList<>();
    }
    
    public String select(){
    
        try(InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);){
            
            selectArea(br);
            selectGenre(br);
            
        }catch(IOException e){
            e.printStackTrace();
        }
    
        System.out.println("Selected URL : " + nowUrl + "\n");
        
        return nowUrl;
    }
    
    private String selectGenre(BufferedReader br){
        
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(nowUrl).get();
            
            checkCount(doc);
            
            Map<String, String> genreMap = new HashMap<>();
            
            Elements elems = doc.select("li.list-balloon__text-item");
            
            setElemsToNameAndLinkMap(elems, genreMap);
            
            String next = extractUrlFromMap(br, genreMap, "genre");
            
            while(!next.equals(nowUrl)){
                next = selectDetailGenre(next, br);
            }
            
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return "";
        
    }
    
    private String selectDetailGenre(String url, BufferedReader br){
        
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(url).get();
            genreUrlHist.add(url);
            nowUrl = url;
            
            checkCount(doc);
            
            
            Elements elems = doc.select("#js-leftnavi-genre-balloon li.list-balloon__list-item");
            
            Map<String, String> genreMap = new HashMap<>();
            
            setElemsToNameAndLinkMap(elems, genreMap);
            
            return extractUrlFromMap(br, genreMap, "genre");
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return "";
    
    }
    
    private void checkCount(Document doc){
        String count = doc.select(".list-condition__count").text();
        System.out.println("Now count : " + count);
    }
    
    private String selectArea(BufferedReader br){
        
        try{
            
            Map<String, String> prefLink = setPrefLink();
            
            String next = extractUrlFromMap(br, prefLink, "area");
            
            while(!next.equals(nowUrl)){
                next = selectDetailArea(next, br);
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        System.out.println("selected URL : " + nowUrl + "\n");
        
        return nowUrl;
    }
    
    
    private String selectDetailArea(String url, BufferedReader br){
        
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(url).get();
            areaUrlHist.add(url);
            checkCount(doc);
            nowUrl = url;
            
            Map<String, String> region = new HashMap<>();
            
            Elements elems = doc.select("#tabs-panel-balloon-pref-area li.list-balloon__list-item");
            
            /* when elems = 0, the 'area' element of the document has small headings */
            if(elems.size() == 0){
                elems = doc.select("li.list-balloon__sub-list-item");
            }
            
            setElemsToNameAndLinkMap(elems, region);
            
            return extractUrlFromMap(br, region, "area");
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return "";
    }
    
    
    private String extractUrlFromMap(BufferedReader br, Map<String, String> map, String display)
    throws IOException{
        
        String url  = "";
        String input = "";
        
        while(true){
        
            System.out.print("select "+ display + " -> ");
            input = br.readLine();
            System.out.println();
            
            if(END_COMMAND.equals(input)){
                return nowUrl;
            }
            
            // option
            if(BACK_COMMAND.equals(input)){
                if(display.equals("area")){
                    
                    String returl = updateListByBackComan(areaUrlHist, areaChain, "area");
                    if(!returl.isEmpty()){
                        return returl;
                    }
                    
                }else if(display.equals("genre")){
                    
                    String returl = updateListByBackComan(genreUrlHist, genreChain, "genre");
                    if(!returl.isEmpty()){
                        return returl;
                    }
                }
            }
            
            for(String key : map.keySet()){
                if(key.contains(input)){
                    if(display.equals("area")){
                        areaChain.add(key);
                    }else if(display.equals("genre")){
                        genreChain.add(key);
                    }
                    return map.get(key);
                }
            }
            
            System.out.println("you missed input.");
            System.out.println("retry.");
            
        }
    
    }
    
    private String updateListByBackComan(List<String> hisotory, List<String> searchPath, String way){
    
        int margin = 1;
        if(way.equals("area")){
            margin = 2;
        }
        int histLen = hisotory.size();
        if(histLen > margin){
            String returl = hisotory.get(histLen-2);
            hisotory.remove(histLen-1);
            hisotory.remove(histLen-2);
            searchPath.remove(searchPath.size()-1);
            return returl;
        }else{
            System.out.println("You can't back.");
            
        }
        return "";
    }
    
    private void setElemsToNameAndLinkMap(Elements elems, Map<String, String> map){
        
        for(Element elem : elems){
            String name = elem.text();
            String link = elem.select("a").attr("href");
            
            if(link.isEmpty() || link.contains("trend")){
                continue;
            }
            
            System.out.println(name + " : " + link);
            map.put(name, link);
        }
        
    }
    
    
    private Map<String, String> setPrefLink(){
        
        Map<String, String> prefLink = new HashMap<>();
    
        try{
            
            sleepOneSec();
            Document doc = Jsoup.connect(rootUrl).get();
            areaUrlHist.add(rootUrl);
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

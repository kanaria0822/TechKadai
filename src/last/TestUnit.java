package last;

import java.util.List;

public class TestUnit {
    
    public static void main(String[] args){
        
        DetermURL durl = new DetermURL();
        String url = durl.select();
        
        CrawlTabe ct = new CrawlTabe(url);
        List<Store> stores = ct.run();
        
        DrawHTML dhtml = new DrawHTML(stores);
        dhtml.runDrawHTML();
        
    }

}

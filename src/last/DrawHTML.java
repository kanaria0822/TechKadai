package last;

import java.util.List;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Collections;

public class DrawHTML {
    
    private final String filePath = "last.html";
    private List<Store> stores;
    private List<String> areaChain;
    private List<String> genreChain;
    String fullHtml;
    
    DrawHTML(List<Store> stores){
        
        this.stores = new ArrayList<>(stores);
        Collections.sort(this.stores, (e1, e2) ->
                         -(int)(( (e1.getAve() - e1.getNotaScoreDouble()) - (e2.getAve() - e2.getNotaScoreDouble()) ) * 100) );
        
    }
    
    DrawHTML(List<Store> stores, List<String> area, List<String> genre){
        this(stores);
        this.areaChain = area;
        this.genreChain = genre;
    }
    
    public void runDrawHTML(){
        setHTML();
        writeHTML(this.fullHtml);
        openHTML();
    
    }
    
    public void openHTML(){
        
        try{
            
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("open", this.filePath);
            Process process = pb.start();
    
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void setHTML(){
        
        String firstPartHtml =
          "<!DOCTYPE html>"
        + "<html>"
        + "<head>"
        + "<meta charset=\"utf-8\" />"
        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"last.css\">"
        + "</head>"
        + "<body>";
        
        String latterHtml =
          "</body>"
        + "</html>";
        
        String template = "";
        template = "<h2>食べログ！穴場かもしれない度ランキング</h2><br>";
        String area = String.join(" ー＞ ", areaChain);
        String genre = String.join(" ー＞ ", genreChain);
        template = template +"エリア　 ： "+ area + "<br>" +"ジャンル ： "+ genre + "<br>";
        for(Store store : this.stores){
            template =
            template
            + "<section class=\"item\">"
            + "<h3 class=\"title\">"
            + "<a href=\"" + store.getUrl() + "\" target=\"_blank\" >" + store.getName() + "</a>"
            + "</h3>"
            + "<div class=\"image\">"
            + "<img class=\"photo-image\" src=\"" + store.getImageFileUrl() + "\" >"
            + "<img class=\"map-image\" src=\"" + store.getMapImageUrl() + "\">"
            + "<a class=\"map-image-link\" href=\"" + store.getMapLinkUrl() +"\" target=\"_blank\" > 大きな地図で見る </a>"
            + "</div>"
            + "<div class=\"score\">"
            + "　実際の平均値 : <span class=\"real_ave_score\">" + String.format("%.2f", store.getAve()) + "</span><br>"
            + "表記上の平均値 : <span class=\"notation_ave_score\">" + store.getNotaScore() + "</span><br>"
            + "</div>"
            + "</section>";
        }
        
        this.fullHtml = firstPartHtml + template + latterHtml;
        
        
    }
    
    public void writeHTML(String html){
        
        Path filePath = Paths.get(this.filePath);
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            bw.write(html);
        }catch (IOException e) {
            e.printStackTrace();
        }
    
    }

}

package last;

import java.util.List;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.IOException;


public class DrawHTML {
    
    private final String filePath = "last.html";
    private List<Store> stores;
    String fullHtml;
    
    DrawHTML(List<Store> stores){
        this.stores = stores;
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
        template = "<h2>食べログのランキング</h2>";
        for(Store store : this.stores){
            template =
            template
            + "<section class=\"item\">"
            + "<h3 class=\"title\">"
            + "<a href=\"" + store.getUrl() + "\" target=\"_blank\" >" + store.getName() + "</a>"
            + "</h3>"
            + "<img src=\""+ store.getImageFilePath() + "\" width=\"150\" height=\"150\" ><br>"
            + "<div class=\"score\">"
            + "表記上の平均値 : <span class=\"notation_ave_score\">" + store.getNotaScore() + "</span><br>"
            + "実際の平均値　 : <span class=\"real_ave_score\">" + String.format("%.2f", store.getAve()) + "</span>"
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

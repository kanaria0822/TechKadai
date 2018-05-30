package last;

import java.util.Map;
import java.util.Map.Entry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

public class Store {
    
    private static final String dir = "./img/";
    
    private String url;
    private String name;
    /**
     * eval format
     * <String, String>
     * "5.0" -> "3"
     * "4.7" ->
     * "4.2" ...
     * "3.7"
     * ...
     * "1.2"
     *
     */
    private Map<String, String> eval;
    private double average;
    private String openDate;
    private String notaScore;
    private String imgFileUrl;
    private String imgFileName;
    private String imgFilePath;
    
    public String getUrl(){ return this.url; }
    public String getName(){ return this.name; }
    public double getAve(){ return this.average; }
    public String getOpenDate(){ return this.openDate; }
    public String getNotaScore(){ return this.notaScore; }
    public String getImageFileUrl(){ return this.imgFileUrl; }
    public String getImageFileName(){ return this.imgFileName; }
    public String getImageFilePath(){ return this.imgFilePath; }
    
    public Store setUrl(String url){
        this.url = url;
        return this;
    }
    
    public Store setName(String name){
        this.name = name;
        return this;
    }
    
    public Store setEvalMap(Map<String, String> map){
        this.eval = map;
        return this;
    }
    
    public Store setAve(){
        double sum = 0;
        int persons = 0;
        for(Map.Entry<String, String> entry : eval.entrySet()){
            int person = Integer.valueOf(entry.getValue());
            persons += person;
            sum += Double.parseDouble(entry.getKey()) * person;
        }
        
        if(persons == 0){
            this.average = 0;
            return this;
        }
        
        this.average = sum / persons;
        return this;
    
    }
    
    public Store setOpenDate(String date){
        this.openDate = date;
        return this;
    }
    
    public Store setNotaScore(String score){
        this.notaScore = score;
        return this;
    }
    
    public Store setImageFileUrl(String url){
        this.imgFileUrl = url;
        System.out.println("Image URL : " + this.imgFileUrl);
        setImageFileName();
        setImageFilePath();
        try{
            storeImage();
        }catch(IOException e){
            e.printStackTrace();
        }
        return this;
    }
    
    public Store setImageFileName(){
        
        String[] parts = this.imgFileUrl.split("/");
        
        if(parts.length < 8){
            this.imgFileName = parts[6];
        }else{
            this.imgFileName = parts[7];
        }
        
        return this;
    }
    
    public Store setImageFilePath(){
        this.imgFilePath = "./img/" + this.imgFileName;
        return this;
    }
    
    public void print(){
        System.out.println("Name      : " + this.name);
        System.out.println("URL       : " + this.url);
        /*
        for(Map.Entry<String, String> entry : this.eval.entrySet()){
            System.out.println("\t"+entry.getKey() + " : " + entry.getValue());
        }
         */
        System.out.println("Average   : "+this.average);
        System.out.println("NotaScore : "+this.notaScore);
        System.out.println("Image URL : "+this.imgFileUrl);
        System.out.println();
        
    }
    
    private void storeImage() throws IOException{
        
        // example Image URL
        // https:/ /tblg.k-img.com/restaurant/images/Rvw/34141/150x150_square_34141124.jpg
        
        String fileName = this.imgFileName;
        
        Path dirPath = Paths.get(dir);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        Path localFilePath = Paths.get(dir, fileName);
        Files.deleteIfExists(localFilePath);
        Files.createFile(localFilePath);
        
        sleepOneSec();
        URL url = new URL(this.imgFileUrl);
        URLConnection conn = url.openConnection();
        
        try (InputStream is = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(localFilePath.toFile(), false);) {
            
            int b;
            while ((b = is.read()) != -1) {
                fos.write(b);
            }
        }
        
        
    }
    
    private static void sleepOneSec(){
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    
}

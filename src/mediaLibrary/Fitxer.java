package mediaLibrary;

import java.io.File;

public abstract class Fitxer {
    protected String name, filePath, extension;
    
    public Fitxer(String name){
        this.name = name;
        this.extension = getExtension(name);
        
    }

    public String getName(){
        return name;
    }
    
    public String getFileType(){
        return getType(extension);
    }
    
    public String getPath(){
        return filePath;
    }
    
    public static String getFileType(File file){
        return getType(getExtension(file.getName()));
    }
    
    /**
     * Donat el nom d'un arxiu, retorna el nom sense la extensió
     * @param filename El nom de l'arxiu
     * @return el nom de l'arxiu sense la extensió
     */
    public static String getFileName(String filename){
        return (filename.split("\\.")[0]);
    }
    
    /**
     * Donat el nom d'un arxiu, retorna l'extensió
     * @param filename El nom de l'arxiu
     * @return l'extensió de l'arxiu
     */
    public static String getExtension(String filename){
        if (filename.split("\\.").length <= 1) return "";
        return (filename.split("\\.")[1]);
    }
    
    public static String getType(String ext){
        switch (ext){
            case "exe":
                return "executable";
            case "jpg":
            case "png":
            case "bmp":
                return "imatge";
            case "txt":
            case "doc":
            case "docx":
            case "pdf":
                return "document";
            case "mp3":
            case "FLAC":
                return "canço";
            case "mp4":
            case "wmv":
                return "video";
            default:
                return "desconegut";
        }
    }
    
    public boolean isInitialized(){
        return !(filePath == null || extension == null);
    }
    
    public static boolean isValidFileType(String type){
        type = type.toLowerCase();
        return (type.equals("document") || type.equals("imatge") || type.equals("video") || type.equals("canço"));
    }
    
    public boolean equals(String name, String filePath, String extension){
        return (this.name.equals(name) &&
                this.filePath.equals(filePath) &&
                this.extension.equals(extension));
    }
  
        
    public void setParams(String filePath, String type){
        this.filePath = filePath;
        this.extension = type;
    }
    
    public void setParams(String name, String filePath, String extension){
        this.name = name;
        setParams(filePath,extension);
    }
 
    public void setPath(String path){
        this.filePath = path;
    }
}

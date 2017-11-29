package mediaLibrary;

public class Document extends Fitxer {
    private String autor;
    
    public Document(String name, String autor) {
        super(name);
        this.autor = autor;
    }
    
    @Override
    public String toString(){
        return ("Document: " + name + ", " + filePath + ", " + extension + ", Autor: " + autor);
    }
}

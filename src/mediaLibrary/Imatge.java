package mediaLibrary;

public class Imatge extends Fitxer {
    protected String descripcio;
    protected int dimensiox, dimensioy;
    
    public Imatge(String name, String descripcio, int dimensiox, int dimensioy) {
        super(name);
        this.descripcio = descripcio;
        this.dimensiox = dimensiox;
        this.dimensioy = dimensioy;
    }
    
    @Override
    public String toString(){
        return ("Imatge: " + name + ", " + filePath + ", " + extension + ", Descripcio: " + descripcio + ", dimensions: " + dimensiox + "x" +dimensioy);
    }
}
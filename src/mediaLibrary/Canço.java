package mediaLibrary;

public class Canço extends Fitxer {
    protected String artista, album, genere;
    protected int durada;

    public Canço(String name, String artista, String album, String genere, int durada) {
        super(name);
        this.artista = artista;
        this.album = album;
        this.durada = durada;
        this.genere = genere;
    }
    
    @Override
    public String toString(){
        return ("Canço: " + name + ", " + filePath + ", " + extension + ", " + getFileType() + ", Artista: " + artista + ", Genere: " + genere + ", Album: " + album + ", Durada: " + durada + " segons");
    }
    
    
}


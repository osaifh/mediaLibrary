package mediaLibrary;

public class Video extends Fitxer{
    protected String director, genere;
    protected int durada;

    public Video(String name, String director, String genere, int durada) {
        super(name);
        this.director = director;
        this.durada = durada;
        this.genere = genere;
    }

    @Override
    public String toString(){
        return ("Video: " + name + ", " + filePath + ", " + extension + ", " + getFileType() + ", Director: " + director + ", Genere: " + genere + ", Durada: " + durada + " segons");
    }
}

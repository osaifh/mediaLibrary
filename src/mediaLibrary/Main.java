package mediaLibrary;


import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.Desktop;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Scanner user_input = new Scanner(System.in);
        String filePath = new File("").getAbsolutePath();
        String input;
        int command;
        ArrayList llistaFitxers = new ArrayList<>();
        Desktop desktop = Desktop.getDesktop();
        Fitxer selected = null;
        File index = null;
      
        String[] pathArray = {
            filePath+"\\media_library\\documents\\",
            filePath+"\\media_library\\pictures\\",
            filePath+"\\media_library\\videos\\",
            filePath+"\\media_library\\music\\"
        };
        
        String menu = "\n----MENU--------------------------------------------\n"+
                      "1 - mostrar: mostra la llista de fitxers\n"+
                      "2 - seleccinar: selecciona un arxiu\n"+
                      "3 - obrir: obre un arxiu amb el seu corresponent lector\n"+
                      "4 - eliminar: elimina l'arxiu seleccionat\n"+
                      "5 - moure: mou l'arxiu\n"+
                      "6 - sortir: surt del programa\n"+
                      "----------------------------------------------------\n";
        
        //busca l'arxiu index.txt en el directori
        File[] projectDirectory = new File(filePath).listFiles();
        boolean found = false;
        for (File x : projectDirectory){
            if (x.getName().equals("index.txt")){
                index = x;
                found = true;
            }
        }
        //omple la llista files amb els arxius sense filtrar
        ArrayList files = new ArrayList<>();
        for (String path : pathArray){
            File dir = new File(path);
            makeIndex(files,dir);
        }
        //si no troba index.txt, el crea
        if (!found){
            index = new File("index.txt");
            FileWriter fw = new FileWriter(index);
            PrintWriter pw = new PrintWriter(fw);
            for (int i = 0; i < files.size(); ++i){
                File curFile = (File)files.get(i);
                String type = Fitxer.getFileType(curFile);
                String name = Fitxer.getFileName(curFile.getName());
                if (!type.equals("desconegut") && !type.equals("executable")){
                    pw.println(type+","+name);
                }
            }
            fw.close();
        }
        //omple la llistaFitxers amb els arxius corresponents de files
        readIndex(llistaFitxers);
        for (String path : pathArray){
            fillList(files,path,llistaFitxers);
        }

        do {
            //mostra el menu, 
            System.out.println(menu);
            if (selected != null) System.out.println("Arxiu seleccionat: " + selected.toString());
            System.out.println("Introdueix una opció: ");
            input = user_input.nextLine().toLowerCase();
            command = parseInput(input.split(" ")[0]);
            switch (command) {
                case 1:
                    for (int i = 0; i < llistaFitxers.size(); ++i){
                        System.out.println((llistaFitxers.get(i)).toString());
                    }
                    break;
                case 2:
                    selected = searchFile(llistaFitxers,user_input);
                    System.out.println("S'ha seleccionat el fitxer " + selected.toString());
                    break;
                case 3:
                    if (selected != null){
                        File fileToRead = new File(selected.getPath());
                        System.out.println("Obrint el fitxer: " + fileToRead.getName());
                        desktop.open(fileToRead);
                    } else {
                        System.out.println("Error: no s'ha seleccionat cap arxiu");
                    }
                    break;
                case 4:    
                    if (selected != null){
                        if (deleteFile(user_input,selected,llistaFitxers)){
                            System.out.println("S'ha eliminat el fitxer seleccionat");
                            selected = null;
                            updateIndex(index,llistaFitxers);
                        }
                    } else {
                        System.out.println("Error: no s'ha seleccionat cap arxiu");
                    }
                    break;
                case 5:
                    if (selected != null){
                        moveFile(selected, user_input);
                    } else {
                        System.out.println("Error: no s'ha seleccionat cap arxiu");
                    }
                    break;
                case 6:
                    System.out.println("Sortint del programa");
                    break;
                default:
                    System.out.println("Error: l'input no es valid");
                    break;
            }
            
        } while (command!=6);
    }
    
    /**
     * Aquesta funció omple els parametres de la llista local ja existent amb els detalls dels arxius 
     * corresponents en el directori actual
     * @param files
     * @param path
     * @param llistaFitxers 
     */
    public static void fillList(ArrayList files, String path, ArrayList llistaFitxers){
        File currentFile = new File(path);
        File[] currentFileArray = currentFile.listFiles();
        if (currentFileArray == null || currentFileArray.length == 0){
            return;
        }
        if (currentFileArray.length == 1){
            files.add(currentFileArray[0]);
            for (int j = 0; j < llistaFitxers.size(); ++j){
                Fitxer currentFitxer = ((Fitxer)llistaFitxers.get(j));
                String currentFilename = Fitxer.getFileName(currentFileArray[0].getName());
                if (!currentFitxer.isInitialized() && currentFitxer.getName().equals(currentFilename)){
                    currentFitxer.setParams(currentFileArray[0].getAbsolutePath(),Fitxer.getExtension(currentFileArray[0].getName()));
                }
            }
        } 
        else {
            for (int i = 0; i < currentFileArray.length; ++i){
                if (currentFileArray[i].isDirectory()){
                    fillList(files,currentFileArray[i].getPath(),llistaFitxers);
                }
                else {
                    for (int j = 0; j < llistaFitxers.size(); ++j){
                        Fitxer currentFitxer = ((Fitxer)llistaFitxers.get(j));
                        String currentFilename = Fitxer.getFileName(currentFileArray[i].getName());
                        if (!currentFitxer.isInitialized() && currentFitxer.getName().equals(currentFilename)){
                            currentFitxer.setParams(currentFileArray[i].getAbsolutePath(),Fitxer.getExtension(currentFileArray[i].getName()));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Afegeix tots els arxius de un directori i de tots els seus subdirectoris a la llista de File files
     * @param files la llista a la qual afegim els arxius
     * @param curDir l'arxiu actual que estem evaluant, sigui un fitxer o un directori
     */
    public static void makeIndex(ArrayList files, File curDir){
        File[] currentFileArray = curDir.listFiles();
        if (currentFileArray == null || currentFileArray.length == 0) return;
        //if (currentFileArray.length==1 && !currentFileArray[0].isDirectory()) files.add(curDir);
        else {
            for (int i = 0; i < currentFileArray.length; ++i){
                if (currentFileArray[i].isDirectory()) makeIndex(files,currentFileArray[i]);
                else files.add(currentFileArray[i]);
            }
        }
    }
    
    /**
     * Llegeix l'arxiu index.txt i omple la llista de fitxers amb les dades dels arxius trobats.
     * Després de cridar aquesta funció, alguns dels valors encara no estaran inicialitzats, i caldra cridar la funció
     * fillList per poder omplir-los
     * @param llistaFitxers la llista de fitxers que omplirem
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void readIndex(ArrayList llistaFitxers) throws FileNotFoundException, IOException{
        File file = new File(("\\".substring(1))+"index.txt");
        if (file.isFile()){
            InputStream input = new FileInputStream(file.getAbsolutePath());
            InputStreamReader inputStream = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(inputStream);
            String line;
            while ((line = br.readLine())!=null){
                if (line.charAt(0)=='﻿') line = line.substring(1);
                String[] parts = line.split(",");
                Fitxer fitxer;
                switch (parts[0].toLowerCase()){
                    case "document":
                        if (parts.length >= 3){
                            fitxer = new Document(parts[1],parts[2]);
                        }
                        else fitxer = new Document(parts[1],"");
                        break;
                    case "imatge":
                        if (parts.length >= 5){
                            fitxer = new Imatge(parts[1],parts[2],Integer.parseInt(parts[3]),Integer.parseInt(parts[4]));
                        }
                        else fitxer = new Imatge(parts[1],"",0,0);
                        break;
                    case "video":
                        if (parts.length >= 5){
                            fitxer = new Video(parts[1],parts[2],parts[3],Integer.parseInt(parts[4]));
                        }
                        else fitxer = new Video(parts[1],"","",0);
                        break;
                    case "canço":
                        if (parts.length >= 6){
                            fitxer = new Canço(parts[1],parts[2],parts[3],parts[4],Integer.parseInt(parts[5]));
                        }
                        else fitxer = new Canço(parts[1],"","","",0);
                        break;
                    default:
                        fitxer = new Document("","");
                        break;
                }
                llistaFitxers.add(fitxer);
            }
            br.close();
            inputStream.close();
            input.close();
        }
        else {
            System.out.println("No s'ha trobat l'arxiu index.txt en el directori actual");
        }
    }
    
    /**
     * busca un fitxer en la llista de fitxers
     * @param llistaFitxers la llista de fitxers
     * @param user_input l'scanner utilitzat per rebre l'input de l'usuari
     * @return el fitxer seleccionat, o, en el cas de que la selecció no sigui valida, null
     */
    public static Fitxer searchFile(ArrayList llistaFitxers, Scanner user_input){
        String fileType;
        int choice;
        String[] fileTypes = {"document","imatge","video","canço"};
        do {
            System.out.print("Quin tipus de fitxer vols buscar?\n"
                             + "1. document\n"
                             + "2. imatge\n"
                             + "3. video\n"
                             + "4. canço\n"
            );
            choice = parseInput(user_input.nextLine());
            if (choice < 1 || choice > 4) System.out.println("Error: input invalid");
        } while (choice < 1 || choice > 4);
        fileType = fileTypes[choice-1];
        System.out.println("Introdueix el nom del "+fileType+" que vols buscar");
        String title = user_input.nextLine().toLowerCase();
        System.out.println("Buscant el " + fileType + " amb nom "+title+":");
        ArrayList foundList = searchFileList(title,fileType,llistaFitxers);
        while (foundList.size()>1){
            System.out.println("S'ha trobat més d'un resultat:");
            for (int i = 0; i < foundList.size(); ++i){
                System.out.println(foundList.get(i).toString());
            }
            System.out.println("Introdueix el titol especific d'una de les opcions");
            title = user_input.nextLine().toLowerCase();
            foundList = searchFileList(title,fileType,foundList);
        }
        if (foundList.isEmpty()){
            System.out.println("No s'ha trobat el fitxer");
            return null;
        }
        else {
            System.out.println("Selecciont: " + foundList.get(0).toString());
            return ((Fitxer)foundList.get(0));
        }
    }
    
    /**
     * retorna una llista amb tots els arxius del tipus corresponent a fileType en fileList que continguin les paraules
     * corresponents a title
     * @param title el titol de l'arxiu que estem buscant
     * @param fileType el tipus d'arxiu que estem buscant
     * @param fileList la llista de fitxers
     * @return una llista amb tots els fitxers que compleixen les propietats
     */
    public static ArrayList searchFileList(String title, String fileType, ArrayList fileList){
        ArrayList foundList = new ArrayList<>();
        String[] wordsToSearch = title.split(" ");
        boolean[] foundWords = new boolean[wordsToSearch.length];
        for (int i = 0; i < fileList.size();++i){
            Fitxer elem = (Fitxer)fileList.get(i);
            if (elem.getFileType().equals(fileType)){
                for (boolean b : foundWords) b = false;
                for (int j = 0; j < wordsToSearch.length; j++){
                    foundWords[j] = (elem.getName().toLowerCase().contains(wordsToSearch[j]));
                }
                boolean allWordsMatch = true;
                for (boolean b : foundWords) if (!b) allWordsMatch = false;
                if (allWordsMatch) foundList.add(elem);           
            }
        }
        return foundList;
    }
   
    /**
     * Elimina l'arxiu seleccionat, eliminant-lo també de la llista
     * @param user_input l'scanner utilitzat per rebre l'input de l'usuari
     * @param selected l'arxiu seleccionat
     * @param llistaFitxers la llista de fitxers
     * @return retorna true si l'arxiu s'ha eliminat, retorna fals si no s'ha eliminat
     */
    public static boolean deleteFile(Scanner user_input, Fitxer selected, ArrayList llistaFitxers){
        System.out.println("Estas segur de que vols eliminar el fitxer \"" + selected.getName() + "\" ?");
        String input = user_input.nextLine();
        if (!input.isEmpty() && (input.charAt(0) == 's' || input.charAt(0) == 'y')){
            File fileToDelete = new File(selected.getPath());
            fileToDelete.delete();
            llistaFitxers.remove(selected);
            return true;
        }
        else {
            System.out.println("S'ha cancelat la operació");
            return false;
        }
    }
    
    /**
     * mou un arxiu a un directori diferent
     * @param selected l'arxiu seleccionat actualment
     * @param user_input l'scanner utilitzat per rebre l'input de l'usuari
     */
    public static void moveFile(Fitxer selected, Scanner user_input){
        String newPath = selectPath(selected.getPath(),user_input);
        File fileToMove = new File(selected.getPath());
        File movedFile = new File(newPath);
        if (fileToMove.renameTo(movedFile)){
            System.out.println("S'ha mogut correctament l'arxiu " + fileToMove.getName());
            selected.setPath(newPath);
        }
        else {
            System.out.println("Error: no s'ha mogut correctament l'arxiu");
        }
    }
    
    /**
     * selecciona una ruta absoluta utilitzada per canviar la ruta d'un arxiu
     * @param currentPath el path actual
     * @param user_input l'scanner utilitzat per agafar l'input de l'usuari
     * @return el nou path especificiat per l'usuari
     */
    public static String selectPath(String currentPath, Scanner user_input){
        String fileSeparator = Pattern.quote(System.getProperty("file.separator"));
        String[] parts = currentPath.split(fileSeparator);
        String filename = parts[parts.length-1];
        int size = filename.length();
        String basePath = currentPath.substring(0, currentPath.length() - size);
        currentPath = basePath;
        String menu = " Opcions: \n"
                    + " cd : canvia de directori\n"
                    + " ls: llista els arxius del directori\n"
                    + " ok: utilitza la ruta actual\n"
                    + " help: mostra el menu\n";
        String input, command;
        System.out.println(menu);
        do {
            System.out.println(currentPath);
            input = user_input.nextLine();
            command = input.split(" ")[0];
            switch (command) {
                case "cd":
                    String previousPath = currentPath;
                    if (input.split(" ").length>1){
                        input = input.split(" ")[1];
                        if (input.substring(0,2).equals("..")){
                            String[] pathParts = currentPath.split("\\\\");
                            currentPath = pathParts[0] + "\\";
                            for (int i = 1; i < pathParts.length-1;++i){
                                currentPath += pathParts[i] + "\\";
                            }
                        }
                        else currentPath += input + "\\";
                        //si la ruta no es valida, tornem a la ruta anterior
                        if (!(new File(currentPath).isDirectory())){
                            currentPath = previousPath;
                            System.out.println("Ruta no valida");
                        }
                    }
                    break;
                case "ls":
                    File folder = new File(currentPath);
                    if (folder.isDirectory()){
                        File[] listFiles = folder.listFiles();
                        for (int i = 0; i < listFiles.length; ++i){
                            System.out.println("i: " + i + " " + listFiles[i].getName());
                        }
                    }   break;
                case "help":
                    System.out.println(menu);
                    break;
                default:
                    break;
            }
        } while (!input.equals("ok"));
        File path = new File(currentPath);
        if (path.isDirectory()){
            return currentPath.concat(filename);
        }
        else {
            System.out.println("Error: ruta no valida");
            return selectPath(basePath,user_input);
        }
    }
    
    /**
     * Actualitza l'index, esborrant els elements que han sigut esborrats
     * @param index
     * @param llistaFitxers
     * @throws IOException 
     */
    public static void updateIndex(File index, ArrayList llistaFitxers) throws IOException{
        List<String> fileContent;
        fileContent = new ArrayList<>(Files.readAllLines(Paths.get(index.getAbsolutePath()),StandardCharsets.UTF_8));
        for (int i = 0; i < fileContent.size(); i++) {
            String name = fileContent.get(i).split(",")[1];
            boolean found = false;
            for (int j = 0; j < llistaFitxers.size() && !found; j++){
                found = (((Fitxer)llistaFitxers.get(j)).getName().equals(name));
            }
            if (!found) fileContent.remove(i);
        }
        Files.write(Paths.get(index.getAbsolutePath()), fileContent, StandardCharsets.UTF_8);
    }
    
    /**
     * comprova si un input té unicament caracters numerics. Si és correcte, retorna el numero corresponent com a int. Si no, retorna -1
     * @param input l'input que volem interpretar
     * @return el valor de input si és númeric o -1 si no ho és
     */
    public static int parseInput(String input){
        for (char c : input.toCharArray()){
            if (c<'0' || c>'9') return -1;
        }
        return Integer.parseInt(input);
    }
}
package fourword.model;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by jonathan on 2015-06-22.
 */
public class Dictionary {

    //Should be lowercase-words
    private HashSet<String> lowerWords;
    public final static String CHARSET = "ISO-8859-1";

    public Dictionary(HashSet<String> lowerWords){
        this.lowerWords = lowerWords;
    }

    public static Dictionary fromFiles(String charset, File... files) {
        try{
            HashSet<String> lowerWords = new HashSet<String>();
            for(File file : files){
                BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(file), charset ) );
                Scanner scanner = new Scanner(reader);
                while(scanner.hasNext()){
                    lowerWords.add(scanner.nextLine().toLowerCase());
                }
                reader.close();
                scanner.close();
            }
            return new Dictionary(lowerWords);
        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }

    public static Dictionary fromFiles(File... files) {
        return fromFiles(CHARSET, files);
    }

    public boolean isWord(String word){
        return lowerWords.contains(word.toLowerCase());
    }
}

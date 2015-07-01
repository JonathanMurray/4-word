package fourword;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jonathan on 2015-06-30.
 */
public class DictionaryCreator {

    public static void main(String[] args) throws IOException {
//        FileReader reader = new FileReader(new File("swedish-word-list"));
        final String charset = "ISO-8859-1";
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( "swedish-word-list" ), charset ) );
        Scanner scanner = new Scanner(reader);
        List<String> twoLetterWords = new ArrayList<String>();
        List<String> threeLetterWords = new ArrayList<String>();
        List<String> fourLetterWords = new ArrayList<String>();
        List<String> fiveLetterWords = new ArrayList<String>();

        System.out.println("Reading...");
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            switch (line.length()){
                case 2:
                    twoLetterWords.add(line);
                    break;
                case 3:
                    threeLetterWords.add(line);
                    break;
                case 4:
                    fourLetterWords.add(line);
                    break;
                case 5:
                    fiveLetterWords.add(line);
                    break;
            }
        }
        System.out.println("Writing...");
        writeWordsToFile(twoLetterWords, "swedish-word-list-2-letters", charset);
        writeWordsToFile(threeLetterWords, "swedish-word-list-3-letters", charset);
        writeWordsToFile(fourLetterWords, "swedish-word-list-4-letters", charset);
        writeWordsToFile(fiveLetterWords, "swedish-word-list-5-letters", charset);
        System.out.println("Done!");
    }

    private static void writeWordsToFile(List<String> words, String path, String charset) throws IOException {
        System.out.println("Writing " + words.size() + " words to " + path + " ...");
        Writer writer = new OutputStreamWriter(new FileOutputStream(path), charset);
        for(String w : words){
            writer.write(w + "\n");
        }
        writer.flush();
        writer.close();
    }

}

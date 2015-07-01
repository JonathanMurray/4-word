package fourword.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 2015-06-22.
 */
public class ScoreCalculator {

    Dictionary dictionary;

    public ScoreCalculator(Dictionary dictionary){
        this.dictionary = dictionary;
    }

    public List<String> extractLowerWords(String whole){
        return extractLowerWords(whole, dictionary);
    }

    public static List<String> extractLowerWords(String whole, Dictionary dictionary){
        List<String> words = new ArrayList<String>();
        for(int wordLength = 2; wordLength <= whole.length(); wordLength ++){
            for(int start = 0; start <= whole.length() - wordLength; start ++){
                String substring = whole.substring(start, start + wordLength);
                if(dictionary.isWord(substring)){
                    words.add(substring.toLowerCase());
                }
            }
        }
        return words;
    }

    public static int getValidWordScore(String word){
        return word.length();
    }

    public int computeScore(String whole){
        return computeScore(whole, dictionary);
    }

    public static int computeScore(String whole, Dictionary dictionary){
        int score = 0;
        List<String> words = extractLowerWords(whole, dictionary);
        for(String word : words){
            score += getValidWordScore(word);
        }
        return score;
    }

}

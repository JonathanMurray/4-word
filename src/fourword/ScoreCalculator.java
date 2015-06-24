package fourword;

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

    public List<String> extractWords(String whole){
        List<String> words = new ArrayList<String>();
        for(int wordLength = 2; wordLength <= whole.length(); wordLength ++){
            for(int start = 0; start <= whole.length() - wordLength; start ++){
                String substring = whole.substring(start, start + wordLength);
                if(dictionary.isWord(substring)){
                    words.add(substring);
                }
            }
        }
        return words;
    }

    public int computeScore(String whole){
        int score = 0;
        List<String> words = extractWords(whole);
        for(String word : words){
            score += word.length(); //TODO
        }
        return score;
    }

}

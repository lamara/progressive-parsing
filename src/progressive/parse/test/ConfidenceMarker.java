/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progressive.parse.test;

import java.util.ArrayList;

/**
 *
 * @author alex
 */
public class ConfidenceMarker {
    
    static final int CONSECUTIVE_MATCHES_THRESHOLD = 3; //2 seems to be a good number, 1 gives more accurate timestamps but it might get thrown off track really easily
    static final int SCAN_THRESHOLD = 30;
    
    
    public static void markWords(ArrayList<Word> parsedWords, ArrayList<String> originalWords, int lowestOriginalIndex)
    {
        int parIndex = 0;
        int origIndex = 0;
        
        int consecutiveMatches = 0; //we're only going to count consecutive matches above a threshold as a confident timestamp location
        int lastConfidentOrigIndex = -1;
        int lastConfidentParIndex = -1;
        String lastMatchedText = "";
        
        int currParIndex = parIndex;
        
        while (origIndex < originalWords.size() && parIndex < parsedWords.size())
        {
            
            for (int i = 0; i < SCAN_THRESHOLD; i++) {
                if (origIndex + i >= originalWords.size()) {
                    //i = 0;
                    //parIndex++;
                    break;
                }                
                Word parsedWord = parsedWords.get(currParIndex);
                
                String parsedString = parsedWord.getWord();
                String originalString = originalWords.get(origIndex + i);
                
                
                if (stringIsEqualToString(parsedString, originalString) && (origIndex + i) > lastConfidentOrigIndex) { //parsedString.equals(originalString)) {
    
                    if (lastMatchedText.equals("") || stringIsEqualToString(lastMatchedText, originalWords.get(origIndex + i - 1))) { //lastMatchedText.equals(originalWords.get(origIndex + i - 1))) {
                        lastMatchedText = originalString;
                        origIndex = origIndex + i + 1;
                        consecutiveMatches++;
                        i = -1;
                        currParIndex++;
                        continue;
                    }
                    else if (consecutiveMatches == 0) {
                        lastMatchedText = originalString;
                        consecutiveMatches = 1;
                        origIndex = origIndex + i + 1;
                        i = -1;
                        currParIndex++;
                        continue;
                    }
                    /*
                    else {
                        consecutiveMatches = 0;
                        break;
                    }
                            */
                }
                
                if ((i == SCAN_THRESHOLD || i >= originalWords.size())
                       && consecutiveMatches < CONSECUTIVE_MATCHES_THRESHOLD)
                {
                    consecutiveMatches= 0;
                    //origIndex = lowestOriginalIndex;
                    break;
                }
            }
            parIndex++;
            currParIndex = parIndex;
            if (consecutiveMatches >= CONSECUTIVE_MATCHES_THRESHOLD) {
                lowestOriginalIndex = origIndex - 1;
                
                for (int i = lowestOriginalIndex; i > lowestOriginalIndex - consecutiveMatches; i--)
                {
                    parsedWords.get(i).isConfident = true;
                }
                consecutiveMatches = 0;
            }
        }
        
        
    }
    
    
    private static boolean stringIsEqualToString(String string1, String string2) 
    {
        string1 = string1.toLowerCase();
        string1 = string1.replaceAll("\\.", "");
        string1 = string1.replaceAll(",", "");
        string1 = string1.replaceAll("\\?", "");
        string1 = string1.replaceAll("\\!", "");
        string1 = string1.replaceAll("\"", "");
        string1 = string1.trim();
        
        string2 = string2.toLowerCase();
        string2 = string2.replaceAll("\\.", "");
        string2 = string2.replaceAll(",", "");
        string2 = string2.replaceAll("\\?", "");
        string1 = string1.replaceAll("\\!", "");
        string2 = string2.replaceAll("\"", "");
        string2 = string2.trim();
        
        return string1.equals(string2);
    }
}

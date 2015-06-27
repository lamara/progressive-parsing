/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progressive.parse.test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



import edu.cmu.sphinx.alignment.LongTextAligner;
import edu.cmu.sphinx.api.SpeechAligner;
import edu.cmu.sphinx.result.WordResult;

/**
 *
 * @author alex
 */
public class Aligner {
    private static final String ACOUSTIC_MODEL_PATH =
            "res/en-us";
    private static final String DICTIONARY_PATH =
            "res/cmudict-en-us.dict";
    private static final String TEXT = "mister and misses dursely of number four privet drive were proud to say that they "
            + "were perfectly normal thank you very much";

    public static void align() throws Exception {
        URL audioUrl;
        String transcript;
        
        audioUrl = new File("res/harry-one-sentence.wav").toURI().toURL();
       
        transcript = TEXT;
       
        String acousticModelPath = ACOUSTIC_MODEL_PATH;
        String dictionaryPath = DICTIONARY_PATH;
        String g2pPath = null;
        SpeechAligner aligner = new SpeechAligner(acousticModelPath, dictionaryPath, g2pPath);

        List<WordResult> results = aligner.align(audioUrl, transcript);
        List<String> stringResults = new ArrayList<String>();
        for (WordResult wr : results) {
            
            long start = wr.getTimeFrame().getStart();
            long end = wr.getTimeFrame().getEnd();
            String string = wr.getWord().getSpelling();
            System.out.println(string + ": " + start + ", " + end);
            stringResults.add(wr.getWord().getSpelling());
        }
        
       // aligner.
        
        LongTextAligner textAligner =
                new LongTextAligner(stringResults, 2);
        List<String> words = aligner.getWordExpander().expand(transcript);
       // List<String> words = aligner.sentenceToWords(sentences);
        
        int[] aid = textAligner.align(words);
        
        int lastId = -1;
        for (int i = 0; i < aid.length; ++i) {
            if (aid[i] == -1) {
                System.out.format("- %s\n", words.get(i));
            } else {
                if (aid[i] - lastId > 1) {
                    for (WordResult result : results.subList(lastId + 1,
                            aid[i])) {
                        System.out.format("+ %-25s [%s]\n", result.getWord()
                                .getSpelling(), result.getTimeFrame());
                    }
                }
                System.out.format("  %-25s [%s]\n", results.get(aid[i])
                        .getWord().getSpelling(), results.get(aid[i])
                        .getTimeFrame());
                lastId = aid[i];
            }
        }

        if (lastId >= 0 && results.size() - lastId > 1) {
            for (WordResult result : results.subList(lastId + 1,
                    results.size())) {
                System.out.format("+ %-25s [%s]\n", result.getWord()
                        .getSpelling(), result.getTimeFrame());
            }
        }
    }
                
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progressive.parse.test;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.ConfidenceResult;
import edu.cmu.sphinx.result.MAPConfidenceScorer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;

/**
 *
 * @author timmy
 */


public class AudioBookToText 
{
    public static void audioToText(String audioFileName, String actualBookTextFile, String outFileName, boolean skipIfExists) throws IOException
    {
        //The speech-to-text can take a long time, so skip it if the file already exists
        if (false)//skipIfExists && new File(outFileName).isFile())
        {
                System.out.println("Option to skip is selected, and the timestamped text already exists, so skipping the time-consuming speech-to-text");
                return;
        }
        
        //Create the new grammar using all the words found in the actual text
        BufferedReader reader = null;
        try 
        {
            reader = new BufferedReader(new FileReader(actualBookTextFile));
        } 
        catch (FileNotFoundException e) 
        {
            System.err.println("Cannot open file containing original text");
            e.printStackTrace();
            System.exit(1);        
        }
        
        ArrayList<String> originalWords = new ArrayList<String>();
        HashSet<String> wordsInOriginalText = new HashSet<String>();
        String readWord;
        try 
        {
            while ( (readWord = reader.readLine()) != null )
            {
                originalWords.add(readWord);
                wordsInOriginalText.add(readWord);
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Failure in reading file containing original text");
            e.printStackTrace();
            System.exit(1);
        }
        
        PrintWriter grammarFile = null;
        try
        {
            grammarFile = new PrintWriter("res/words.gram", "UTF-8");
        }
        catch(FileNotFoundException e)
        {
            System.err.println("Failed to create output file");
            e.printStackTrace();
            System.exit(1);
        } 
        catch (UnsupportedEncodingException e) 
        {
            System.err.println("Unsupported Format for audio-text output file");
            e.printStackTrace();
            System.exit(1);
        }
        
        grammarFile.print("#JSGF V1.0;\n\n" +
                            "/**\n" +
                            "* JSGF Grammar for words in Harry Potter\n" +
                            "*/\n\n" +
                            "grammar words;\n\n" +
                            "public <words> = (Mr ");
        
        for (String s: wordsInOriginalText)
        {
            if (s.length() > 0)
            {
                grammarFile.print(" | " + s);
            }
        }
        grammarFile.println(") * ;");
       
        grammarFile.close();
        
        //Create the voice-to-text
        URL audioBookFile = null;
        URL config = null;
        try
        {
            
            audioBookFile = new File(audioFileName).toURI().toURL();
            config = new File("res/config.xml").toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            System.err.println("Failed to open up file");
            e.printStackTrace();
            System.exit(1);
        }
        
        PrintWriter outputFile = null;
        try
        {
            outputFile = new PrintWriter(outFileName, "UTF-8");
        }
        catch(FileNotFoundException e)
        {
            System.err.println("Failed to create output file");
            e.printStackTrace();
            System.exit(1);
        } 
        catch (UnsupportedEncodingException e) 
        {
            System.err.println("Unsupported Format for audio-text output file");
            e.printStackTrace();
            System.exit(1);
        }
         
        Configuration configuration = new Configuration();
        
        configuration
                .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");

        // You can also load model from folder
        // configuration.setAcousticModelPath("file:en-us");

        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");
        configuration.setUseGrammar(true);
        
        configuration.setGrammarName("words");
        configuration.setGrammarPath("res/");
        
        
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        
         InputStream stream = new FileInputStream(audioFileName);
        
         recognizer.startRecognition(stream);
         
         MAPConfidenceScorer scorer = new MAPConfidenceScorer();
         
        ArrayList<Word> parsedWords = new ArrayList<Word>();
         
        SpeechResult result;
        while ((result = recognizer.getResult()) != null)
        {
            ConfidenceResult confidence = scorer.score(result.getResult());
            for (WordResult r : confidence.getBestHypothesis().getWords())
            {
                System.out.println(r.getWord().getSpelling() + " " + r.getConfidence());
                
                if (r.isFiller()) {
                    continue;
                }
                
                Word word = new Word();
                word.setWord(r.getWord().getSpelling());
                word.setStartTime(r.getTimeFrame().getStart());
                word.setEndTime(r.getTimeFrame().getEnd());
                parsedWords.add(word);
            }
            //double confidence = scorer.score(result).getBestHypothesis().getConfidence();
            /*
            for (WordResult r : result.getWords()) {
                //System.out.println(r.getConfidence());
                System.out.println(r);
            }
            */

            /*
            String resultTxt = result.getResult().getTimedBestResult(false).get(0).getWord().getSpelling();
            double start = result.getResult().getTimedBestResult(false).get(0).getTimeFrame().getStart();//getTimedBestResult(false);
            double end = result.getResult().getTimedBestResult(false).get(0).getTimeFrame().getEnd();            
                
            System.out.println(resultTxt + "," + start + "," + end);
                    */
        }
        outputFile.close();
        
        ConfidenceMarker.markWords(parsedWords, originalWords, 0);
        
        System.out.println("==== Marked Words =====");
        for (Word word : parsedWords)
        {
            System.out.println(word.getWord() + ", confident: " + word.isConfident);
        }
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progressive.parse.test;

/**
 *
 * @author alex
 */
public class ProgressiveParseTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        AudioBookToText.audioToText("res/harry-one-sentence-3x.wav", "res/harry-one-sentence.txt", "hey", true);
        //System.out.println("Hey");
    }
    
}

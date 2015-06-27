/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progressive.parse.test;

/**
 *  A text sample is a string and an associated start time and end time.
 *  The string is usually either a single word that is spoken, or
 *  a phrase consisting of multiple words that should be shown together.
 * 
 * 
 * @author alex
 */
public class Word {
    private boolean negative;
    private double startTime;
    private double endTime;
    
    private String text;
    
    public boolean isImportant = false;
    
    //If we are confident this is a correctly-timed word (we didn't have to interpolate its timestamp)
    public boolean isConfident = false;
    //sometimse we are only mostly confident that a word is correctly timed
    public boolean isSoftConfident = false;
    
    public boolean isSilent = false;
    
    public int index;
    
    private double happiness;
    
    //5.375239679 is the (unweighted) average happiness
    public static final double AVERAGE_HAPPINESS = 5.375239679;
    
    public Word()
    {
        this((String)null, 0.0, 0.0, new Double(AVERAGE_HAPPINESS));
    }
   
    
    public Word(String word, double startTime, double endTime, Double happiness) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.text = word;
        this.setHappiness(happiness);
    }
    
    public double getHappiness()
    {
        return happiness;
    }
    
    public void setHappiness(Double happiness)
    {
        if (happiness != null)
        {
            this.happiness = happiness.doubleValue();
        }
        else
        {
            this.happiness = AVERAGE_HAPPINESS;
        }
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public boolean isNegative() {
        return negative;
    }
    
    public String getWord() {
        return text;
    }

    public void setWord(String word) {
        this.text = word;
    }
    
    public void setNegative(boolean val) {
        this.negative = val;
    }

    public String getText() {
       return text;
    }
}

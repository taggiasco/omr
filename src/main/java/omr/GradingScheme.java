package omr;

import omr.QuestionGroup.Orientation;

/**
 * Calculates points from answers.
 */
public class GradingScheme {

    private double correctScore;
    private double incorrectScore;
    private double defaultScore;
    private double multipleSelectedScore;
    private double minScore;
    private double maxScore;
    
    /**
     * Constructor
     * @param correctScore Points to add if correct answer is selected
     * @param incorrectScore Points to add if incorrect answer is selected
     * @param defaultScore Points to add if nothing is selected
     * @param multipleSelectedScore Points to add if multiple answers are selected
     * @param minScore Minimum points per question
     * @param maxScore Maximum points per question
     */
    public GradingScheme(double correctScore, double incorrectScore, double defaultScore, double multipleSelectedScore, double minScore, double maxScore) {
        this.correctScore = correctScore;
        this.incorrectScore = incorrectScore;
        this.defaultScore = defaultScore;
        this.multipleSelectedScore = multipleSelectedScore;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }
    
    public static final double SCORE_CORRECT = 
            OMRProperties.getDouble("score-correct", 1.0 );
    
    public static final double SCORE_INCORRECT = 
            OMRProperties.getDouble("score-incorrect", -0.5 );
    
    public static final double SCORE_NONE = 
            OMRProperties.getDouble("score-none", 0.0 );
    
    
    /**
     * Default constructor. 1 point for correct answer, -0.5 for incorrect or multiple selected, 0 if nothing is selected 
     */
    public GradingScheme() {
        this(SCORE_CORRECT , SCORE_INCORRECT, SCORE_NONE, SCORE_INCORRECT, 
                SCORE_INCORRECT, SCORE_CORRECT);
    }
    
    /**
     * Calculates the total score of all question groups in a sheet.
     */
    public double getScore(Sheet sheet, SheetStructure structure) {
        double score = 0.0;
        
        for (QuestionGroup group : structure.getQuestionGroups()) { 
            score += getScore(sheet, group); 
        }
        
        return score;
    }
    
    /**
     * Calculates the total score of one question group in a sheet.
     * @param group QuestionGroup from the SheetStructure 
     */
    public double getScore(Sheet sheet, QuestionGroup group) {
        double score = 0.0;
        
        for (int i = 0; i < group.getQuestionsCount(); i++) {
            score += getScore(sheet, group, i); 
        }
        
        return score;
    }
    
    /**
     * Returns the score from a single question.
     * @param sheet
     * @param group
     * @param question Local question number (row number)
     */
    public double getScore(Sheet sheet, QuestionGroup group, int question) {
        int alternativesSelected = 0;
        boolean correctAnswerSelected = false;
        boolean incorrectAnswerSelected = false;
        
        if (group.getOrientation() != Orientation.VERTICAL && group.getOrientation() != Orientation.HORIZONTAL) {
        	return 0.0;
        }
        
        // Iterate through alternatives
        for (int alternative = 0; alternative < group.getAlternativesCount(); alternative++) {
            boolean selected;
            if (group.getOrientation() == Orientation.HORIZONTAL) {
            	selected = sheet.getAnswer(group, alternative, question) < 0;
            } else {
            	selected = sheet.getAnswer(group, question, alternative) < 0;
            }
            
            boolean shouldBeSelected = group.getCorrectAnswer(question, alternative);
            
            // Is any of the correct answers selected?
            if (shouldBeSelected && selected) {
                correctAnswerSelected = true;
            }
            
            // Is any of the incorrect answers selected?
            if (!shouldBeSelected && selected) {
                incorrectAnswerSelected = true;
            }
            
            // Calculate how many alternatives are selected
            if (selected) {
                alternativesSelected++;
            }
        }
        
        // Calculate score
        double score;
        if (alternativesSelected > 1) {
            score = multipleSelectedScore;
        } else if (correctAnswerSelected) {
            score = correctScore;
        } else if (incorrectAnswerSelected) {
            score = incorrectScore;
        } else {
            score = defaultScore;
        }
        
        // Check bounds 
        if (score < this.minScore) {
            score = this.minScore;
        } else if (score > this.maxScore) {
            score = this.maxScore;
        }
        
        return score;
    }
    
}

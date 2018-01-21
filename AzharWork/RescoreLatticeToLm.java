package AzharWork;

import java.util.LinkedList;
import java.util.List;

import edu.cmu.sphinx.linguist.WordSequence;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.linguist.language.ngram.LanguageModel;
import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.util.LogMath;

public class RescoreLatticeToLm {
/*	protected final Lattice lattice;
    protected final LanguageModel model;
    private int depth;*/
	
	public Lattice lattice;
	public LanguageModel model;
	public int depth; 
    /**
     * Azhar: Why languageWeight is Fixed!
     * Azhar: I added another constructor in Jan 2018 with variable lm weight 
     */
    private float languageWeigth = 8.0f;

    /**
     * Create a new Lattice optimizer
     * 
     * @param lattice lattice to rescore
     * @param model language model to rescore
     * 
     */
    public RescoreLatticeToLm(Lattice lattice, LanguageModel model) {
        this.lattice = lattice;
        this.model = model;
        this.languageWeigth = 8.0f;
        depth = model.getMaxDepth();
    	
        
    
    	
    	
    }
    
    /**
     * This constructor enables choosing languageWeight 
     * @param lattice
     * @param model
     * @param lmWeight
     * 
     * @author Azhar Sabah Abdulaziz
     * @since
     */
    
   /* public LatticeRescorer(Lattice lattice, LanguageModel model, float lmWeight) {
        this.lattice = lattice;
        this.model = model;
        this.languageWeigth = lmWeight;
        depth = model.getMaxDepth();
    }
   */ 
    
    private void rescoreEdges() {
        for (Edge edge : lattice.getEdges()) {

            float maxProb = LogMath.LOG_ZERO;
            if (isFillerNode(edge.getToNode())) {
                edge.setLMScore(maxProb);
                continue;
            }

            List<String> paths = allPathsTo("", edge, depth);
            for (String path : paths) {
                List<Word> wordList = new LinkedList<Word>();
                for (String pathWord : path.split(" ")) {
                    wordList.add(new Word(pathWord, null, false));
                }
                wordList.add(edge.getToNode().getWord());
               
                WordSequence seq = new WordSequence(wordList);
                float prob = model.getProbability(seq) * languageWeigth;
                if (maxProb < prob)
                    maxProb = prob;
            }
            edge.setLMScore(maxProb);
        }
    }

    protected List<String> allPathsTo(String path, Edge edge, int currentDepth) {
        List<String> l = new LinkedList<String>();
        String p = path;
        boolean isFiller = isFillerNode(edge.getFromNode());
        if (!isFiller)
            p = edge.getFromNode().getWord().toString() + ' ' + p;

        if (currentDepth == 2
                || edge.getFromNode().equals(lattice.getInitialNode())) {
            l.add(p);
        } else {
            int decrement = isFiller ? 0 : 1;
            for (Edge e : edge.getFromNode().getEnteringEdges()) {
                l.addAll(allPathsTo(p, e, currentDepth - decrement));
            }
        }
        return l;
    }

    public void rescore() {
        rescoreEdges();
    }
    

/*    public void rescore() {
        rescoreEdges();
    }
*/    
    /**
     * This method is added by Azhar Abdulaziz 2018
     * It is used to get the lattice after it is scored. Note this method already calls {@link rescore()}, 
     * so, there is no need to call rescore() again.  
     * @return
     * 
     * @author Azhar Sabah Abdulaziz
     * @since 2018
     */
    public Lattice getRescoredLattice() {
    	rescoreEdges();
    	return this.lattice;
    }

    
    private boolean isFillerNode(Node node) {
        Word word = node.getWord();
        if (word.isSentenceStartWord() || word.isSentenceEndWord())
            return false;
        return word.isFiller();
    }

}

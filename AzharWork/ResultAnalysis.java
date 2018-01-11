package AzharWork;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.decoder.search.ActiveList;
import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.result.WordResult;

public class ResultAnalysis {

	/**
	 * 
	 * @param result
	 */
	public static void Word_analysis(SpeechResult result){
		// Words Analysis
		System.out.println("..........Word General Analysis..............");
    	for (WordResult r : result.getWords()) {
        	System.out.println("Pronunciation  for word '" + r.getWord() + "' is: " + r.getPronunciation());
        	System.out.println("Score for word '" + r.getWord() + "' is: " + r.getScore());
        	System.out.println("Confidence for word '" + r.getWord() + "' is: "+ r.getConfidence());
        	System.out.println("Time Frame for word '" + r.getWord() + "' is: " + r.getTimeFrame());
        	
        
        }
    	
	}
	
	
	/**
	 * 
	 * @param result
	 */
	public static void getWordScore(SpeechResult result){
		// Words Analysis
		double acc = 0;
		System.out.println("..........Word General Analysis..............");
    	for (WordResult r : result.getWords()) {
        	
        	System.out.println("Score for word '" + r.getWord() + "' is: " + r.getScore());
        	acc = acc+r.getScore();
        	
        
        }
    	System.out.println("Total Score = " + acc);
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public static double getTotalWordScore(SpeechResult result){
		// Total Words Score
		double acc=0;
		
    	for (WordResult r : result.getWords()) {
        	
        	//System.out.println("Score for word '" + r.getWord() + "' is: " + r.getScore());
        	acc = acc+r.getScore();
        	
        
        }
    	return acc;
	}
	
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	
	public static List<String> AllPossiblePaths(SpeechResult result){
		System.out.println("......... All Possible Paths ..........");
		List<String> allPaths = result.getLattice().allPaths();
		for(int i=0; i< allPaths.size(); i++){
			System.out.println("Path(" + i + "): " + allPaths.get(i));
			
		}
		
		return(result.getLattice().allPaths());

	}
	
	
	
	/**
	 * 
	 * @param lattice
	 * @return
	 */
	public static List<String> AllPossiblePaths(Lattice lattice){
		System.out.println("......... All Possible Paths ..........");
		List<String> allPaths = lattice.allPaths();
		for(int i=0; i< allPaths.size(); i++){
			System.out.println("Path(" + i + "): " + allPaths.get(i));
			
		}
		
		return(allPaths);

	}
	
	/**
	 * 
	 * @param lattice
	 */
	
	public static void Node_analysis(Lattice lattice){
		//Lattice Nodes
		System.out.println("............Lattice Nodes Analysis ......");
		System.out.println("--------------- Nodes Statistics -------------------------");
		Collection<Node> nodes = lattice.getNodes();
		System.out.println("No. of nodes for this lattice is: " + nodes.size());
    	Iterator<Node> node = nodes.iterator();
    	int ind=0;
    	while(node.hasNext()){
    		Node current_node = node.next();
    		
    		System.out.println("--- Node:" + current_node.getWord()+"---");
    		System.out.println("--- Node ID:" + current_node.getId()+ "---");
    		
    		System.out.println("Forward Score of Node(" + ind + ") = " + current_node.getForwardScore());
    		System.out.println("Backward Score of Node(" + ind + ") = "
					+ current_node.getBackwardScore());
    		double posteriori_pro = current_node.getPosterior();
    		System.out.println("Posteriori Probability of Node(" +ind + ") = " + posteriori_pro);
    		System.out.println(
					"Begin Time of Node(" + ind + ") = " + current_node.getBeginTime());
    		System.out.println(
					"End Time of Node(" + ind + ") = " + current_node.getEndTime());
    		
    		System.out.println(
					"Viterbi Score of Node(" + ind + ") = " + current_node.getViterbiScore());
    		
    		System.out.println("-----------------------------------------------------");
    		
    		
    		ind=ind+1;
    	}
       	System.out.println("^^^^^^^^^^^^^^ End Tokens Analysis ^^^^^^^^^^^^^^^^^");
	}
	
	/**
	 * 
	 * @param result
	 */
   public static void Node_analysis(SpeechResult result){
		//Lattice Nodes
		System.out.println("............Lattice Nodes Analysis ......");
		System.out.println("--------------- Nodes Statistics -------------------------");
		Collection<Node> nodes = result.getLattice().getNodes();
		System.out.println("No. of nodes for this lattice is: " + nodes.size());
    	Iterator<Node> node = nodes.iterator();
    	int ind=0;
    	while(node.hasNext()){
    		Node current_node = node.next();
    		
    		System.out.println("--- Node:" + current_node.getWord()+"---");
    		System.out.println("--- Node ID:" + current_node.getId()+ "---");
    		
    		System.out.println("Forward Score of Node(" + ind + ") = " + current_node.getForwardScore());
    		System.out.println("Backward Score of Node(" + ind + ") = "
					+ current_node.getBackwardScore());
    		double posteriori_pro = current_node.getPosterior();
    		System.out.println("Posteriori Probability of Node(" +ind + ") = " + posteriori_pro);
    		System.out.println(
					"Begin Time of Node(" + ind + ") = " + current_node.getBeginTime());
    		System.out.println(
					"End Time of Node(" + ind + ") = " + current_node.getEndTime());
    		
    		System.out.println(
					"Viterbi Score of Node(" + ind + ") = " + current_node.getViterbiScore());
    		
    		System.out.println("-----------------------------------------------------");
    		
    		
    		ind=ind+1;
    	}
       	System.out.println("^^^^^^^^^^^^^^ End Tokens Analysis ^^^^^^^^^^^^^^^^^");
	}
	
	public static void Token_analysis(SpeechResult result, boolean BestToken){
		 ActiveList Active_tokens = result.getResult().getActiveTokens();
     	//Token Best_token = Active_tokens.getBestToken();
     	if(BestToken){
     		Token tokens = Active_tokens.getBestToken();
     		System.out.println("Best Token associated with: " + tokens.getWord());
     		System.out.println("Best Token Score: " + tokens.getScore());
     		System.out.println("Best Token Acoustic Score: " + tokens.getAcousticScore());
     		System.out.println("Best Token LM Score: " + tokens.getLanguageScore());
     		System.out.println("Best Token Insertion Score: " + tokens.getInsertionScore());
     		System.out.println(
					"Best Token Predecessor word: " + tokens.getPredecessor().getPredecessor().getWord());
     		
     	}
     		else{
     			List<Token> tokens = Active_tokens.getTokens();
     			System.out.println(".........Tokens analysis.....");
     	     	System.out.println(
     						"The search for this utterance produces " + tokens.size() + " tokens");
     	     	for(int i=0; i< tokens.size(); i++){
     	     		System.out.println(
     							"Time for Token (" + i + ") is:" + tokens.get(i).getCollectTime());
     	     		System.out.println("Acoustic Score for Token (" + i + ") is:" + tokens.get(i).getAcousticScore());
     	     		System.out.println("Language Model score for Token (" + i + ") is:" + tokens.get(i).getLanguageScore());
     	     		System.out.println("Insertion score for Token (" + i + ") is:"
     							+ tokens.get(i).getInsertionScore());
     	     		System.out.println("Total score :for Token (" + i + ") is:" + tokens.get(i).getScore());
     	     		System.out.println("Is this token Emmiting? " + tokens.get(i).isEmitting());
     	     		System.out.println("Is this token final? " + tokens.get(i).isFinal());
     	     		System.out.println("This node is in STATE " + tokens.get(i).getSearchState()
     							+ "and associated with WORD " + tokens.get(i).getWord());
     	     	
     		
     	     	}
     	
     		
     		
     		System.out.println("---------------------------------------------------");
     	}
	}

}

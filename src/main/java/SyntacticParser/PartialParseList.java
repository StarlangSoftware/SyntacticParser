package SyntacticParser;

import ParseTree.ParseNode;
import ProbabilisticContextFreeGrammar.ProbabilisticParseNode;

import java.util.ArrayList;

public class PartialParseList {
    private final ArrayList<ParseNode> partialParses;

    /**
     * Constructor for the PartialParseList class. Initializes partial parses array list.
     */
    public PartialParseList(){
        partialParses = new ArrayList<>();
    }

    /**
     * Adds a new partial parse (actually a parse node representing the root of the subtree of the partial parse)
     * @param parseNode Root of the subtree showing the partial parse.
     */
    public void addPartialParse(ParseNode parseNode){
        partialParses.add(parseNode);
    }

    /**
     * Updates the partial parse by removing less probable nodes with the given parse node.
     * @param parseNode Parse node to be added to the partial parse.
     */
    public void updatePartialParse(ProbabilisticParseNode parseNode){
        boolean found = false;
        for (ParseNode partialParse: partialParses){
            if (partialParse.getData().getName().equals(parseNode.getData().getName())){
                if (((ProbabilisticParseNode) partialParse).getLogProbability() < parseNode.getLogProbability()){
                    partialParses.remove(partialParse);
                    partialParses.add(parseNode);
                }
                found = true;
                break;
            }
        }
        if (!found){
            partialParses.add(parseNode);
        }
    }

    /**
     * Accessor for the partialParses array list.
     * @param index Position of the parse node.
     * @return Parse node at the given position.
     */
    public ParseNode getPartialParse(int index){
        return partialParses.get(index);
    }

    /**
     * Returns size of the partial parse.
     * @return Size of the partial parse.
     */
    public int size(){
        return partialParses.size();
    }
}

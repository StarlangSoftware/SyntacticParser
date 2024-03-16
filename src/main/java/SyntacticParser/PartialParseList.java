package SyntacticParser;

import ParseTree.ParseNode;
import ProbabilisticContextFreeGrammar.ProbabilisticParseNode;

import java.util.ArrayList;

public class PartialParseList {
    private final ArrayList<ParseNode> partialParses;

    public PartialParseList(){
        partialParses = new ArrayList<>();
    }

    public void addPartialParse(ParseNode parseNode){
        partialParses.add(parseNode);
    }

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

    public ParseNode getPartialParse(int index){
        return partialParses.get(index);
    }

    public int size(){
        return partialParses.size();
    }
}

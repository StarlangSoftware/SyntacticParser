package ProbabilisticContextFreeGrammar;

import ParseTree.ParseNode;
import ParseTree.Symbol;

public class ProbabilisticParseNode extends ParseNode {

    private final double logProbability;

    public ProbabilisticParseNode(ParseNode left, ParseNode right, Symbol data, double logProbability){
        super(left, right, data);
        this.logProbability = logProbability;
    }

    public ProbabilisticParseNode(ParseNode left, Symbol data, double logProbability){
        super(left, data);
        this.logProbability = logProbability;
    }

    public ProbabilisticParseNode(Symbol data, double logProbability){
        super(data);
        this.logProbability = logProbability;
    }

    public double getLogProbability(){
        return logProbability;
    }

}

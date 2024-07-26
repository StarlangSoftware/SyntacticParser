package ProbabilisticContextFreeGrammar;

import ParseTree.ParseNode;
import ParseTree.Symbol;

public class ProbabilisticParseNode extends ParseNode {

    private final double logProbability;

    /**
     * Constructor for the ProbabilisticParseNode class. Extends the parse node with a probability.
     * @param left Left child of this node.
     * @param right Right child of this node.
     * @param data Data for this node.
     * @param logProbability Logarithm of the probability of the node.
     */
    public ProbabilisticParseNode(ParseNode left, ParseNode right, Symbol data, double logProbability){
        super(left, right, data);
        this.logProbability = logProbability;
    }

    /**
     * Another constructor for the ProbabilisticParseNode class.
     * @param left Left child of this node.
     * @param data Data for this node.
     * @param logProbability Logarithm of the probability of the node.
     */
    public ProbabilisticParseNode(ParseNode left, Symbol data, double logProbability){
        super(left, data);
        this.logProbability = logProbability;
    }

    /**
     * Another constructor for the ProbabilisticParseNode class.
     * @param data Data for this node.
     * @param logProbability Logarithm of the probability of the node.
     */
    public ProbabilisticParseNode(Symbol data, double logProbability){
        super(data);
        this.logProbability = logProbability;
    }

    /**
     * Accessor for the logProbability attribute.
     * @return logProbability attribute.
     */
    public double getLogProbability(){
        return logProbability;
    }

}

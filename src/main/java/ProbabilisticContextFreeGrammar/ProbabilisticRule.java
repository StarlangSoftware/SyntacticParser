package ProbabilisticContextFreeGrammar;

import ContextFreeGrammar.Rule;
import ContextFreeGrammar.RuleType;
import ParseTree.Symbol;
import java.util.ArrayList;

public class ProbabilisticRule extends Rule {

    private double probability;
    private int count = 0;

    public ProbabilisticRule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide, RuleType type, double probability){
        super(leftHandSide, rightHandSide, type);
        this.probability = probability;
    }

    public ProbabilisticRule(Symbol leftHandSide, ArrayList<Symbol> rightHandSideSymbol){
        super(leftHandSide, rightHandSideSymbol);
    }

    public ProbabilisticRule(String rule){
        int i;
        String prob = rule.substring(rule.indexOf('[') + 1, rule.indexOf(']'));
        String left = rule.substring(0, rule.indexOf("->")).trim();
        String right = rule.substring(rule.indexOf("->") + 2, rule.indexOf('[')).trim();
        leftHandSide = new Symbol(left);
        String[] rightSide = right.split(" ");
        rightHandSide = new ArrayList<Symbol>();
        for (i = 0; i < rightSide.length; i++){
            rightHandSide.add(new Symbol(rightSide[i]));
        }
        probability = Double.parseDouble(prob);
    }

    public double getProbability(){
        return probability;
    }

    public void increment(){
        count++;
    }

    public void normalizeProbability(int total){
        probability = count / (total + 0.0);
    }

    public int getCount(){
        return count;
    }

    public String toString(){
        return super.toString() + " [" + probability + "]";
    }

}

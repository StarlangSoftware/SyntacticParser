package ProbabilisticContextFreeGrammar;

import ContextFreeGrammar.Rule;
import ContextFreeGrammar.RuleType;
import ParseTree.Symbol;
import java.util.ArrayList;

public class ProbabilisticRule extends Rule {

    private double probability;
    private int count = 0;

    /**
     * Constructor for the probabilistic rule X -&gt;  beta. beta is a string of symbols from symbols (non-terminal)
     * @param leftHandSide Non-terminal symbol X.
     * @param rightHandSide beta. beta is a string of symbols from symbols (non-terminal)
     * @param type Type of the rule. TERMINAL if the rule is like X -&gt;  a, SINGLE_NON_TERMINAL if the rule is like X -&gt;  Y,
     *             TWO_NON_TERMINAL if the rule is like X -&gt;  YZ, MULTIPLE_NON_TERMINAL if the rule is like X -&gt;  YZT..
     * @param probability Probability of the rule
     */
    public ProbabilisticRule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide, RuleType type, double probability){
        super(leftHandSide, rightHandSide, type);
        this.probability = probability;
    }

    /**
     * Constructor for the rule X -&gt;  beta. beta is a string of symbols from symbols (non-terminal)
     * @param leftHandSide Non-terminal symbol X.
     * @param rightHandSide beta. beta is a string of symbols from symbols (non-terminal)
     */
    public ProbabilisticRule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide){
        super(leftHandSide, rightHandSide);
    }

    /**
     * Constructor for any probabilistic rule from a string. The string is of the form X -&gt;  .... [probability] The
     * method constructs left hand side symbol and right hand side symbol(s) from the input string.
     * @param rule String containing the rule. The string is of the form X -&gt;  .... [probability]
     */
    public ProbabilisticRule(String rule){
        int i;
        String prob = rule.substring(rule.indexOf('[') + 1, rule.indexOf(']'));
        String left = rule.substring(0, rule.indexOf("->")).trim();
        String right = rule.substring(rule.indexOf("->") + 2, rule.indexOf('[')).trim();
        leftHandSide = new Symbol(left);
        String[] rightSide = right.split(" ");
        rightHandSide = new ArrayList<>();
        for (i = 0; i < rightSide.length; i++){
            rightHandSide.add(new Symbol(rightSide[i]));
        }
        probability = Double.parseDouble(prob);
    }

    /**
     * Accessor for the probability attribute.
     * @return Probability attribute.
     */
    public double getProbability(){
        return probability;
    }

    /**
     * Increments the count attribute.
     */
    public void increment(){
        count++;
    }

    /**
     * Calculates the probability from count and the given total value.
     * @param total Value used for calculating the probability.
     */
    public void normalizeProbability(int total){
        probability = count / (total + 0.0);
    }

    /**
     * Accessor for the count attribute
     * @return Count attribute
     */
    public int getCount(){
        return count;
    }

    /**
     * Converts the rule to the form X -&gt;  ... [probability]
     * @return String form of the rule in the form of X -&gt;  ... [probability]
     */
    public String toString(){
        return super.toString() + " [" + probability + "]";
    }

}

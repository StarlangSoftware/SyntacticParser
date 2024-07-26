package ContextFreeGrammar;

import ParseTree.Symbol;

import java.util.ArrayList;

public class Rule {
    protected Symbol leftHandSide;
    protected ArrayList<Symbol> rightHandSide;
    protected RuleType type;

    /**
     * Empty constructor for the rule class.
     */
    public Rule(){
    }

    /**
     * Constructor for the rule X -> Y.
     * @param leftHandSide Non-terminal symbol X
     * @param rightHandSideSymbol Symbol Y (terminal or non-terminal)
     */
    public Rule(Symbol leftHandSide, Symbol rightHandSideSymbol){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = new ArrayList<>();
        this.rightHandSide.add(rightHandSideSymbol);
    }

    /**
     * Constructor for the rule X -> YZ.
     * @param leftHandSide Non-terminal symbol X.
     * @param rightHandSideSymbol1 Symbol Y (non-terminal).
     * @param rightHandSideSymbol2 Symbol Z (non-terminal).
     */
    public Rule(Symbol leftHandSide, Symbol rightHandSideSymbol1, Symbol rightHandSideSymbol2){
        this(leftHandSide, rightHandSideSymbol1);
        this.rightHandSide.add(rightHandSideSymbol2);
    }

    /**
     * Constructor for the rule X -> beta. beta is a string of symbols from symbols (non-terminal)
     * @param leftHandSide Non-terminal symbol X.
     * @param rightHandSide beta. beta is a string of symbols from symbols (non-terminal)
     */
    public Rule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    /**
     * Constructor for the rule X -> beta. beta is a string of symbols from symbols (non-terminal)
     * @param leftHandSide Non-terminal symbol X.
     * @param rightHandSide beta. beta is a string of symbols from symbols (non-terminal)
     * @param type Type of the rule. TERMINAL if the rule is like X -> a, SINGLE_NON_TERMINAL if the rule is like X -> Y,
     *             TWO_NON_TERMINAL if the rule is like X -> YZ, MULTIPLE_NON_TERMINAL if the rule is like X -> YZT..
     */
    public Rule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide, RuleType type){
        this(leftHandSide, rightHandSide);
        this.type = type;
    }

    /**
     * Constructor for any rule from a string. The string is of the form X -> .... The method constructs left hand
     * side symbol and right hand side symbol(s) from the input string.
     * @param rule String containing the rule. The string is of the form X -> ....
     */
    public Rule(String rule){
        int i;
        String left = rule.substring(0, rule.indexOf("->")).trim();
        String right = rule.substring(rule.indexOf("->") + 2).trim();
        leftHandSide = new Symbol(left);
        String[] rightSide = right.split(" ");
        rightHandSide = new ArrayList<>();
        for (i = 0; i < rightSide.length; i++){
            rightHandSide.add(new Symbol(rightSide[i]));
        }
    }

    @Override public boolean equals(Object aThat) {
        if (this == aThat)
            return true;
        if (!(aThat instanceof Rule))
            return false;
        Rule rule = (Rule)aThat;
        return toString().equals(rule.toString());
    }

    /**
     * Checks if the rule is left recursive or not. A rule is left recursive if it is of the form X -> X..., so its
     * first symbol of the right side is the symbol on the left side.
     * @return True, if the rule is left recursive; false otherwise.
     */
    public boolean leftRecursive(){
        return rightHandSide.get(0).equals(leftHandSide) && type == RuleType.SINGLE_NON_TERMINAL;
    }

    /**
     * In conversion to Chomsky Normal Form, rules like A -> BC... are replaced with A -> X1... and X1 -> BC. This
     * method replaces B and C non-terminals on the right hand side with X1.
     * @param first Non-terminal symbol B.
     * @param second Non-terminal symbol C.
     * @param with Non-terminal symbol X1.
     * @return True, if any replacements has been made; false otherwise.
     */
    protected boolean updateMultipleNonTerminal(Symbol first, Symbol second, Symbol with){
        int i;
        for (i = 0; i < rightHandSide.size() - 1; i++){
            if (rightHandSide.get(i).equals(first) && rightHandSide.get(i + 1).equals(second)){
                rightHandSide.remove(i + 1);
                rightHandSide.remove(i);
                rightHandSide.add(i, with);
                if (rightHandSide.size() == 2){
                    type = RuleType.TWO_NON_TERMINAL;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Accessor for the rule type.
     * @return Rule type.
     */
    public RuleType getType(){
        return type;
    }

    /**
     * Accessor for the left hand side.
     * @return Left hand side.
     */
    public Symbol getLeftHandSide(){
        return leftHandSide;
    }

    /**
     * Accessor for the right hand side.
     * @return Right hand side.
     */
    public ArrayList<Symbol> getRightHandSide(){
        return rightHandSide;
    }

    /**
     * Returns number of symbols on the right hand side.
     * @return Number of symbols on the right hand side.
     */
    public int getRightHandSideSize(){
        return rightHandSide.size();
    }

    /**
     * Returns symbol at position index on the right hand side.
     * @param index Position of the symbol
     * @return Symbol at position index on the right hand side.
     */
    public Symbol getRightHandSideAt(int index){
        return rightHandSide.get(index);
    }

    /**
     * Converts the rule to the form X -> ...
     * @return String form of the rule in the form of X -> ...
     */
    public String toString(){
        StringBuilder result = new StringBuilder(leftHandSide + " -> ");
        for (Symbol symbol: rightHandSide){
            result.append(" ").append(symbol);
        }
        return result.toString();
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }

}

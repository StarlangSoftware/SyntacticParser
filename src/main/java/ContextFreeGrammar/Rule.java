package ContextFreeGrammar;

import ParseTree.Symbol;

import java.util.ArrayList;

public class Rule {
    protected Symbol leftHandSide;
    protected ArrayList<Symbol> rightHandSide;
    protected RuleType type;

    public Rule(){

    }

    public Rule(Symbol leftHandSide, Symbol rightHandSideSymbol){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = new ArrayList<>();
        this.rightHandSide.add(rightHandSideSymbol);
    }

    public Rule(Symbol leftHandSide, Symbol rightHandSideSymbol1, Symbol rightHandSideSymbol2){
        this(leftHandSide, rightHandSideSymbol1);
        this.rightHandSide.add(rightHandSideSymbol2);
    }

    public Rule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    public Rule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide, RuleType type){
        this(leftHandSide, rightHandSide);
        this.type = type;
    }

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

    public boolean leftRecursive(){
        return rightHandSide.get(0).equals(leftHandSide) && type == RuleType.SINGLE_NON_TERMINAL;
    }

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

    public RuleType getType(){
        return type;
    }

    public Symbol getLeftHandSide(){
        return leftHandSide;
    }

    public ArrayList<Symbol> getRightHandSide(){
        return rightHandSide;
    }

    public int getRightHandSideSize(){
        return rightHandSide.size();
    }

    public Symbol getRightHandSideAt(int index){
        return rightHandSide.get(index);
    }

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

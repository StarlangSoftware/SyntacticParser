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
        this.rightHandSide = new ArrayList<Symbol>();
        this.rightHandSide.add(rightHandSideSymbol);
        updateType();
    }

    public Rule(Symbol leftHandSide, Symbol rightHandSideSymbol1, Symbol rightHandSideSymbol2){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = new ArrayList<Symbol>();
        this.rightHandSide.add(rightHandSideSymbol1);
        this.rightHandSide.add(rightHandSideSymbol2);
        updateType();
    }

    public Rule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        updateType();
    }

    public Rule(Symbol leftHandSide, ArrayList<Symbol> rightHandSide, RuleType type){
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.type = type;
    }

    public Rule(String rule){
        int i;
        String left = rule.substring(0, rule.indexOf("->")).trim();
        String right = rule.substring(rule.indexOf("->") + 2).trim();
        leftHandSide = new Symbol(left);
        String[] rightSide = right.split(" ");
        rightHandSide = new ArrayList<Symbol>();
        for (i = 0; i < rightSide.length; i++){
            rightHandSide.add(new Symbol(rightSide[i]));
        }
        updateType();
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
        return rightHandSide.get(0).equals(leftHandSide) && rightHandSide.size() == 1;
    }

    protected void updateType(){
        if (rightHandSide.size() > 2){
            type = RuleType.MULTIPLE_NON_TERMINAL;
        } else {
            if (rightHandSide.size() == 2){
                type = RuleType.TWO_NON_TERMINAL;
            } else {
                if (rightHandSide.get(0).isTerminal()){
                    type = RuleType.TERMINAL;
                } else {
                    type = RuleType.SINGLE_NON_TERMINAL;
                }
            }
        }
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
        String result = leftHandSide + " -> ";
        for (Symbol symbol: rightHandSide){
            result = result + " " + symbol;
        }
        return result;
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }

}

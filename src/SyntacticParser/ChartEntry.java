package SyntacticParser;

import ContextFreeGrammar.Rule;
import ParseTree.Symbol;
import ParseTree.ParseNode;
import ProbabilisticContextFreeGrammar.ProbabilisticRule;

import java.util.ArrayList;

public class ChartEntry{

    private Rule rule;
    private int from;
    private int to;
    private int dotPlace;
    private double probability = 1.0;
    private ArrayList<ChartEntry> states;

    public ChartEntry(Rule rule, int from, int to, int dotPlace){
        this.rule = rule;
        this.from = from;
        this.to = to;
        this.dotPlace = dotPlace;
        states = new ArrayList<ChartEntry>();
    }

    public ChartEntry(ProbabilisticRule rule, int from, int to, int dotPlace){
        this((Rule) rule, from, to, dotPlace);
        this.probability = rule.getProbability();
    }

    public ChartEntry(Rule rule, int from, int to, int dotPlace, ChartEntry current, ChartEntry added){
        this.rule = rule;
        this.from = from;
        this.to = to;
        this.dotPlace = dotPlace;
        states = new ArrayList<ChartEntry>();
        for (ChartEntry state: current.states){
            states.add(state);
        }
        states.add(added);
    }

    public ChartEntry(ProbabilisticRule rule, int from, int to, int dotPlace, ChartEntry current, ChartEntry added){
        this((Rule) rule, from, to, dotPlace, current, added);
        this.probability = current.probability * added.probability;
    }

    public ArrayList<ChartEntry> getStates(){
        return states;
    }

    public double getProbability(){
        return probability;
    }

    public ParseNode constructParseNode(){
        ParseNode node;
        if (states.size() == 0){
            node = new ParseNode(new ParseNode(rule.getRightHandSideAt(0)), rule.getLeftHandSide());
        } else {
            node = new ParseNode(rule.getLeftHandSide());
            for (ChartEntry state:states){
                node.addChild(state.constructParseNode());
            }
        }
        return node;
    }

    public Rule getRule(){
        return rule;
    }

    public int from(){
        return from;
    }

    public int to(){
        return to;
    }

    public int dotPlace(){
        return dotPlace;
    }

    public boolean isComplete(){
        return dotPlace == rule.getRightHandSideSize();
    }

    public Symbol getNextCategory(){
        return rule.getRightHandSideAt(dotPlace);
    }

    public boolean nextCategoryPartOfSpeech(ArrayList<Symbol> posTags){
        for (Symbol posTag:posTags){
            if (getNextCategory().equals(posTag)){
                return true;
            }
        }
        return false;
    }

    public String toString(){
        int i = 0;
        String result = rule.getLeftHandSide() + " -> ";
        for (Symbol symbol: rule.getRightHandSide()){
            if (dotPlace == i){
                result = result + "." + symbol;
            } else {
                result = result + " " + symbol;
            }
            i++;
        }
        if (dotPlace == i){
            result = result + ".";
        }
        result = result + " [" + from + "," + to() + "]";
        return result;
    }

    public String toStringExtended(){
        String result = toString();
        for (ChartEntry state: states){
            result = result + state.toStringExtended();
        }
        return result;
    }

}

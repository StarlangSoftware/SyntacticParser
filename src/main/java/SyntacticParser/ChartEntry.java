package SyntacticParser;

import ContextFreeGrammar.Rule;
import ParseTree.Symbol;
import ParseTree.ParseNode;
import ProbabilisticContextFreeGrammar.ProbabilisticRule;

import java.util.ArrayList;

public class ChartEntry{

    private final Rule rule;
    private final int from;
    private final int to;
    private final int dotPlace;
    private double probability = 1.0;
    private final ArrayList<ChartEntry> states;

    public ChartEntry(Rule rule, int from, int to, int dotPlace){
        this.rule = rule;
        this.from = from;
        this.to = to;
        this.dotPlace = dotPlace;
        states = new ArrayList<>();
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
        states = new ArrayList<>();
        states.addAll(current.states);
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
        if (states.isEmpty()){
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
        StringBuilder result = new StringBuilder(rule.getLeftHandSide() + " -> ");
        for (Symbol symbol: rule.getRightHandSide()){
            if (dotPlace == i){
                result.append(".").append(symbol);
            } else {
                result.append(" ").append(symbol);
            }
            i++;
        }
        if (dotPlace == i){
            result.append(".");
        }
        result.append(" [").append(from).append(",").append(to()).append("]");
        return result.toString();
    }

    public String toStringExtended(){
        StringBuilder result = new StringBuilder(toString());
        for (ChartEntry state: states){
            result.append(state.toStringExtended());
        }
        return result.toString();
    }

}

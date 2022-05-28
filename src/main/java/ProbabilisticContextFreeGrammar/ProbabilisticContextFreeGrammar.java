package ProbabilisticContextFreeGrammar;

import ContextFreeGrammar.*;
import ParseTree.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProbabilisticContextFreeGrammar extends ContextFreeGrammar {

    public ProbabilisticContextFreeGrammar(){
    }

    public ProbabilisticContextFreeGrammar(String ruleFileName, String dictionaryFileName, int minCount){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ruleFileName), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                ProbabilisticRule newRule = new ProbabilisticRule(line);
                rules.add(newRule);
                rulesRightSorted.add(newRule);
                line = br.readLine();
            }
            Comparator<Rule> comparator = new RuleComparator();
            rules.sort(comparator);
            Comparator<Rule> rightComparator = new RuleRightSideComparator();
            rulesRightSorted.sort(rightComparator);
        } catch (IOException e) {
            e.printStackTrace();
        }
        readDictionary(dictionaryFileName);
        updateTypes();
        this.minCount = minCount;
    }

    public ProbabilisticContextFreeGrammar(TreeBank treeBank, int minCount){
        ArrayList<Symbol> variables;
        ArrayList<Rule> candidates;
        int total;
        constructDictionary(treeBank);
        for (int i = 0; i < treeBank.size(); i++){
            ParseTree parseTree = treeBank.get(i);
            updateTree(parseTree, minCount);
            addRules(parseTree.getRoot());
        }
        variables = getLeftSide();
        for (Symbol variable: variables){
            candidates = getRulesWithLeftSideX(variable);
            total = 0;
            for (Rule candidate: candidates){
                total += ((ProbabilisticRule) candidate).getCount();
            }
            for (Rule candidate: candidates){
                ((ProbabilisticRule) candidate).normalizeProbability(total);
            }
        }
        updateTypes();
        this.minCount = minCount;
    }

    public static ProbabilisticRule toRule(ParseNode parseNode, boolean trim){
        Symbol left;
        ArrayList<Symbol> right = new ArrayList<>();
        if (trim)
            left = parseNode.getData().trimSymbol();
        else
            left = parseNode.getData();
        for (int i = 0; i < parseNode.numberOfChildren(); i++) {
            ParseNode childNode = parseNode.getChild(i);
            if (childNode.getData() != null){
                if (childNode.getData().isTerminal()){
                    right.add(childNode.getData());
                } else {
                    right.add(childNode.getData().trimSymbol());
                }
            } else {
                return null;
            }
        }
        return new ProbabilisticRule(left, right);
    }

    private void addRules(ParseNode parseNode){
        Rule existedRule;
        ProbabilisticRule newRule;
        newRule = toRule(parseNode, true);
        if (newRule != null){
            existedRule = searchRule(newRule);
            if (existedRule == null){
                addRule(newRule);
                newRule.increment();
            } else {
                ((ProbabilisticRule) existedRule).increment();
            }
        } else {
            System.out.println(this);
        }
        for (int i = 0; i < parseNode.numberOfChildren(); i++) {
            ParseNode childNode = parseNode.getChild(i);
            if (childNode.numberOfChildren() > 0){
                addRules(childNode);
            }
        }
    }

    private double probability(ParseNode parseNode){
        Rule existedRule;
        ProbabilisticRule rule;
        double sum = 0.0;
        if (parseNode.numberOfChildren() > 0){
            rule = toRule(parseNode, true);
            existedRule = searchRule(rule);
            sum = Math.log(((ProbabilisticRule)existedRule).getProbability());
            if (existedRule.getType() != RuleType.TERMINAL){
                for (int i = 0; i < parseNode.numberOfChildren(); i++){
                    ParseNode childNode = parseNode.getChild(i);
                    sum += probability(childNode);
                }
            }
        }
        return sum;
    }

    public double probability(ParseTree parseTree){
        return probability(parseTree.getRoot());
    }


    private void removeSingleNonTerminalFromRightHandSide(){
        ArrayList<Symbol> nonTerminalList;
        Symbol removeCandidate;
        ArrayList<Rule> ruleList;
        ArrayList<Rule> candidateList;
        nonTerminalList = new ArrayList<>();
        removeCandidate = getSingleNonTerminalCandidateToRemove(nonTerminalList);
        while (removeCandidate != null){
            ruleList = getRulesWithRightSideX(removeCandidate);
            for (Rule rule: ruleList){
                candidateList = getRulesWithLeftSideX(removeCandidate);
                for (Rule candidate: candidateList){
                    addRule(new ProbabilisticRule(rule.getLeftHandSide(), (ArrayList<Symbol>) candidate.getRightHandSide().clone(), candidate.getType(), ((ProbabilisticRule) rule).getProbability() * ((ProbabilisticRule) candidate).getProbability()));
                }
                removeRule(rule);
            }
            nonTerminalList.add(removeCandidate);
            removeCandidate = getSingleNonTerminalCandidateToRemove(nonTerminalList);
        }
    }

    private void updateMultipleNonTerminalFromRightHandSide(){
        Rule updateCandidate;
        int newVariableCount = 0;
        updateCandidate = getMultipleNonTerminalCandidateToUpdate();
        while (updateCandidate != null){
            ArrayList<Symbol> newRightHandSide = new ArrayList<>();
            Symbol newSymbol = new Symbol("X" + newVariableCount);
            newRightHandSide.add(updateCandidate.getRightHandSide().get(0));
            newRightHandSide.add(updateCandidate.getRightHandSide().get(1));
            updateAllMultipleNonTerminalWithNewRule(updateCandidate.getRightHandSide().get(0), updateCandidate.getRightHandSide().get(1), newSymbol);
            addRule(new ProbabilisticRule(newSymbol, newRightHandSide, RuleType.TWO_NON_TERMINAL, 1.0));
            newVariableCount++;
            updateCandidate = getMultipleNonTerminalCandidateToUpdate();
        }
    }

    public void convertToChomskyNormalForm(){
        removeSingleNonTerminalFromRightHandSide();
        updateMultipleNonTerminalFromRightHandSide();
        Comparator<Rule> comparator = new RuleComparator();
        rules.sort(comparator);
        Comparator<Rule> rightComparator = new RuleRightSideComparator();
        rulesRightSorted.sort(rightComparator);
    }

}

package ContextFreeGrammar;

import ParseTree.*;

import java.io.*;
import java.util.*;

public class ContextFreeGrammar {

    protected ArrayList<Rule> rules;
    protected ArrayList<Rule> rulesRightSorted;

    public ContextFreeGrammar(){
        rules = new ArrayList<>();
        rulesRightSorted = new ArrayList<>();
    }

    public ContextFreeGrammar(String fileName){
        rules = new ArrayList<>();
        rulesRightSorted = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
            String line = br.readLine();
            while (line != null){
                rules.add(new Rule(line));
                rulesRightSorted.add(new Rule(line));
                line = br.readLine();
            }
            Comparator<Rule> comparator = new RuleComparator();
            Collections.sort(rules, comparator);
            Comparator<Rule> rightComparator = new RuleRightSideComparator();
            Collections.sort(rulesRightSorted, rightComparator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Rule toRule(ParseNode parseNode, boolean trim){
        Symbol left;
        ArrayList<Symbol> right = new ArrayList<>();
        if (trim)
            left = parseNode.getData().trimSymbol();
        else
            left = parseNode.getData();
        for (int i = 0; i < parseNode.numberOfChildren(); i++) {
            ParseNode childNode = parseNode.getChild(i);
            if (childNode.getData() != null){
                if (childNode.getData().isTerminal() || !trim){
                    right.add(childNode.getData());
                } else {
                    right.add(childNode.getData().trimSymbol());
                }
            } else {
                return null;
            }
        }
        return new Rule(left, right);
    }

    private void addRules(ParseNode parseNode){
        Rule newRule;
        newRule = toRule(parseNode, true);
        if (newRule != null){
           addRule(newRule);
        } else {
            System.out.println(toString());
        }
        for (int i = 0; i < parseNode.numberOfChildren(); i++) {
            ParseNode childNode = parseNode.getChild(i);
            if (childNode.numberOfChildren() > 0){
                addRules(childNode);
            }
        }
    }

    public ContextFreeGrammar(TreeBank treeBank){
        for (int i = 0; i < treeBank.size(); i++){
            ParseTree parseTree = treeBank.get(i);
            addRules(parseTree.getRoot());
        }
    }

    public void writeToFile(String fileName){
        try {
            FileWriter fw = new FileWriter(new File(fileName));
            for (Rule rule:rules){
                fw.write(rule.toString() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRule(Rule newRule){
        int pos;
        Comparator<Rule> comparator = new RuleComparator();
        pos = Collections.binarySearch(rules, newRule, comparator);
        if (pos < 0){
            rules.add(-pos - 1, newRule);
            Comparator<Rule> rightComparator = new RuleRightSideComparator();
            pos = Collections.binarySearch(rulesRightSorted, newRule, rightComparator);
            if (pos >= 0){
                rulesRightSorted.add(pos, newRule);
            } else {
                rulesRightSorted.add(-pos - 1, newRule);
            }
        }
    }

    public void removeRule(Rule rule){
        int pos, posUp, posDown;
        Comparator<Rule> comparator = new RuleComparator();
        pos = Collections.binarySearch(rules, rule, comparator);
        if (pos >= 0){
            rules.remove(pos);
            Comparator<Rule> rightComparator = new RuleRightSideComparator();
            pos = Collections.binarySearch(rulesRightSorted, rule, rightComparator);
            posUp = pos;
            while (posUp >= 0 && rightComparator.compare(rulesRightSorted.get(posUp), rule) == 0){
                if (comparator.compare(rule, rulesRightSorted.get(posUp)) == 0){
                    rulesRightSorted.remove(posUp);
                    return;
                }
                posUp--;
            }
            posDown = pos + 1;
            while (posDown < rulesRightSorted.size() && rightComparator.compare(rulesRightSorted.get(posDown), rule) == 0){
                if (comparator.compare(rule, rulesRightSorted.get(posDown)) == 0){
                    rulesRightSorted.remove(posDown);
                    return;
                }
                posDown++;
            }
        }
    }

    /*Return rules such as X -> ... */
    public ArrayList<Rule> getRulesWithLeftSideX(Symbol X){
        int middle, middleUp, middleDown;
        ArrayList<Rule> result = new ArrayList<>();
        Rule dummyRule = new Rule(X, X);
        Comparator<Rule> leftComparator = new RuleLeftSideComparator();
        middle = Collections.binarySearch(rules, dummyRule, leftComparator);
        if (middle >= 0){
            middleUp = middle;
            while (middleUp >= 0 && rules.get(middleUp).getLeftHandSide().equals(X)){
                result.add(rules.get(middleUp));
                middleUp--;
            }
            middleDown = middle + 1;
            while (middleDown < rules.size() && rules.get(middleDown).getLeftHandSide().equals(X)){
                result.add(rules.get(middleDown));
                middleDown++;
            }
        }
        return result;
    }

    /*Return symbols X from terminal rules such as X -> a */
    public ArrayList<Symbol> partOfSpeechTags(){
        ArrayList<Symbol> result = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.getType() == RuleType.TERMINAL && !result.contains(rule.getLeftHandSide())) {
                result.add(rule.getLeftHandSide());
            }
        }
        return result;
    }

    /*Return symbols X from all rules such as X -> ... */
    public ArrayList<Symbol> getLeftSide(){
        ArrayList<Symbol> result = new ArrayList<>();
        for (Rule rule : rules) {
            if (!result.contains(rule.getLeftHandSide())) {
                result.add(rule.getLeftHandSide());
            }
        }
        return result;
    }

    /*Return terminal rules such as X -> s*/
    public ArrayList<Rule> getTerminalRulesWithRightSideX(Symbol s){
        int middle, middleUp, middleDown;
        ArrayList<Rule> result = new ArrayList<>();
        Rule dummyRule = new Rule(s, s);
        Comparator<Rule> rightComparator = new RuleRightSideComparator();
        middle = Collections.binarySearch(rulesRightSorted, dummyRule, rightComparator);
        if (middle >= 0){
            middleUp = middle;
            while (middleUp >= 0 && rulesRightSorted.get(middleUp).getRightHandSideAt(0).equals(s)){
                if (rulesRightSorted.get(middleUp).getType() == RuleType.TERMINAL){
                    result.add(rulesRightSorted.get(middleUp));
                }
                middleUp--;
            }
            middleDown = middle + 1;
            while (middleDown < rulesRightSorted.size() && rulesRightSorted.get(middleDown).getRightHandSideAt(0).equals(s)){
                if (rulesRightSorted.get(middleDown).getType() == RuleType.TERMINAL){
                    result.add(rulesRightSorted.get(middleDown));
                }
                middleDown++;
            }
        }
        return result;
    }

    /*Return terminal rules such as X -> S*/
    public ArrayList<Rule> getRulesWithRightSideX(Symbol S){
        int pos, posUp, posDown;
        ArrayList<Rule> result = new ArrayList<Rule>();
        Rule dummyRule = new Rule(S, S);
        Comparator<Rule> rightComparator = new RuleRightSideComparator();
        pos = Collections.binarySearch(rulesRightSorted, dummyRule, rightComparator);
        if (pos >= 0){
            posUp = pos;
            while (posUp >= 0 && rulesRightSorted.get(posUp).getRightHandSideAt(0).equals(S) && rulesRightSorted.get(posUp).getRightHandSideSize() == 1){
                result.add(rulesRightSorted.get(posUp));
                posUp--;
            }
            posDown = pos + 1;
            while (posDown < rulesRightSorted.size() && rulesRightSorted.get(posDown).getRightHandSideAt(0).equals(S) && rulesRightSorted.get(posDown).getRightHandSideSize() == 1){
                result.add(rulesRightSorted.get(posDown));
                posDown++;
            }
        }
        return result;
    }

    /*Return rules such as X -> AB */
    public ArrayList<Rule> getRulesWithTwoNonTerminalsOnRightSide(Symbol A, Symbol B){
        int pos, posUp, posDown;
        ArrayList<Rule> result = new ArrayList<Rule>();
        Rule dummyRule = new Rule(A, A, B);
        Comparator<Rule> rightComparator = new RuleRightSideComparator();
        pos = Collections.binarySearch(rulesRightSorted, dummyRule, rightComparator);
        if (pos >= 0){
            posUp = pos;
            while (posUp >= 0 && rulesRightSorted.get(posUp).getRightHandSideSize() == 2 && rulesRightSorted.get(posUp).getRightHandSideAt(0).equals(A) && rulesRightSorted.get(posUp).getRightHandSideAt(1).equals(B)){
                result.add(rulesRightSorted.get(posUp));
                posUp--;
            }
            posDown = pos + 1;
            while (posDown < rulesRightSorted.size() && rulesRightSorted.get(posDown).getRightHandSideSize() == 2 && rulesRightSorted.get(posDown).getRightHandSideAt(0).equals(A) && rulesRightSorted.get(posDown).getRightHandSideAt(1).equals(B)){
                result.add(rulesRightSorted.get(posDown));
                posDown++;
            }

        }
        return result;
    }

    /*Return Y of the first rule such as X -> Y */
    protected Symbol getSingleNonTerminalCandidateToRemove(ArrayList<Symbol> removedList){
        Symbol removeCandidate = null;
        for (Rule rule:rules) {
            if (rule.type == RuleType.SINGLE_NON_TERMINAL && !rule.leftRecursive() && !removedList.contains(rule.getRightHandSideAt(0))) {
                removeCandidate = rule.getRightHandSideAt(0);
                break;
            }
        }
        return removeCandidate;
    }

    /*Return the first rule such as X -> ABC... */
    protected Rule getMultipleNonTerminalCandidateToUpdate(){
        Rule removeCandidate = null;
        for (Rule rule:rules) {
            if (rule.type == RuleType.MULTIPLE_NON_TERMINAL) {
                removeCandidate = rule;
                break;
            }
        }
        return removeCandidate;
    }

    private void removeSingleNonTerminalFromRightHandSide(){
        ArrayList<Symbol> nonTerminalList;
        Symbol removeCandidate;
        ArrayList<Rule> ruleList;
        ArrayList<Rule> candidateList;
        nonTerminalList = new ArrayList<Symbol>();
        removeCandidate = getSingleNonTerminalCandidateToRemove(nonTerminalList);
        while (removeCandidate != null){
            ruleList = getRulesWithRightSideX(removeCandidate);
            for (Rule rule: ruleList){
                candidateList = getRulesWithLeftSideX(removeCandidate);
                for (Rule candidate: candidateList){
                    addRule(new Rule(rule.leftHandSide, (ArrayList<Symbol>) candidate.getRightHandSide().clone(), candidate.getType()));
                }
                removeRule(rule);
            }
            nonTerminalList.add(removeCandidate);
            removeCandidate = getSingleNonTerminalCandidateToRemove(nonTerminalList);
            System.out.println(rules.size());
        }
    }

    protected void updateAllMultipleNonTerminalWithNewRule(Symbol first, Symbol second, Symbol with){
        for (Rule rule : rules) {
            if (rule.type == RuleType.MULTIPLE_NON_TERMINAL){
                rule.updateMultipleNonTerminal(first, second, with);
            }
        }
        for (Rule rule : rulesRightSorted) {
            if (rule.type == RuleType.MULTIPLE_NON_TERMINAL) {
                rule.updateMultipleNonTerminal(first, second, with);
            }
        }
    }

    private void updateMultipleNonTerminalFromRightHandSide(){
        Rule updateCandidate;
        int newVariableCount = 0;
        updateCandidate = getMultipleNonTerminalCandidateToUpdate();
        while (updateCandidate != null){
            ArrayList<Symbol> newRightHandSide = new ArrayList<Symbol>();
            Symbol newSymbol = new Symbol("X" + newVariableCount);
            newRightHandSide.add(updateCandidate.getRightHandSide().get(0));
            newRightHandSide.add(updateCandidate.getRightHandSide().get(1));
            updateAllMultipleNonTerminalWithNewRule(updateCandidate.getRightHandSide().get(0), updateCandidate.getRightHandSide().get(1), newSymbol);
            addRule(new Rule(newSymbol, newRightHandSide, RuleType.TWO_NON_TERMINAL));
            updateCandidate = getMultipleNonTerminalCandidateToUpdate();
            newVariableCount++;
            System.out.println(rules.size());
        }
    }

    public void convertToChomskyNormalForm(){
        removeSingleNonTerminalFromRightHandSide();
        updateMultipleNonTerminalFromRightHandSide();
        Comparator<Rule> comparator = new RuleComparator();
        Collections.sort(rules, comparator);
        Comparator<Rule> rightComparator = new RuleRightSideComparator();
        Collections.sort(rulesRightSorted, rightComparator);
    }

    public Rule searchRule(Rule rule){
        int pos;
        Comparator<Rule> comparator = new RuleComparator();
        pos = Collections.binarySearch(rules, rule, comparator);
        if (pos >= 0){
            return rules.get(pos);
        } else {
            return null;
        }
    }

    public int size(){
        return rules.size();
    }
}

package ProbabilisticContextFreeGrammar;

import ContextFreeGrammar.*;
import ParseTree.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ProbabilisticContextFreeGrammar extends ContextFreeGrammar {

    /**
     * Empty constructor for the ContextFreeGrammar class.
     */
    public ProbabilisticContextFreeGrammar(){
    }

    /**
     * Constructor for the ProbabilisticContextFreeGrammar class. Reads the rules from the rule file, lexicon rules from
     * the dictionary file and sets the minimum frequency parameter.
     * @param ruleFileName File name for the rule file.
     * @param dictionaryFileName File name for the lexicon file.
     * @param minCount Minimum frequency parameter.
     */
    public ProbabilisticContextFreeGrammar(String ruleFileName, String dictionaryFileName, int minCount){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(ruleFileName)), StandardCharsets.UTF_8));
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
        } catch (IOException ignored) {
        }
        readDictionary(dictionaryFileName);
        updateTypes();
        this.minCount = minCount;
    }

    /**
     * Another constructor for the ProbabilisticContextFreeGrammar class. Constructs the lexicon from the leaf nodes of
     * the trees in the given treebank. Extracts rules from the non-leaf nodes of the trees in the given treebank. Also
     * sets the minimum frequency parameter.
     * @param treeBank Treebank containing the constituency trees.
     * @param minCount Minimum frequency parameter.
     */
    public ProbabilisticContextFreeGrammar(TreeBank treeBank, int minCount){
        ArrayList<Symbol> variables;
        ArrayList<Rule> candidates;
        int total;
        constructDictionary(treeBank);
        for (int i = 0; i < treeBank.size(); i++){
            ParseTree parseTree = treeBank.get(i);
            updateExceptionalWordsInTree(parseTree, minCount);
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

    /**
     * Converts a parse node in a tree to a rule. The symbol in the parse node will be the symbol on the leaf side of the
     * rule, the symbols in the child nodes will be the symbols on the right hand side of the rule.
     * @param parseNode Parse node for which a rule will be created.
     * @param trim If true, the tags will be trimmed. If the symbol's data contains '-' or '=', this method trims all
     *             characters after those characters.
     * @return A new rule constructed from a parse node and its children.
     */
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

    /**
     * Recursive method to generate all rules from a subtree rooted at the given node.
     * @param parseNode Root node of the subtree.
     */
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

    /**
     * Calculates the probability of a parse node.
     * @param parseNode Parse node for which probability is calculated.
     * @return Probability of a parse node.
     */
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

    /**
     * Calculates the probability of a parse tree.
     * @param parseTree Parse tree for which probability is calculated.
     * @return Probability of the parse tree.
     */
    public double probability(ParseTree parseTree){
        return probability(parseTree.getRoot());
    }

    /**
     * In conversion to Chomsky Normal Form, rules like X -> Y are removed and new rules for every rule as Y -> beta are
     * replaced with X -> beta. The method first identifies all X -> Y rules. For every such rule, all rules Y -> beta
     * are identified. For every such rule, the method adds a new rule X -> beta. Every Y -> beta rule is then deleted.
     * The method also calculates the probability of the new rules based on the previous rules.
     */
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

    /**
     * In conversion to Chomsky Normal Form, rules like A -> BC... are replaced with A -> X1... and X1 -> BC. This
     * method determines such rules and for every such rule, it adds new rule X1->BC and updates rule A->BC to A->X1.
     * The method sets the probability of the rules X1->BC to 1, and calculates the probability of the rules A -> X1...
     */
    private void updateMultipleNonTerminalFromRightHandSide(){
        Rule updateCandidate;
        int newVariableCount = 0;
        updateCandidate = getMultipleNonTerminalCandidateToUpdate();
        while (updateCandidate != null){
            ArrayList<Symbol> newRightHandSide = new ArrayList<>();
            Symbol newSymbol = new Symbol("X" + newVariableCount);
            newRightHandSide.add(updateCandidate.getRightHandSideAt(0));
            newRightHandSide.add(updateCandidate.getRightHandSideAt(1));
            updateAllMultipleNonTerminalWithNewRule(updateCandidate.getRightHandSideAt(0), updateCandidate.getRightHandSideAt(1), newSymbol);
            addRule(new ProbabilisticRule(newSymbol, newRightHandSide, RuleType.TWO_NON_TERMINAL, 1.0));
            newVariableCount++;
            updateCandidate = getMultipleNonTerminalCandidateToUpdate();
        }
    }

    /**
     * The method converts the grammar into Chomsky normal form. First, rules like X -> Y are removed and new rules for
     * every rule as Y -> beta are replaced with X -> beta. Second, rules like A -> BC... are replaced with A -> X1...
     * and X1 -> BC.
     */
    public void convertToChomskyNormalForm(){
        removeSingleNonTerminalFromRightHandSide();
        updateMultipleNonTerminalFromRightHandSide();
        Comparator<Rule> comparator = new RuleComparator();
        rules.sort(comparator);
        Comparator<Rule> rightComparator = new RuleRightSideComparator();
        rulesRightSorted.sort(rightComparator);
    }

}

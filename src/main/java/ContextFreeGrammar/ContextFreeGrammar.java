package ContextFreeGrammar;

import Corpus.Sentence;
import DataStructure.CounterHashMap;
import ParseTree.*;
import ParseTree.NodeCondition.IsLeaf;
import Dictionary.Word;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class ContextFreeGrammar {

    protected CounterHashMap<String> dictionary = new CounterHashMap<>();
    protected ArrayList<Rule> rules = new ArrayList<>();
    protected ArrayList<Rule> rulesRightSorted = new ArrayList<>();
    protected int minCount = 1;

    /**
     * Empty constructor for the ContextFreeGrammar class.
     */
    public ContextFreeGrammar(){
    }

    /**
     * Reads the lexicon for the grammar. Each line consists of two items, the terminal symbol and the frequency of
     * that symbol. The method fills the dictionary counter hash map according to this data.
     * @param dictionaryFileName File name of the lexicon.
     */
    protected void readDictionary(String dictionaryFileName){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(dictionaryFileName)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split(" ");
                dictionary.putNTimes(items[0], Integer.parseInt(items[1]));
                line = br.readLine();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Constructor for the ContextFreeGrammar class. Reads the rules from the rule file, lexicon rules from the
     * dictionary file and sets the minimum frequency parameter.
     * @param ruleFileName File name for the rule file.
     * @param dictionaryFileName File name for the lexicon file.
     * @param minCount Minimum frequency parameter.
     */
    public ContextFreeGrammar(String ruleFileName, String dictionaryFileName, int minCount){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(ruleFileName)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                Rule newRule = new Rule(line);
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
     * Another constructor for the ContextFreeGrammar class. Constructs the lexicon from the leaf nodes of the trees
     * in the given treebank. Extracts rules from the non-leaf nodes of the trees in the given treebank. Also sets the
     * minimum frequency parameter.
     * @param treeBank Treebank containing the constituency trees.
     * @param minCount Minimum frequency parameter.
     */
    public ContextFreeGrammar(TreeBank treeBank, int minCount){
        constructDictionary(treeBank);
        for (int i = 0; i < treeBank.size(); i++){
            ParseTree parseTree = treeBank.get(i);
            updateExceptionalWordsInTree(parseTree, minCount);
            addRules(parseTree.getRoot());
        }
        updateTypes();
        this.minCount = minCount;
    }

    /**
     * Constructs the lexicon from the given treebank. Reads each tree and for each leaf node in each tree puts the
     * symbol in the dictionary.
     * @param treeBank Treebank containing the constituency trees.
     */
    protected void constructDictionary(TreeBank treeBank){
        for (int i = 0; i < treeBank.size(); i++){
            ParseTree parseTree = treeBank.get(i);
            NodeCollector nodeCollector = new NodeCollector(parseTree.getRoot(), new IsLeaf());
            ArrayList<ParseNode> leafList = nodeCollector.collect();
            for (ParseNode parseNode : leafList){
                dictionary.put(parseNode.getData().getName());
            }
        }
    }

    /**
     * Updates the exceptional symbols of the leaf nodes in the trees. Constituency trees consists of rare symbols and
     * numbers, which are usually useless in creating constituency grammars. This is due to the fact that, numbers may
     * not occur exactly the same both in the train and/or test set, although they have the same meaning in general.
     * Similarly, when a symbol occurs in the test set but not in the training set, there will not be any rule covering
     * that symbol and therefore no parse tree will be generated. For those reasons, the leaf nodes containing numerals
     * are converted to the same terminal symbol, i.e. _num_; the leaf nodes containing rare symbols are converted to
     * the same terminal symbol, i.e. _rare_.
     * @param parseTree Parse tree to be updated.
     * @param minCount Minimum frequency for the terminal symbols to be considered as rare.
     */
    public void updateExceptionalWordsInTree(ParseTree parseTree, int minCount){
        NodeCollector nodeCollector = new NodeCollector(parseTree.getRoot(), new IsLeaf());
        ArrayList<ParseNode> leafList = nodeCollector.collect();
        Pattern pattern1 = Pattern.compile("\\+?\\d+");
        Pattern pattern2 = Pattern.compile("\\+?(\\d+)?\\.\\d*");
        for (ParseNode parseNode : leafList){
            String data = parseNode.getData().getName();
            if (pattern1.matcher(data).matches() || (pattern2.matcher(data).matches() && !data.equals("."))){
                parseNode.setData(new Symbol("_num_"));
            } else {
                if (dictionary.count(data) < minCount){
                    parseNode.setData(new Symbol("_rare_"));
                }
            }
        }
    }

    /**
     * Updates the exceptional words in the sentences for which constituency parse trees will be generated. Constituency
     * trees consist of rare symbols and numbers, which are usually useless in creating constituency grammars. This is
     * due to the fact that, numbers may not occur exactly the same both in the train and/or test set, although they have
     * the same meaning in general. Similarly, when a symbol occurs in the test set but not in the training set, there
     * will not be any rule covering that symbol and therefore no parse tree will be generated. For those reasons, the
     * words containing numerals are converted to the same terminal symbol, i.e. _num_; thewords containing rare symbols
     * are converted to the same terminal symbol, i.e. _rare_.
     * @param sentence Sentence to be updated.
     */
    public void updateExceptionalWordsInSentence(Sentence sentence){
        Pattern pattern1 = Pattern.compile("\\+?\\d+");
        Pattern pattern2 = Pattern.compile("\\+?(\\d+)?\\.\\d*");
        for (int i = 0; i < sentence.wordCount(); i++){
            Word word = sentence.getWord(i);
            if (pattern1.matcher(word.getName()).matches() || (pattern2.matcher(word.getName()).matches() && !word.getName().equals("."))){
                word.setName("_num_");
            } else {
                if (dictionary.count(word.getName()) < minCount){
                    word.setName("_rare_");
                }
            }
        }
    }

    /**
     * After constructing the constituency tree with a parser for a sentence, it contains exceptional words such as
     * rare words and numbers, which are represented as _rare_ and _num_ symbols in the tree. Those words should be
     * converted to their original forms. This method replaces the exceptional symbols to their original forms by
     * replacing _rare_ and _num_ symbols.
     * @param parseTree Parse tree to be updated.
     * @param sentence Original sentence for which constituency tree is generated.
     */
    public void reinsertExceptionalWordsFromSentence(ParseTree parseTree, Sentence sentence){
        NodeCollector nodeCollector = new NodeCollector(parseTree.getRoot(), new IsLeaf());
        ArrayList<ParseNode> leafList = nodeCollector.collect();
        for (int i = 0; i < leafList.size(); i++){
            String treeWord = leafList.get(i).getData().getName();
            String sentenceWord = sentence.getWord(i).getName();
            if (treeWord.equals("_rare_") || treeWord.equals("_num_")){
                leafList.get(i).setData(new Symbol(sentenceWord));
            }
        }
    }

    /**
     * Updates the types of the rules according to the number of symbols on the right hand side. Rule type is TERMINAL
     * if the rule is like X -> a, SINGLE_NON_TERMINAL if the rule is like X -> Y, TWO_NON_TERMINAL if the rule is like
     * X -> YZ, MULTIPLE_NON_TERMINAL if the rule is like X -> YZT...
     */
    protected void updateTypes(){
        HashSet<String> nonTerminals = new HashSet<>();
        for (Rule rule: rules){
            nonTerminals.add(rule.leftHandSide.getName());
        }
        for (Rule rule : rules){
            if (rule.getRightHandSideSize() > 2){
                rule.type = RuleType.MULTIPLE_NON_TERMINAL;
            } else {
                if (rule.getRightHandSideSize() == 2){
                    rule.type = RuleType.TWO_NON_TERMINAL;
                } else {
                    if (rule.getRightHandSideAt(0).isTerminal() || Word.isPunctuation(rule.getRightHandSideAt(0).getName()) || !nonTerminals.contains(rule.getRightHandSideAt(0).getName())){
                        rule.type = RuleType.TERMINAL;
                    } else {
                        rule.type = RuleType.SINGLE_NON_TERMINAL;
                    }
                }
            }
        }
    }

    /**
     * Converts a parse node in a tree to a rule. The symbol in the parse node will be the symbol on the leaf side of the
     * rule, the symbols in the child nodes will be the symbols on the right hand side of the rule.
     * @param parseNode Parse node for which a rule will be created.
     * @param trim If true, the tags will be trimmed. If the symbol's data contains '-' or '=', this method trims all
     *             characters after those characters.
     * @return A new rule constructed from a parse node and its children.
     */
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

    /**
     * Recursive method to generate all rules from a subtree rooted at the given node.
     * @param parseNode Root node of the subtree.
     */
    private void addRules(ParseNode parseNode){
        Rule newRule;
        newRule = toRule(parseNode, true);
        if (newRule != null){
           addRule(newRule);
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
     * Writes the rules and lexicon of this constituency grammar to the rule and dictionary files.
     * @param ruleFileName File name for the rule file.
     * @param dictionaryFileName File name for the lexicon file.
     */
    public void writeToFile(String ruleFileName, String dictionaryFileName){
        try {
            FileWriter ruleWriter = new FileWriter(ruleFileName);
            FileWriter dictionaryWriter = new FileWriter(dictionaryFileName);
            for (Rule rule : rules){
                ruleWriter.write(rule.toString() + "\n");
            }
            for (Map.Entry<String, Integer> set : dictionary.entrySet()) {
                dictionaryWriter.write(set.getKey() + " " + set.getValue() + "\n");
            }
            ruleWriter.close();
            dictionaryWriter.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Inserts a new rule into the correct position in the sorted rules and rulesRightSorted array lists.
     * @param newRule Rule to be inserted into the sorted array lists.
     */
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

    /**
     * Removes a given rule from the sorted rules and rulesRightSorted array lists.
     * @param rule Rule to be removed from the sorted array lists.
     */
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

    /**
     * Returns rules formed as X -> ... Since there can be more than one rule, which have X on the left side, the method
     * first binary searches the rule to obtain the position of such a rule, then goes up and down to obtain others
     * having X on the left side.
     * @param X Left side of the rule
     * @return Rules of the form X -> ...
     */
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

    /**
     * Returns all symbols X from terminal rules such as X -> a.
     * @return All symbols X from terminal rules such as X -> a.
     */
    public ArrayList<Symbol> partOfSpeechTags(){
        ArrayList<Symbol> result = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.getType() == RuleType.TERMINAL && !result.contains(rule.getLeftHandSide())) {
                result.add(rule.getLeftHandSide());
            }
        }
        return result;
    }

    /**
     * Returns all symbols X from all rules such as X -> ...
     * @return All symbols X from all rules such as X -> ...
     */
    public ArrayList<Symbol> getLeftSide(){
        ArrayList<Symbol> result = new ArrayList<>();
        for (Rule rule : rules) {
            if (!result.contains(rule.getLeftHandSide())) {
                result.add(rule.getLeftHandSide());
            }
        }
        return result;
    }

    /**
     * Returns all rules with the given terminal symbol on the right hand side, that is it returns all terminal rules
     * such as X -> s
     * @param s Terminal symbol on the right hand side.
     * @return All rules with the given terminal symbol on the right hand side
     */
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

    /**
     * Returns all rules with the given non-terminal symbol on the right hand side, that is it returns all non-terminal
     * rules such as X -> S
     * @param S Non-terminal symbol on the right hand side.
     * @return All rules with the given non-terminal symbol on the right hand side
     */
    public ArrayList<Rule> getRulesWithRightSideX(Symbol S){
        int pos, posUp, posDown;
        ArrayList<Rule> result = new ArrayList<>();
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

    /**
     * Returns all rules with the given two non-terminal symbols on the right hand side, that is it returns all
     * non-terminal rules such as X -> AB.
     * @param A First non-terminal symbol on the right hand side.
     * @param B Second non-terminal symbol on the right hand side.
     * @return All rules with the given two non-terminal symbols on the right hand side
     */
    public ArrayList<Rule> getRulesWithTwoNonTerminalsOnRightSide(Symbol A, Symbol B){
        int pos, posUp, posDown;
        ArrayList<Rule> result = new ArrayList<>();
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

    /**
     * Returns the symbol on the right side of the first rule with one non-terminal symbol on the right hand side, that
     * is it returns S of the first rule such as X -> S. S should also not be in the given removed list.
     * @param removedList Discarded list for symbol S.
     * @return The symbol on the right side of the first rule with one non-terminal symbol on the right hand side. The
     * symbol to be returned should also not be in the given discarded list.
     */
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

    /**
     * Returns all rules with more than two non-terminal symbols on the right hand side, that is it returns all
     * non-terminal rules such as X -> ABC...
     * @return All rules with more than two non-terminal symbols on the right hand side.
     */
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

    /**
     * In conversion to Chomsky Normal Form, rules like X -> Y are removed and new rules for every rule as Y -> beta are
     * replaced with X -> beta. The method first identifies all X -> Y rules. For every such rule, all rules Y -> beta
     * are identified. For every such rule, the method adds a new rule X -> beta. Every Y -> beta rule is then deleted.
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
                    addRule(new Rule(rule.leftHandSide, (ArrayList<Symbol>) candidate.getRightHandSide().clone(), candidate.getType()));
                }
                removeRule(rule);
            }
            nonTerminalList.add(removeCandidate);
            removeCandidate = getSingleNonTerminalCandidateToRemove(nonTerminalList);
        }
    }

    /**
     * In conversion to Chomsky Normal Form, rules like A -> BC... are replaced with A -> X1... and X1 -> BC. This
     * method replaces B and C non-terminals on the right hand side with X1 for all rules in the grammar.
     * @param first Non-terminal symbol B.
     * @param second Non-terminal symbol C.
     * @param with Non-terminal symbol X1.
     */
    protected void updateAllMultipleNonTerminalWithNewRule(Symbol first, Symbol second, Symbol with){
        for (Rule rule : rules) {
            if (rule.type == RuleType.MULTIPLE_NON_TERMINAL){
                rule.updateMultipleNonTerminal(first, second, with);
            }
        }
    }

    /**
     * In conversion to Chomsky Normal Form, rules like A -> BC... are replaced with A -> X1... and X1 -> BC. This
     * method determines such rules and for every such rule, it adds new rule X1->BC and updates rule A->BC to A->X1.
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
            addRule(new Rule(newSymbol, newRightHandSide, RuleType.TWO_NON_TERMINAL));
            updateCandidate = getMultipleNonTerminalCandidateToUpdate();
            newVariableCount++;
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

    /**
     * Searches a given rule in the grammar.
     * @param rule Rule to be searched.
     * @return Rule if found, null otherwise.
     */
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

    /**
     * Returns number of rules in the grammar.
     * @return Number of rules in the Context Free Grammar.
     */
    public int size(){
        return rules.size();
    }
}

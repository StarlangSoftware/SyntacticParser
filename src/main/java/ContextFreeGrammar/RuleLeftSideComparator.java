package ContextFreeGrammar;

import java.util.Comparator;

public class RuleLeftSideComparator implements Comparator<Rule> {

    public int compare(Rule ruleA, Rule ruleB) {
        return ruleA.getLeftHandSide().getName().compareTo(ruleB.getLeftHandSide().getName());
    }

}

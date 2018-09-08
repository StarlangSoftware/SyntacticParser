package ContextFreeGrammar;

import java.util.Comparator;

public class RuleComparator implements Comparator<Rule> {

    public int compare(Rule ruleA, Rule ruleB) {
        if (ruleA.getLeftHandSide().equals(ruleB.getLeftHandSide())){
            Comparator<Rule> rightComparator = new RuleRightSideComparator();
            return rightComparator.compare(ruleA, ruleB);
        } else {
            Comparator<Rule> leftComparator = new RuleLeftSideComparator();
            return leftComparator.compare(ruleA, ruleB);
        }
    }

}

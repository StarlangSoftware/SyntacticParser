package ContextFreeGrammar;

import java.util.Comparator;

public class RuleComparator implements Comparator<Rule> {

    /**
     * Compares two rules based on first their left hand side and their right hand side lexicographically.
     * @param ruleA the first rule to be compared.
     * @param ruleB the second rule to be compared.
     * @return -1 if the first rule is less than the second rule lexicographically, 1 if the first rule is larger than
     * the second rule lexicographically, 0 if they are the same rule.
     */
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

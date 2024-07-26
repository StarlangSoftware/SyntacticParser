package ContextFreeGrammar;

import java.util.Comparator;

public class RuleLeftSideComparator implements Comparator<Rule> {

    /**
     * Compares two rules based on their left sides lexicographically.
     * @param ruleA the first rule to be compared.
     * @param ruleB the second rule to be compared.
     * @return -1 if the first rule is less than the second rule lexicographically, 1 if the first rule is larger than
     *          the second rule lexicographically, 0 if they are the same rule.
     */
    public int compare(Rule ruleA, Rule ruleB) {
        return ruleA.getLeftHandSide().getName().compareTo(ruleB.getLeftHandSide().getName());
    }

}

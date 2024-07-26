package ContextFreeGrammar;

import java.util.Comparator;

public class RuleRightSideComparator implements Comparator<Rule> {

    /**
     * Compares two rules based on their right sides lexicographically.
     * @param ruleA the first rule to be compared.
     * @param ruleB the second rule to be compared.
     * @return -1 if the first rule is less than the second rule lexicographically, 1 if the first rule is larger than
     *          the second rule lexicographically, 0 if they are the same rule.
     */
    public int compare(Rule ruleA, Rule ruleB) {
        int i = 0;
        while (i < ruleA.getRightHandSideSize() && i < ruleB.getRightHandSideSize()){
            if (ruleA.getRightHandSideAt(i).getName().equals(ruleB.getRightHandSideAt(i).getName())){
                i++;
            } else {
                return ruleA.getRightHandSideAt(i).getName().compareTo(ruleB.getRightHandSideAt(i).getName());
            }
        }
        return Integer.compare(ruleA.getRightHandSideSize(), ruleB.getRightHandSideSize());
    }

}

package ContextFreeGrammar;

import java.util.Comparator;

public class RuleRightSideComparator implements Comparator<Rule> {

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

package SyntacticParser;

import ContextFreeGrammar.Rule;
import ContextFreeGrammar.RuleComparator;

import java.util.Comparator;

public class ChartEntryComparator implements Comparator<ChartEntry> {

    public int compare(ChartEntry entryA, ChartEntry entryB) {
        if (entryA.getRule().equals(entryB.getRule())){
            if (entryA.from() == entryB.from()){
                if (entryA.dotPlace() == entryB.dotPlace()){
                    for (ChartEntry state:entryA.getStates()){
                        if (!entryB.getStates().contains(state)){
                            return -1;
                        }
                    }
                    return 0;
                } else {
                    if (entryA.dotPlace() < entryB.dotPlace()){
                        return -1;
                    } else {
                        return 1;
                    }
                }
            } else {
                if (entryA.from() < entryB.from()){
                    return -1;
                } else {
                    return 1;
                }
            }
        } else {
            Comparator<Rule> comparator = new RuleComparator();
            return comparator.compare(entryA.getRule(), entryB.getRule());
        }
    }

}

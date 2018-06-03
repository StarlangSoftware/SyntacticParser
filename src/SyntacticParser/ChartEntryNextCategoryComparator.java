package SyntacticParser;

import java.util.Comparator;

public class ChartEntryNextCategoryComparator implements Comparator<ChartEntry> {

    public int compare(ChartEntry entryA, ChartEntry entryB) {
        if (entryA.isComplete() && entryB.isComplete()){
            return 0;
        } else {
            if (entryA.isComplete()){
                return -1;
            } else {
                if (entryB.isComplete()){
                    return 1;
                } else {
                    return entryA.getNextCategory().getName().compareTo(entryB.getNextCategory().getName());
                }
            }
        }
    }

}

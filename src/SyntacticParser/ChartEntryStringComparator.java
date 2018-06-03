package SyntacticParser;

import java.util.Comparator;

public class ChartEntryStringComparator implements Comparator<ChartEntry> {

    public int compare(ChartEntry entryA, ChartEntry entryB) {
        return entryA.toString().compareTo(entryB.toString());
    }

}

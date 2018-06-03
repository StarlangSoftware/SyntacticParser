package SyntacticParser;

import ContextFreeGrammar.Rule;
import ParseTree.Symbol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Chart {

    private ArrayList<ChartEntry> chartEntries;
    private ArrayList<ChartEntry> chartEntriesSorted;
    private ArrayList<ChartEntry> chartEntriesNextCategorySorted;

    public Chart(){
        chartEntries = new ArrayList<ChartEntry>();
        chartEntriesSorted = new ArrayList<ChartEntry>();
        chartEntriesNextCategorySorted = new ArrayList<ChartEntry>();
    }

    public void writeToFile(String fileName){
        try {
            FileWriter fw = new FileWriter(new File(fileName));
            for (ChartEntry entry:chartEntriesSorted){
                fw.write(entry.toString() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int size(){
        return chartEntries.size();
    }


    public boolean addOrUpdate(ChartEntry chartEntry){
        int middle, middleUp, middleDown;
        ChartEntry deleted;
        Comparator<ChartEntry> comparator = new ChartEntryStringComparator();
        middle = Collections.binarySearch(chartEntriesSorted, chartEntry, comparator);
        if (middle < 0){
            addChartEntry(chartEntry);
            return true;
        } else {
            if (chartEntry.getProbability() > chartEntriesSorted.get(middle).getProbability()){
                deleted = chartEntriesSorted.get(middle);
                chartEntriesSorted.set(middle, chartEntry);
                Comparator<ChartEntry> nextCategoryComparator = new ChartEntryNextCategoryComparator();
                middle = Collections.binarySearch(chartEntriesNextCategorySorted, chartEntry, nextCategoryComparator);
                if (middle > 0){
                    if (chartEntriesNextCategorySorted.get(middle).equals(deleted)){
                        chartEntriesNextCategorySorted.set(middle, chartEntry);
                    } else {
                        middleUp = middle - 1;
                        while (middleUp >= 0 && !chartEntriesNextCategorySorted.get(middleUp).isComplete() && chartEntriesNextCategorySorted.get(middleUp).getNextCategory().equals(chartEntry.getNextCategory())){
                            if (chartEntriesNextCategorySorted.get(middleUp).equals(deleted)){
                                chartEntriesNextCategorySorted.set(middleUp, chartEntry);
                                break;
                            }
                            middleUp--;
                        }
                        middleDown = middle;
                        while (middleDown < chartEntriesNextCategorySorted.size() && !chartEntriesNextCategorySorted.get(middleDown).isComplete() && chartEntriesNextCategorySorted.get(middleDown).getNextCategory().equals(chartEntry.getNextCategory())){
                            if (chartEntriesNextCategorySorted.get(middleDown).equals(deleted)){
                                chartEntriesNextCategorySorted.set(middleDown, chartEntry);
                                break;
                            }
                            middleDown++;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void addChartEntry(ChartEntry chartEntry){
        int middle;
        Comparator<ChartEntry> comparator = new ChartEntryComparator();
        middle = Collections.binarySearch(chartEntriesSorted, chartEntry, comparator);
        if (middle < 0){
            chartEntriesSorted.add(-middle - 1, chartEntry);
            chartEntries.add(chartEntry);
            Comparator<ChartEntry> nextCategoryComparator = new ChartEntryNextCategoryComparator();
            middle = Collections.binarySearch(chartEntriesNextCategorySorted, chartEntry, nextCategoryComparator);
            if (middle >= 0){
                chartEntriesNextCategorySorted.add(middle, chartEntry);
            } else {
                chartEntriesNextCategorySorted.add(-middle - 1, chartEntry);
            }
        }
    }

    public ArrayList<ChartEntry> getNextCategoryX(Symbol X){
        int middle, middleUp, middleDown;
        ChartEntry dummyChartEntry;
        ArrayList<ChartEntry> result = new ArrayList<ChartEntry>();
        Comparator<ChartEntry> nextCategoryComparator = new ChartEntryNextCategoryComparator();
        dummyChartEntry = new ChartEntry(new Rule(X, X), 0, 0, 0);
        middle = Collections.binarySearch(chartEntriesNextCategorySorted, dummyChartEntry, nextCategoryComparator);
        if (middle > 0){
            middleUp = middle - 1;
            while (middleUp >= 0 && !chartEntriesNextCategorySorted.get(middleUp).isComplete() && chartEntriesNextCategorySorted.get(middleUp).getNextCategory().equals(X)){
                result.add(chartEntriesNextCategorySorted.get(middleUp));
                middleUp--;
            }
            middleDown = middle;
            while (middleDown < chartEntriesNextCategorySorted.size() && !chartEntriesNextCategorySorted.get(middleDown).isComplete() && chartEntriesNextCategorySorted.get(middleDown).getNextCategory().equals(X)){
                result.add(chartEntriesNextCategorySorted.get(middleDown));
                middleDown++;
            }
        }
        return result;
    }

    public ChartEntry getEntry(int index){
        return chartEntries.get(index);
    }

    public ArrayList<ChartEntry> getSentenceChartEntries(int wordCount){
        ArrayList<ChartEntry> result = new ArrayList<ChartEntry>();
        for (ChartEntry entry : chartEntriesSorted){
            if (entry.isComplete() && entry.from() == 0 && entry.to() == wordCount && entry.getRule().getLeftHandSide().equals(new Symbol("S"))){
                result.add(entry);
            }
        }
        return result;
    }
}

package SyntacticParser;

import ContextFreeGrammar.*;
import Corpus.Sentence;
import Dictionary.Word;
import ParseTree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EarleyParser implements SyntacticParser {

    private ArrayList predictedList[];
    private Map<String, Integer> completedList;

    private void predictor(ContextFreeGrammar cfg, Chart[] chart, ChartEntry chartEntry){
        Symbol B;
        ArrayList<Rule> candidates;
        B = chartEntry.getNextCategory();
        if (!predictedList[chartEntry.to()].contains(B)){
            candidates = cfg.getRulesWithLeftSideX(B);
            for (Rule candidate: candidates){
                if (candidate.getRightHandSideSize() + chartEntry.to() < chart.length){
                    chart[chartEntry.to()].addChartEntry(new ChartEntry(candidate, chartEntry.to(), chartEntry.to(), 0));
                }
            }
            predictedList[chartEntry.to()].add(B);
        }
    }

    private void scanner(ContextFreeGrammar cfg, Sentence sentence, Chart[] chart, ChartEntry chartEntry){
        Symbol B, W;
        Word word;
        ArrayList<Rule> candidates;
        B = chartEntry.getNextCategory();
        if (chartEntry.to() == sentence.wordCount())
            return;
        word = sentence.getWord(chartEntry.to());
        W = new Symbol(word.getName());
        candidates = cfg.getTerminalRulesWithRightSideX(W);
        for (Rule candidate:candidates){
            if (candidate.getLeftHandSide().equals(B)){
                chart[chartEntry.to() + 1].addChartEntry(new ChartEntry(candidate, chartEntry.to(), chartEntry.to() + 1, 1));
                break;
            }
        }
    }

    private void completer(Chart[] chart, ChartEntry chartEntry){
        Symbol B;
        ChartEntry newEntry;
        ArrayList<ChartEntry> candidates;
        B = chartEntry.getRule().getLeftHandSide();
        candidates = chart[chartEntry.from()].getNextCategoryX(B);
        for (ChartEntry A: candidates){
            if (A.getRule().getRightHandSideSize() - A.dotPlace() - 1 + chartEntry.to() < chart.length){
                if (!completedList.containsKey(A.toStringExtended() + chartEntry.toStringExtended())){
                    newEntry = new ChartEntry(A.getRule(), A.from(), chartEntry.to(), A.dotPlace() + 1, A, chartEntry);
                    if (!newEntry.toString().equals(chartEntry.toString())){
                        chart[chartEntry.to()].addChartEntry(newEntry);
                        completedList.put(A.toStringExtended() + chartEntry.toStringExtended(), A.dotPlace());
                    }
                }
            }
        }
    }

    public Chart[] constructChart(ContextFreeGrammar cfg, Sentence sentence) {
        int i, j;
        long start, end;
        Chart[] chart;
        ArrayList<Rule> initialRules;
        ArrayList<Symbol> posTags;
        ChartEntry chartEntry;
        chart = new Chart[sentence.wordCount() + 1];
        predictedList = new ArrayList[sentence.wordCount() + 1];
        for (i = 0; i < sentence.wordCount() + 1; i++){
            chart[i] = new Chart();
            predictedList[i] = new ArrayList<Symbol>();
        }
        initialRules = cfg.getRulesWithLeftSideX(new Symbol("S"));
        for (Rule rule:initialRules){
            chart[0].addChartEntry(new ChartEntry(rule, 0, 0, 0));
        }
        posTags = cfg.partOfSpeechTags();
        for (i = 0; i <= sentence.wordCount(); i++){
            start = System.currentTimeMillis();
            completedList = new HashMap<>();
            for (j = 0; j < chart[i].size(); j++){
                chartEntry = chart[i].getEntry(j);
                if (!chartEntry.isComplete()){
                    if (!chartEntry.nextCategoryPartOfSpeech(posTags)){
                        predictor(cfg, chart, chartEntry);
                    } else {
                        scanner(cfg, sentence, chart, chartEntry);
                    }
                } else {
                    completer(chart, chartEntry);
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Word " + i + " completed in " + (end - start) + " milliseconds");
        }
        return chart;
    }

    public ArrayList<ParseTree> parse(ContextFreeGrammar cfg, Sentence sentence){
        ArrayList<ParseTree> parseTrees;
        ArrayList<ChartEntry> entries;
        Chart[] chart = constructChart(cfg, sentence);
        parseTrees = new ArrayList<>();
        entries = chart[sentence.wordCount()].getSentenceChartEntries(sentence.wordCount());
        for (ChartEntry entry: entries){
            parseTrees.add(new ParseTree(entry.constructParseNode()));
        }
        return parseTrees;
    }
}

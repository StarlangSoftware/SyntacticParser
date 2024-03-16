package ProbabilisticParser;

import ContextFreeGrammar.Rule;
import ParseTree.Symbol;
import Corpus.Sentence;
import Dictionary.Word;
import ProbabilisticContextFreeGrammar.*;
import ParseTree.ParseTree;
import SyntacticParser.Chart;
import SyntacticParser.ChartEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProbabilisticEarleyParser implements ProbabilisticParser {

    private ArrayList[] predictedList;
    private Map<String, Integer> completedList;

    private void predictor(ProbabilisticContextFreeGrammar pcfg, Chart[] chart, ChartEntry chartEntry){
        Symbol B;
        ArrayList<Rule> candidates;
        B = chartEntry.getNextCategory();
        if (!predictedList[chartEntry.to()].contains(B)){
            candidates = pcfg.getRulesWithLeftSideX(B);
            for (Rule candidate: candidates){
                if (candidate.getRightHandSideSize() + chartEntry.to() < chart.length){
                    chart[chartEntry.to()].addChartEntry(new ChartEntry((ProbabilisticRule)candidate, chartEntry.to(), chartEntry.to(), 0));
                }
            }
            predictedList[chartEntry.to()].add(B);
        }
    }

    private void scanner(ProbabilisticContextFreeGrammar pcfg, Sentence sentence, Chart[] chart, ChartEntry chartEntry){
        Symbol B, W;
        Word word;
        ArrayList<Rule> candidates;
        B = chartEntry.getNextCategory();
        if (chartEntry.to() == sentence.wordCount())
            return;
        word = sentence.getWord(chartEntry.to());
        W = new Symbol(word.getName());
        candidates = pcfg.getTerminalRulesWithRightSideX(W);
        for (Rule candidate:candidates){
            if (candidate.getLeftHandSide().equals(B)){
                chart[chartEntry.to() + 1].addChartEntry(new ChartEntry((ProbabilisticRule)candidate, chartEntry.to(), chartEntry.to() + 1, 1));
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
                if (!completedList.containsKey(A.toStringExtended() + chartEntry.toStringExtended())) {
                    newEntry = new ChartEntry((ProbabilisticRule)A.getRule(), A.from(), chartEntry.to(), A.dotPlace() + 1, A, chartEntry);
                    if (!newEntry.toString().equals(chartEntry.toString())) {
                        if (chart[chartEntry.to()].addOrUpdate(newEntry)){
                            completedList.put(A.toStringExtended() + chartEntry.toStringExtended(), A.dotPlace());
                        }
                    }
                }
            }
        }
    }

    public Chart[] constructChart(ProbabilisticContextFreeGrammar pcfg, Sentence sentence) {
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
        initialRules = pcfg.getRulesWithLeftSideX(new Symbol("S"));
        for (Rule rule:initialRules){
            chart[0].addChartEntry(new ChartEntry((ProbabilisticRule)rule, 0, 0, 0));
        }
        posTags = pcfg.partOfSpeechTags();
        for (i = 0; i <= sentence.wordCount(); i++){
            start = System.currentTimeMillis();
            completedList = new HashMap<>();
            for (j = 0; j < chart[i].size(); j++){
                chartEntry = chart[i].getEntry(j);
                if (!chartEntry.isComplete()){
                    if (!chartEntry.nextCategoryPartOfSpeech(posTags)){
                        predictor(pcfg, chart, chartEntry);
                    } else {
                        scanner(pcfg, sentence, chart, chartEntry);
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

    public ArrayList<ParseTree> parse(ProbabilisticContextFreeGrammar pCfg, Sentence sentence){
        ArrayList<ParseTree> parseTrees;
        ArrayList<ParseTree> result;
        ArrayList<ChartEntry> entries;
        ParseTree tree;
        double bestProbability, probability;
        Chart[] chart = constructChart(pCfg, sentence);
        parseTrees = new ArrayList<>();
        bestProbability = -Double.MAX_VALUE;
        entries = chart[sentence.wordCount()].getSentenceChartEntries(sentence.wordCount());
        for (ChartEntry entry: entries){
            tree = new ParseTree(entry.constructParseNode());
            probability = pCfg.probability(tree);
            if (probability > bestProbability){
                bestProbability = probability;
            }
            parseTrees.add(tree);
        }
        result = new ArrayList<>();
        for (ParseTree parseTree: parseTrees){
            probability = pCfg.probability(parseTree);
            if (probability == bestProbability){
                result.add(parseTree);
            }
        }
        return result;
    }
}

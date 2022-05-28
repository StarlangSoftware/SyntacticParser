package ProbabilisticParser;

import ContextFreeGrammar.*;
import Corpus.Sentence;
import Dictionary.Word;
import ParseTree.*;
import ProbabilisticContextFreeGrammar.*;
import SyntacticParser.PartialParseList;
import java.util.ArrayList;

public class ProbabilisticCYKParser implements ProbabilisticParser {

    public ArrayList<ParseTree> parse(ProbabilisticContextFreeGrammar pCfg, Sentence sentence) {
        int i, j, k, x, y;
        PartialParseList table[][];
        ProbabilisticParseNode leftNode, rightNode;
        double bestProbability, probability;
        ArrayList<Rule> candidates;
        ArrayList<ParseTree> parseTrees = new ArrayList<ParseTree>();
        Sentence backUp = new Sentence();
        for (i = 0; i < sentence.wordCount(); i++){
            backUp.addWord(new Word(sentence.getWord(i).getName()));
        }
        pCfg.removeExceptionalWordsFromSentence(sentence);
        table = new PartialParseList[sentence.wordCount()][sentence.wordCount()];
        for (i = 0; i < sentence.wordCount(); i++)
            for (j = i; j < sentence.wordCount(); j++)
                table[i][j] = new PartialParseList();
        for (i = 0; i < sentence.wordCount(); i++){
            candidates = pCfg.getTerminalRulesWithRightSideX(new Symbol(sentence.getWord(i).getName()));
            for (Rule candidate: candidates){
                table[i][i].addPartialParse(new ProbabilisticParseNode(new ParseNode(new Symbol(sentence.getWord(i).getName())), candidate.getLeftHandSide(), Math.log(((ProbabilisticRule) candidate).getProbability())));
            }
        }
        for (j = 1; j < sentence.wordCount(); j++){
            for (i = j - 1; i >= 0; i--){
                for (k = i; k < j; k++)
                    for (x = 0; x < table[i][k].size(); x++)
                        for (y = 0; y < table[k + 1][j].size(); y++){
                            leftNode = (ProbabilisticParseNode) table[i][k].getPartialParse(x);
                            rightNode = (ProbabilisticParseNode) table[k + 1][j].getPartialParse(y);
                            candidates = pCfg.getRulesWithTwoNonTerminalsOnRightSide(leftNode.getData(), rightNode.getData());
                            for (Rule candidate: candidates){
                                probability = Math.log(((ProbabilisticRule) candidate).getProbability()) + leftNode.getLogProbability() + rightNode.getLogProbability();
                                table[i][j].updatePartialParse(new ProbabilisticParseNode(leftNode, rightNode, candidate.getLeftHandSide(), probability));
                            }
                        }
            }
        }
        bestProbability = -Double.MAX_VALUE;
        for (i = 0; i < table[0][sentence.wordCount() - 1].size(); i++){
            if (table[0][sentence.wordCount() - 1].getPartialParse(i).getData().getName().equals("S") && ((ProbabilisticParseNode) table[0][sentence.wordCount() - 1].getPartialParse(i)).getLogProbability() > bestProbability) {
                bestProbability = ((ProbabilisticParseNode) table[0][sentence.wordCount() - 1].getPartialParse(i)).getLogProbability();
            }
        }
        for (i = 0; i < table[0][sentence.wordCount() - 1].size(); i++){
            if (table[0][sentence.wordCount() - 1].getPartialParse(i).getData().getName().equals("S") && ((ProbabilisticParseNode) table[0][sentence.wordCount() - 1].getPartialParse(i)).getLogProbability() == bestProbability) {
                ParseTree parseTree = new ParseTree(table[0][sentence.wordCount() - 1].getPartialParse(i));
                parseTree.correctParents();
                parseTree.removeXNodes();
                parseTrees.add(parseTree);
            }
        }
        for (ParseTree parseTree : parseTrees){
            pCfg.reinsertExceptionalWordsFromSentence(parseTree, backUp);
        }
        return parseTrees;
    }
}

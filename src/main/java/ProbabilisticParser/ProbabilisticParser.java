package ProbabilisticParser;

import Corpus.Sentence;
import ProbabilisticContextFreeGrammar.ProbabilisticContextFreeGrammar;
import ParseTree.ParseTree;
import java.util.ArrayList;

public interface ProbabilisticParser {

    ArrayList<ParseTree> parse(ProbabilisticContextFreeGrammar pCfg, Sentence sentence);
}

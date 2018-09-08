package SyntacticParser;

import java.util.ArrayList;
import ContextFreeGrammar.*;
import Corpus.Sentence;
import ParseTree.ParseTree;

public interface SyntacticParser {

    ArrayList<ParseTree> parse(ContextFreeGrammar cfg, Sentence sentence);

}

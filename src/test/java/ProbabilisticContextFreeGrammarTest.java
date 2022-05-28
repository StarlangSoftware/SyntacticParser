import Corpus.Sentence;
import ParseTree.ParseTree;
import ParseTree.TreeBank;
import ProbabilisticContextFreeGrammar.ProbabilisticContextFreeGrammar;
import ProbabilisticParser.ProbabilisticCYKParser;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProbabilisticContextFreeGrammarTest {

    @Test
    public void testPCFG() {
        TreeBank treeBank = new TreeBank(new File("trees"));
        ProbabilisticContextFreeGrammar pcfg = new ProbabilisticContextFreeGrammar(treeBank, 1);
        ProbabilisticContextFreeGrammar pcfg2 = new ProbabilisticContextFreeGrammar("rule-pcfg.txt", "dictionary-pcfg.txt");
        assertEquals(pcfg.size(), pcfg2.size());
        TreeBank treeBank2 = new TreeBank(new File("trees2"));
        ProbabilisticContextFreeGrammar pcfg3 = new ProbabilisticContextFreeGrammar(treeBank2, 1);
    }

    @Test
    public void testPCFGParser() {
        ProbabilisticCYKParser probabilisticCYKParser = new ProbabilisticCYKParser();
        TreeBank treeBank = new TreeBank(new File("trees"));
        ProbabilisticContextFreeGrammar pcfg = new ProbabilisticContextFreeGrammar(treeBank, 1);
        pcfg.convertToChomskyNormalForm();
        Sentence sentence = new Sentence("yeni Büyük yasada karmaşık dil savaşı bulandırmıştır .");
        ArrayList<ParseTree> parses1 = probabilisticCYKParser.parse(pcfg, sentence);
        assertEquals(parses1.size(), 1);
        TreeBank treeBank2 = new TreeBank(new File("trees2"));
        ProbabilisticContextFreeGrammar pcfg2 = new ProbabilisticContextFreeGrammar(treeBank2, 1);
        pcfg2.convertToChomskyNormalForm();
        Sentence sentence2 = new Sentence("yeni büyük yasa karmaşık dil savaş bulan .");
        ArrayList<ParseTree> parses2 = probabilisticCYKParser.parse(pcfg2, sentence2);
        assertEquals(parses2.size(), 1);
    }

}

import ParseTree.TreeBank;
import ProbabilisticContextFreeGrammar.ProbabilisticContextFreeGrammar;
import org.junit.Test;

import java.io.File;
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

}

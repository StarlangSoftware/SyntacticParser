import ParseTree.TreeBank;
import ProbabilisticContextFreeGrammar.ProbabilisticContextFreeGrammar;
import org.junit.Test;

import java.io.File;

public class ProbabilisticContextFreeGrammarTest {

    @Test
    public void testPCFG() {
        TreeBank treeBank = new TreeBank(new File("trees"));
        ProbabilisticContextFreeGrammar cfg = new ProbabilisticContextFreeGrammar(treeBank, 1);
        TreeBank treeBank2 = new TreeBank(new File("trees2"));
        ProbabilisticContextFreeGrammar cfg2 = new ProbabilisticContextFreeGrammar(treeBank2, 1);
    }

}

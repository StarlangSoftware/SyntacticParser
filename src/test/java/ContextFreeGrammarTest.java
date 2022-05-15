import ContextFreeGrammar.ContextFreeGrammar;
import ParseTree.TreeBank;
import org.junit.Test;

import java.io.File;

public class ContextFreeGrammarTest {

    @Test
    public void testCFG() {
        TreeBank treeBank = new TreeBank(new File("trees"));
        ContextFreeGrammar cfg = new ContextFreeGrammar(treeBank, 1);
        TreeBank treeBank2 = new TreeBank(new File("trees2"));
        ContextFreeGrammar cfg2 = new ContextFreeGrammar(treeBank2, 1);
    }
}

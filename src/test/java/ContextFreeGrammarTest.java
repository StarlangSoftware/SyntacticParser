import ContextFreeGrammar.ContextFreeGrammar;
import ParseTree.TreeBank;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ContextFreeGrammarTest {

    @Test
    public void testCFG() {
        TreeBank treeBank = new TreeBank(new File("trees"));
        ContextFreeGrammar cfg = new ContextFreeGrammar(treeBank, 1);
        ContextFreeGrammar cfg2 = new ContextFreeGrammar("rule-cfg.txt", "dictionary-cfg.txt", 1);
        assertEquals(cfg.size(), cfg2.size());
        TreeBank treeBank2 = new TreeBank(new File("trees2"));
        ContextFreeGrammar cfg3 = new ContextFreeGrammar(treeBank2, 1);
    }
}

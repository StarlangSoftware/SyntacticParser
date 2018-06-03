package SyntacticParser;

import ContextFreeGrammar.ContextFreeGrammar;
import Corpus.Corpus;
import Corpus.TurkishChecker;
import ParseTree.ParseTree;

import java.util.ArrayList;

public class TestSyntacticParser {

    public static void main(String[] args){
        boolean earley = false;
        int dataset = 3;
        Corpus corpus;
        ContextFreeGrammar cfg;
        SyntacticParser parser;
        if (earley){
            switch (dataset){
                case 1:
                default:
                    cfg = new ContextFreeGrammar("Data/Cfg/cfg.txt");
                    corpus = new Corpus("sentences1.txt", new TurkishChecker());
                    break;
                case 2:
                    cfg = new ContextFreeGrammar("Data/Cfg/rules-train.txt");
                    corpus = new Corpus("sentences.txt", new TurkishChecker());
                    break;
                case 3:
                    cfg = new ContextFreeGrammar("Data/Cfg/rules-development.txt");
                    corpus = new Corpus("sentences.txt", new TurkishChecker());
                    break;
            }
            parser = new EarleyParser();
        } else {
            switch (dataset){
                case 1:
                default:
                    cfg = new ContextFreeGrammar("Data/Cfg/cfg.txt");
                    cfg.convertToChomskyNormalForm();
                    corpus = new Corpus("sentences1.txt", new TurkishChecker());
                    break;
                case 2:
                    cfg = new ContextFreeGrammar("Data/Cfg/rules-cnf-train.txt");
                    corpus = new Corpus("sentences.txt", new TurkishChecker());
                    break;
                case 3:
                    cfg = new ContextFreeGrammar("Data/Cfg/rules-cnf-development.txt");
                    corpus = new Corpus("sentences.txt", new TurkishChecker());
                    break;
            }
            parser = new CYKParser();
        }
        System.out.println("CFG read");
        for (int i = 0; i < corpus.sentenceCount(); i++){
            ArrayList<ParseTree> parseTrees = parser.parse(cfg, corpus.getSentence(i));
            for (ParseTree parseTree:parseTrees){
                System.out.println(parseTree.toString());
            }
        }
    }

}

package ProbabilisticParser;

import Corpus.Corpus;
import ParseTree.ParseTree;
import ProbabilisticContextFreeGrammar.ProbabilisticContextFreeGrammar;

import java.util.ArrayList;

public class TestProbabilisticParser {

    public static void main(String[] args){
        boolean earley = false;
        int dataset = 3;
        Corpus corpus;
        ProbabilisticContextFreeGrammar pcfg;
        ProbabilisticParser parser;
        if (earley){
            switch (dataset){
                default:
                case 1:
                    pcfg = new ProbabilisticContextFreeGrammar("Data/Cfg/pcfg.txt");
                    corpus = new Corpus("sentences1.txt");
                    break;
                case 2:
                    pcfg = new ProbabilisticContextFreeGrammar("Data/Cfg/probabilistic-train.txt");
                    corpus = new Corpus("sentences.txt");
                    break;
                case 3:
                    pcfg = new ProbabilisticContextFreeGrammar("Data/Cfg/probabilistic-development.txt");
                    corpus = new Corpus("sentences.txt");
                    break;
            }
            parser = new ProbabilisticEarleyParser();
        } else {
            switch (dataset){
                default:
                case 1:
                    pcfg = new ProbabilisticContextFreeGrammar("Data/Cfg/pcfg.txt");
                    pcfg.convertToChomskyNormalForm();
                    corpus = new Corpus("sentences1.txt");
                    break;
                case 2:
                    pcfg = new ProbabilisticContextFreeGrammar("Data/Cfg/probabilistic-cnf-train.txt");
                    corpus = new Corpus("sentences.txt");
                    break;
                case 3:
                    pcfg = new ProbabilisticContextFreeGrammar("Data/Cfg/probabilistic-cnf-development.txt");
                    corpus = new Corpus("sentences.txt");
                    break;
            }
            parser = new ProbabilisticCYKParser();
        }
        System.out.println("PCFG read");
        for (int i = 0; i < corpus.sentenceCount(); i++){
            ArrayList<ParseTree> parseTrees = parser.parse(pcfg, corpus.getSentence(i));
            if (parseTrees.size() > 0){
                System.out.println(parseTrees.get(0).toString());
            }
        }
    }

}

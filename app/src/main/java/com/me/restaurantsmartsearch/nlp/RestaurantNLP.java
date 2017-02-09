package com.me.restaurantsmartsearch.nlp;

import android.content.res.AssetManager;

import com.me.restaurantsmartsearch.activity.MainActivity;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

/**
 * Created by SinhBlack on 2/9/2017.
 */

public class RestaurantNLP {

    public static NameFinderME nameFinder;
    public static DocumentCategorizerME categorizer;

    public static void init(MainActivity mainActivity){
        AssetManager am = mainActivity.getAssets();

        try{
            nameFinder = new NameFinderME(new TokenNameFinderModel(am.open("restaurants-ner.bin")));
            categorizer = new DocumentCategorizerME(new DoccatModel(am.open("restaurants-dc.bin")));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static String[][] getNamesInQuery(String query){
        String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(query);
        Span[] spans = nameFinder.find(tokens);
        String[] names = Span.spansToStrings(spans, tokens);
        int len = spans.length;
        String[][] result = new String[len][2];
        for (int i=0; i<spans.length; i++){
            result[i][0] = spans[i].getType();
            result[i][1] = names[i];
        }
        return result;
    }

    public static String getCategory(String query){
        double[] dcOutComes = categorizer.categorize(query);
        return categorizer.getBestCategory(dcOutComes);
    }
}

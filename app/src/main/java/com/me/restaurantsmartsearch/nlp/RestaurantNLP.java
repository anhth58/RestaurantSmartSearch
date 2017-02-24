package com.me.restaurantsmartsearch.nlp;

import android.content.res.AssetManager;

import com.me.restaurantsmartsearch.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            nameFinder = new NameFinderME(new TokenNameFinderModel(am.open("restaurants-ner-v2.bin")));
            categorizer = new DocumentCategorizerME(new DoccatModel(am.open("restaurants-dc-v2.bin")));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static ContextNLP query(String query){
        Set<String> categoriesIn = getCategory(query);
        HashMap<String, String> names = getNamesInQuery(query);
        return new ContextNLP(categoriesIn, names, query);
    }

    public static HashMap<String, String> getNamesInQuery(String query){
        String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(query);
        Span[] spans = nameFinder.find(tokens);
        String[] names = Span.spansToStrings(spans, tokens);
        HashMap<String, String> result = new HashMap<>();
        for (int i=0; i<spans.length; i++){
            result.put(spans[i].getType(), names[i]);
        }
        return result;
    }

    public static Set<String> getCategory(String query){
//        double[] dcOutComes = categorizer.categorize(query);
        Map<String, Double> scoreMap = categorizer.scoreMap(query);
        Set<String> categoriesIn = new HashSet<>();
        for (Map.Entry<String, Double> entry : scoreMap.entrySet())
        {
            if (entry.getValue() > 0.1) categoriesIn.add(entry.getKey());
        }
        return  categoriesIn;
    }
}

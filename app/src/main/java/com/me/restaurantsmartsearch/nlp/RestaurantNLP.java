package com.me.restaurantsmartsearch.nlp;

import android.content.res.AssetManager;
import android.util.Log;

import com.me.restaurantsmartsearch.activity.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
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
    private static int NUM_CATEGORY = 4;
    private static float threshouldScore = 0.1f;
    private static Set<String> nearHereKeyWords;
    private static Set<String> nearByKeyWords;
    private static Set<String> searchVerbKeyWords;
    private static Set<String> cheapKeyWords;

    private static AbstractSequenceClassifier classifier;

    public static void init(MainActivity mainActivity){
        AssetManager am = mainActivity.getAssets();

        try{
            //nameFinder = new NameFinderME(new TokenNameFinderModel(am.open("restaurants-ner-v3.bin")));
           // categorizer = new DocumentCategorizerME(new DoccatModel(am.open("restaurants-dc-v2.bin")));
            classifier = CRFClassifier.getClassifier(am.open("uet-search-ner-new-v2"));
           /* nearHereKeyWords = getKeyWordsSet(am.open("key_words_near_here.txt"));
            nearByKeyWords = getKeyWordsSet(am.open("key_words_near_by.txt"));
            searchVerbKeyWords = getKeyWordsSet(am.open("key_words_search_verbs.txt"));
            cheapKeyWords = getKeyWordsSet(am.open("key_words_cheap.txt"));*/

        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
    public static HashMap<String, String> getHashMapFromStanford(String result){
        HashMap<String, String> hashMap = new HashMap<>();
        String [] tokens = result.split(" ");
        for (String token : tokens){
            String[] parts = token.split("/");
            if (parts.length >= 2) {
                if (!parts[1].equals("O")) {
                    if (hashMap.containsKey(parts[1])) {
                        String old = hashMap.get(parts[1]);
                        hashMap.put(parts[1], old + " " + parts[0]);
                    } else {
                        hashMap.put(parts[1], parts[0]);
                    }
                }
            }
        }
        return hashMap;
    }
    public static Set<String> getKeyWordsSet(InputStream file){
        Set<String> keywords = new LinkedHashSet<>();
        String line;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                keywords.add(new String(line.getBytes("UTF-8"), "UTF-8"));
            }
            reader.close();
        }catch (Exception e){
            System.out.print(e.toString());
        }
        return keywords;
    }

    static public boolean containsKeyWord(Set<String> keywords, String query){
        for (String keyword : keywords){
            if (query.contains(keyword)){
                return true;
            }
        }
        return false;
    }

    static public String getContainedKeyWord(Set<String> keywords, String query){
        for (String keyword : keywords){
            if (query.contains(keyword)) return keyword;
        }
        return null;
    }

    static public ContextNLP isFollowingRules(String query){
        boolean isFollwingRulesFlag=false;
        String name="", address="", type="", nearLocation="";
        if (containsKeyWord(nearHereKeyWords, query)){
            String keyword = getContainedKeyWord(nearHereKeyWords, query);
            if (keyword != null){
                int index = query.indexOf(keyword);
                nearLocation = "here";
                name = query.substring(0, index);
            }
            isFollwingRulesFlag = true;
        }else if(containsKeyWord(nearByKeyWords, query)){
            String keyword = getContainedKeyWord(nearByKeyWords, query);
            if (keyword != null){
                int index = query.indexOf(keyword);
                nearLocation = query.substring(index+keyword.length());
                name = query.substring(0, index);
            }
            Log.d("keyword nearby: ", keyword);
            isFollwingRulesFlag = true;
        }else if(containsKeyWord(cheapKeyWords, query)){
            name = query;
            isFollwingRulesFlag = true;
        }

        if(isFollwingRulesFlag){
            if(containsKeyWord(searchVerbKeyWords, name)){
                String keyword = getContainedKeyWord(searchVerbKeyWords, name);
                if(keyword != null) name = name.replace(keyword, "");
            }
            if(containsKeyWord(cheapKeyWords, name)){
                String keyword = getContainedKeyWord(cheapKeyWords, name);
                if(keyword != null) name = name.replace(keyword, "");
                type = "Sinh viên";
            }
            if(containsKeyWord(cheapKeyWords, nearLocation)){
                String keyword = getContainedKeyWord(cheapKeyWords, nearLocation);
                if(keyword != null) nearLocation = name.replace(keyword, "");
                type = "Sinh viên";
            }
            return new ContextNLP(type, name, address, nearLocation);
        }

        return null;
    }

    public static ContextNLP query(String query){
        query = query.toLowerCase();
        String nquery = query;
        try{
            nquery = new String(query.getBytes("UTF-8"), "UTF-8");
        }catch (Exception e){
            Log.d("Exception: ", e.toString());
        }
       /* ContextNLP context = isFollowingRules(nquery);
        if (context == null){
            Set<String> categoriesIn = getCategory(nquery);
            HashMap<String, String> names = getNamesInQuery(nquery);
            context = new ContextNLP(categoriesIn, names, nquery);
        }*/
        return new ContextNLP(getHashMapFromStanford(classifier.classifyToString(nquery)), nquery);
       // return context;
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
        System.out.print(scoreMap);
        Set<String> categoriesIn = new HashSet<>();
        int countCategoryIn = 0;
        System.out.println(threshouldScore + "--");
        for (Map.Entry<String, Double> entry : scoreMap.entrySet())
        {
            if (entry.getValue() >= threshouldScore) countCategoryIn++;
            if (entry.getValue() >= 0.1) categoriesIn.add(entry.getKey());
        }
        System.out.println(countCategoryIn);
        if (countCategoryIn >= 4) categoriesIn.clear();
        return  categoriesIn;
    }
}

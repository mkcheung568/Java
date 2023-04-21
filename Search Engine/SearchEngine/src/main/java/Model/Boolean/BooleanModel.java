package Model.Boolean;

import Data.Corpus;
import Data.SearchResult;
import Data.WordMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BooleanModel {
    private Corpus corpus;
    private int queryLength;
    private List<WordMetadata> queryResults2 = new ArrayList<WordMetadata>();
    private Map<Integer, Integer> counter2 = new HashMap<Integer, Integer>();
    private Map<Integer, Double> resultForAllIdScore = new HashMap<Integer, Double>();
    private ArrayList<SearchResult> resultForTop1000IdScore = new ArrayList<SearchResult>();
    private int resultCounter = 0;

    public BooleanModel(Corpus corpus){
        this.corpus = corpus;
    }

    public SearchResult[] getResult(ArrayList<String> querys){
        System.out.println("[getRusult] " + querys + ", size is " + querys.size());
        queryLength = querys.size();

        for(int i = 0;i<querys.size();i++){
            //System.out.println("[getRusult] " + querys.get(i));
            getResultInMapByTerm(querys.get(i));
        }
        counterOfQuery2();
        calScore();
        getTop1000Result();
        printTop1000ScoreResult();

        SearchResult[] topResults = new SearchResult[resultCounter];
        for(int i = 0;i<resultCounter;i++){
            topResults[i] = resultForTop1000IdScore.get(i);
        }
        return topResults;
    }

    private void getResultInMapByTerm(String term){
        // get the result by term and put into the map
        List<WordMetadata> results = corpus.getInvertedIndex().get(term);
        try{
            System.out.println("[getResultByTerm] " + term + "'s result loaded");
            for (WordMetadata result : results)
                queryResults2.add(result);
            //queryResults.add(queryResult.fileID + " " + queryResult.occurence + " " + queryResult.wordPositions);
            //System.out.println(queryResults2.fileID + " " + queryResults2.occurence + " " + queryResults2.wordPositions);
        } catch (Exception e){
            System.out.println("[Error - getResultByTerm] The inverted file no this keyword --> " + term);
        }
    }

    private void counterOfQuery2(){
        // make the counter2 for the queryResults to count how many queryResults is same in the WordMetadata object
        for (WordMetadata queryResult : queryResults2){
            Integer count = counter2.get(queryResult.fileID);
            if (count == null){
                count = 0;
            }
            counter2.put(queryResult.fileID, count + 1);
        }
    }

    private void calScore(){
        for (Map.Entry<Integer, Integer> entry : counter2.entrySet()){
            int docId = entry.getKey();
            int count = entry.getValue();
            double score = (double) count / queryLength;
            resultForAllIdScore.put(docId, score);
        }
    }

    private void getTop1000Result(){
        // get the top 1000 result in the resultForAllIdScore, key for results.fileID , value for score
        int counter = 0;
        for (Map.Entry<Integer, Double> entry : resultForAllIdScore.entrySet()){
            int docId = entry.getKey();
            double score = entry.getValue();
            if (counter < 1000){
                resultForTop1000IdScore.add(new SearchResult(docId, score));
                counter++;
            }else{
                break;
            }
        }
        resultCounter = counter;
    }

    // print the result method, no need explain

    private void printFileResult(){
        // print the result
        System.out.println();
        System.out.println("[printResult] The result is ");
        for (WordMetadata result : queryResults2){
            System.out.println("fileID : " + result.fileID + "; occurence : " + result.occurence + "; wordPositions : " + result.wordPositions);
        }
    }

    private void printScoreResult(){
        // print the result
        System.out.println();
        System.out.println("[printResult] The result is ");
        int index = 1;
        for (Map.Entry<Integer, Double> entry : resultForAllIdScore.entrySet()){
            System.out.println("["+index++ +"] fileID : " + entry.getKey() + "; score : " + entry.getValue());
        }
    }

    private void printTop1000ScoreResult(){
        // print the result
        System.out.println();
        System.out.println("[printResult] The result is ");
        int index = 1;
        for (SearchResult result : resultForTop1000IdScore){
            System.out.println("["+index++ +"] fileID : " + result.fileID + "; score : " + result.score);
        }
    }

}

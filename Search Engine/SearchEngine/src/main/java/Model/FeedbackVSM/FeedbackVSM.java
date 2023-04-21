package Model.FeedbackVSM;

import Data.Corpus;
import Data.Document;
import Data.SearchResult;
import Data.WordMetadata;
import Model.VectorSpace.VectorSpaceModel;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * VSM with Pseudo Relevance Feedback
 */
public class FeedbackVSM {

    private final Corpus corpus;

    private int N=0;
    public FeedbackVSM(Corpus corpus) {
        this.corpus = corpus;
        this.N = corpus.getDocuments().size();
    }

    public ArrayList<String> getFeedback(SearchResult[] results, ArrayList<String> query){
        int counter=0;
        double idf=0;
        int df=0;

        for(int i=0;i<query.size();i++){
            String term = query.get(i);
            if(corpus.getInvertedIndex().containsKey(term)) {
                df = corpus.getInvertedIndex().get(term).size();
                if(df>0.1*N) query.remove(term);
            }
        }
        for(int i=0;i<1;i++){
            HashMap<String, Double> terms = new HashMap<>();
            int fileID = results[i].getFileID();
            for (Map.Entry<String, List<WordMetadata>> pair : corpus.getInvertedIndex().entrySet()) {
                for (WordMetadata o : pair.getValue()) {
                    if (o.fileID == fileID)
                        idf = corpus.getInverseDocumentFrequency(pair.getKey());
                        terms.put(pair.getKey(), o.occurence*idf);
                }
            }
            HashMap<String, Double> topTerms = (HashMap<String, Double>) sortByValue(terms);
            Iterator<Map.Entry<String, Double>> it = topTerms.entrySet().iterator();
            while(it.hasNext()&&counter<=3){
                Map.Entry<String, Double> pair = it.next();
                if(!query.contains(pair.getKey())){
                    query.add(pair.getKey());
                    counter++;
                }
            }
        }
        for(int i=0;i<query.size();i++){
            String term = query.get(i);
            if(corpus.getInvertedIndex().containsKey(term)) {
                df = corpus.getInvertedIndex().get(term).size();
                if(df>0.1*N) query.remove(term);
            }
        }
        return query;
    }
    /**
     * Returns top 1000 search results for creating TREC Search Result
     */
    public SearchResult[] getTopSearchResults(ArrayList<String> query){
        SearchResult[] results, resultsWithFeedback;
        ArrayList<String> queryWithFeedback;

        results = getTopSearchResults1(query);

        queryWithFeedback = getFeedback(results, query);

        resultsWithFeedback = getTopSearchResults1(queryWithFeedback);

        return resultsWithFeedback;
    }

    public static <K, V extends Comparable<? super V>>
    Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
        ));
    }

    /**
     * The HashMap maps an fileID integer to a score Double
     */
    private HashMap<Integer,Double> getTermDotProductRanking(String term){
        HashMap<Integer,Double> fileIDDotProductMap= new HashMap<>();

        if(!corpus.getInvertedIndex().containsKey(term)){
            return new HashMap<>();
        }

        corpus.getInvertedIndex().get(term).forEach((wordMetadataIn)->{
            //For debug purpose, cant see variables inside a lambda
            //WordMetadata wordMetadata = wordMetadataIn;

            double n = corpus.getInvertedIndex().get(term).size();
            double doc = Math.log((N-n+0.5)/(n+0.5)+1);
            fileIDDotProductMap.put(wordMetadataIn.fileID,Math.pow(wordMetadataIn.occurence,2)*doc);
        });

        return fileIDDotProductMap;
    }

    /**
     * The HashMap maps an fileID integer to a score Double
     */
    public ArrayList<SearchResult> getSearchResults(ArrayList<String> query){
        HashMap<Integer,Double> resultDotProductRanking = new HashMap<>();


        //merge the FileID:Score maps together(add the scores of the same document together)
        for(String word : query){
            HashMap<Integer,Double> termDotProductRanking = getTermDotProductRanking(word);

            for (Map.Entry<Integer, Double> e : termDotProductRanking.entrySet()){
                resultDotProductRanking.merge(e.getKey(),e.getValue(),(oldValue,newValue)-> oldValue+newValue);
            }
        }

        //normalize by dividing Document length and query length to get cosine similarity score
        Iterator<Map.Entry <Integer, Double> > it = resultDotProductRanking.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Double> pair = it.next();

            //result from dot product/ document length / sqrt(query length)
            pair.setValue(pair.getValue() / corpus.getDocumentLength().get(pair.getKey()) / Math.sqrt(Math.pow(query.size(),2)));
        }

        //final result after normalize, now sorting
        ArrayList<SearchResult> sortedResults = new ArrayList<SearchResult>();

        //first put it into an Arraylist
        for (Map.Entry<Integer, Double> e : resultDotProductRanking.entrySet()){
            sortedResults.add(new SearchResult(e.getKey(),e.getValue()));
        }

        //Then sort and return
        sortedResults.sort(Comparator.comparingDouble(searchResult -> searchResult.score));
        Collections.reverse(sortedResults);

        return sortedResults;
    }

    /**
     * Returns top 1000 search results for creating TREC Search Result
     */
    public SearchResult[] getTopSearchResults1(ArrayList<String> query){
        List<SearchResult> results = getSearchResults(query);

        int limit = 0;

        if(results.size()<1000){
            limit = results.size();
        }else{
            limit = 1000;
        }

        SearchResult[] topResults = new SearchResult[limit];

        for(int i=0;i<limit;i++){
            topResults[i] = results.get(i);
        }

        return topResults;
    }



}

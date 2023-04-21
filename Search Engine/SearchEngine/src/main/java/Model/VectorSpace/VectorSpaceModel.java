package Model.VectorSpace;

import Data.Corpus;
import Data.SearchResult;
import Data.WordMetadata;

import java.util.*;


public class VectorSpaceModel {

    private Corpus corpus;

    public VectorSpaceModel(Corpus corpus) {
        this.corpus = corpus;
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
    public SearchResult[] getTopSearchResults(ArrayList<String> query){
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
            WordMetadata wordMetadata = wordMetadataIn;
            double idf = corpus.getInverseDocumentFrequency(term);
            fileIDDotProductMap.put(wordMetadata.fileID,wordMetadata.occurence*idf);
        });

        return fileIDDotProductMap;
    }

}

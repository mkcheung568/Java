package util;

import Data.Corpus;
import Data.SearchResult;

public class TRECResultGenerator {

    private String queryID;
    private final String runID;

    private int counter = 0;


    /**
     * Each Model should only create this object once, this generator will generate all the TREC Search Result texts base on your
     * {@link SearchResult} array, call {@link TRECResultGenerator#configure} method in the same class to
     * change it to a new queryID to continue using this object
     *
     * @param runID the HKPU-1 text at the end of a TREC Search Result in the readme file, each model should have a unique one
     */
    public TRECResultGenerator(String runID){
        this.runID = runID;
    }

    /**
     * After you got the TREC results from a queryID, call this and it will reset the generator to generate TREC results for another queryID
     * @param queryID The new Query ID you want the generator to generate TREC Results for
     */
    public void configure(String queryID){
        this.queryID = queryID;
        this.counter = 0;
    }


    /**
     * Call this to generate TREC result text for the {@link SearchResult} array, the string have new line symbols in them ready for
     * directly printing them to a file or console
     * @param results search result array for a queryID
     * @param corpus a reference to the corpus object in main
     * @return a string with all the TREC Search Results ready to be directly printed to file or console
     */
    public String generateResultFileText(SearchResult[] results, Corpus corpus){
        String toReturn = "";

        for(SearchResult result: results){
            counter++;
            toReturn+=queryID+" "+"Q0"+" "+corpus.getTRECFileID(result.fileID)+" "+counter+" "+result.score+" "+runID+"\n";
        }

        return toReturn;
    }
}

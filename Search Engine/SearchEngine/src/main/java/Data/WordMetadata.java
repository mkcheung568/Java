package Data;

import java.util.ArrayList;

public class WordMetadata {
    public int fileID;
    public int occurence;
    public ArrayList<Integer> wordPositions;

    public WordMetadata(int fileID, int occurence, ArrayList<Integer> wordPositions){
        this.fileID = fileID;
        this.occurence = occurence;
        this.wordPositions = wordPositions;
    }
}

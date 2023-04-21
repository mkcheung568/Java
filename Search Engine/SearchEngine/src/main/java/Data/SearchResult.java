package Data;

public class SearchResult {
    public int fileID;
    public double score;

    public SearchResult(int fileID, double score){
        this.fileID = fileID;
        this.score = score;
    }

    public int getFileID() {
        return fileID;
    }

    public double getScore() {
        return score;
    }
}

package Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * you can get term frequency from this class
 */

public class Document{

    private int fileID; //Document ID/File ID - Actual order of how the file information are listed in file.txt
    public int docLength;//Document Word Count
    private String docID;// Actual Record ID for TREC Programs
    private String path;// Path to the file for TREC Programs

    public Document(int fileID, int docLength, String docID, String path){
        this.fileID = fileID;
        this.docLength = docLength;
        this.docID = docID;
        this.path = path;
    }


    public int getFileID(){
        return this.fileID;
    }
    public int getWordCount(){
        return this.docLength;
    }
    public String getTRECDocID(){
        return this.docID;
    }
    public String getTRECFilePath(){
        return this.path;
    }



    @Override
    public int hashCode() {
        return Objects.hash(fileID);
    }

    /*Not sure if it is needed later, keep it for now
    @Override
    public int compareTo(Document o) {
        if(fileID == o.getFileID()){
            return 0;
        }else if(fileID < o.fileID){
            return -1;
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof Document){
            Document document = (Document) obj;
            return this.fileID == document.getFileID();
        }
        return false;
    }
    */
}

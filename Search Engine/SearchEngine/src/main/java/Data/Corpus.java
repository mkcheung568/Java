package Data;


import java.io.*;
import java.util.*;


public class Corpus {

    /**
     * A HashMap that maps fileID Integer to TREC document data Document.
     */
    private HashMap<Integer, Document> fileID_DocumentMap;

    /**
     * The inverted file.
     * It will map a term(String) to a list of WordMetadata, see class {@link Data.WordMetadata}
     */
    private HashMap<String, List<WordMetadata>> invertedIndex;

    /**
     * A HashMap that maps fileID Integer to Document Length Double (not word count)
     */
    private HashMap<Integer,Double> documentLengths;

    public Corpus(){
        this.fileID_DocumentMap = parseFileTxt();
        this.invertedIndex = new HashMap<>();
        this.documentLengths = new HashMap<>();

        initInvertedIndex();
        //TODO: save document length to a file as well
    }

    /**
     * Parse file.txt in the resource directory.
     * Construct the fileId_DocumentMap
     */
    private HashMap<Integer,Document> parseFileTxt(){
        HashMap<Integer,Document> fileId_DocumentMap = new HashMap<>();

        System.out.println("[Corpus] Parsing file.txt... ");
        try(BufferedReader br = new BufferedReader(new FileReader(Resources.filePath))){

            String str;
            while((str = br.readLine()) != null){

                String[] strs = str.split(" ");

                int fileID = Integer.parseInt(strs[0]);
                int docLength = Integer.parseInt(strs[1]);
                String docID = strs[3];
                String path = strs[4];

                Document document = new Document(fileID, docLength, docID, path);
                fileId_DocumentMap.put(fileID, document);
            }
            return fileId_DocumentMap;

        } catch (FileNotFoundException e) {
            System.err.println("Cannot find the file.txt \n" +
                    "please check if you have put the file.txt in the resources directory");
        } catch (IOException e) {
            System.err.println("Errors occur when reading the file.txt ");
        }

        return null;
    }


    /**
     * Initialize inverted index, first try to read one, if it doesnt exist it will build one
     */
    private void initInvertedIndex(){
        System.out.println("[Corpus] Initializing the inverted index... ");
        //read the invertedIndex file, if not we build it

        File file = new File(Resources.invertedFilePath);

        if(file.exists() && !file.isDirectory()) {
            System.out.println("[Corpus] Found Existing Inverted Index, Reading... This could take a while too ");
            invertedIndex = readInvertedFile(file);
        }else{
            System.out.println("[Corpus] Unable to find existing Inverted Index, Building.... ");

            invertedIndex = PostTextPreprocessor.preprocessPostTxt(Resources.postPath);

            System.out.println("[Corpus] Writing inverted index to file...");
            PostTextPreprocessor.wordDictToFile(invertedIndex);

        }

        System.out.println("[Corpus] calculating document lengths and writing to file... ");
        //no file writing implemented yet
        calculateDocumentLength();
    }


    //Document Length (the sqrt(TF-IDF^2) one not word count)
    private void calculateDocumentLength(){

        for (Map.Entry<String, List<WordMetadata>> e : invertedIndex.entrySet()){
            for(WordMetadata wordMetadata: e.getValue()){
                documentLengths.merge(wordMetadata.fileID,
                        Math.pow(wordMetadata.occurence*getInverseDocumentFrequency(e.getKey()),2),
                        (oldValue,newValue)-> oldValue+newValue);
            }
        }

        //TODO: for now it works, but to reduce computation time we should write it to a file and read from it instead
        //write to file
    }


    //

    /**
     * Note: Document Length (the sqrt(TF-IDF^2) one not word count)
     *
     * @return A HashMap that maps a fileID Integer to a document length Double
     */
    public HashMap<Integer,Double> getDocumentLength(){
        //TODO: for now it works, but to reduce computation time we should write it to a file and read from it instead
        //ideally we read document length file here
        return documentLengths;
    }

    //IDF
    public double getInverseDocumentFrequency(String term){
        if(invertedIndex.containsKey(term)){
            double size = fileID_DocumentMap.size();
            double documentFrequency = invertedIndex.get(term).size();

            return Math.log10(size/documentFrequency);
        }

        return 0;
    }

    /**
     * @return all the documents
     */
    public ArrayList<Document> getDocuments(){
        ArrayList<Document> list = new ArrayList<>(fileID_DocumentMap.values());
        return list;
    }

    public Document getDocumentByFileID(int id){
        return fileID_DocumentMap.get(id);
    }

    public HashMap<String, List<WordMetadata>> getInvertedIndex(){
        return this.invertedIndex;
    }

    /* Technically it is already in the inverted file, but there are variations of TF that needs computation, keeping it for now
    //TF
    public double getTermFrequency(String term,int fileID){
        return invertedIndex.get(term).get(fileID).occurence;
    }
    */

    public HashMap<String, List<WordMetadata>> readInvertedFile(File file){
        HashMap<String, List<WordMetadata>> toReturn = new HashMap<>();

        Scanner myReader = null;
        try {
            int count = 0;
            myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String term;

                ArrayList<WordMetadata> wordMetadata = new ArrayList<>();

                String[] split1 = data.split(":");
                term = split1[0];
                //:1/7/57,71,125,168,183,188,251:
                for(int i=1;i<split1.length;i++){
                    //1 7 57,71,125,168,183,188,251
                    String[] split2 = split1[i].split("/");

                    int fileID = Integer.parseInt(split2[0]);
                    int occurrence = Integer.parseInt(split2[1]);

                    //57,71,125,168,183,188,251
                    String[] split3 = split2[2].split(",");
                    ArrayList<Integer> wordPositions = new ArrayList<>();

                    for(int j=0;j<split3.length;j++){
                        wordPositions.add(Integer.parseInt(split3[j]));
                    }
                    wordMetadata.add(new WordMetadata(fileID,occurrence,wordPositions));
                }
                toReturn.put(term,wordMetadata);

                count++;
                if (count % 50000 == 0){
                    System.out.println("[Corpus] Loaded 50,000 words into memory...");
                }
            }
            myReader.close();
            System.out.printf("[Corpus] Loaded a total of %d words into memory...\n",count);
            return toReturn;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // one optional term frequency normalization method, TF(i,j) = F(i,j) / sum(i){F(i,j)}
    public HashMap<String, Integer> sumTermFrequencyNormalization()
    {
        HashMap<String, Integer> sumTermFrequency = new HashMap<>();
        for (Map.Entry<String, List<WordMetadata>> e : invertedIndex.entrySet()){
            int sum=0;
            for(WordMetadata wordMetadata: e.getValue()){
                sum+=wordMetadata.occurence;
            }
            sumTermFrequency.put(e.getKey(),sum);
        }
        return sumTermFrequency;
    }

    // one optional term frequency normalization method, TF(i,j) = F(i,j) / max(i){F(i,j)}
    public HashMap<String, Integer> maxTermFrequencyNormalization()
    {
        HashMap<String, Integer> maxTermFrequency = new HashMap<>();
        for (Map.Entry<String, List<WordMetadata>> e : invertedIndex.entrySet()){
            int max=0;
            for(WordMetadata wordMetadata: e.getValue()){
                max=Math.max(wordMetadata.occurence, max);
            }
            maxTermFrequency.put(e.getKey(),max);
        }
        return maxTermFrequency;
    }

    public String getTRECFileID(int fileID){
        return fileID_DocumentMap.get(fileID).getTRECDocID();
    }

}

package Data;

import java.io.*;
import java.util.*;

public class PostTextPreprocessor {
    //Test Code
    public static void main(String[] args){

        final String postPath = "src/main/resources/postTest.txt";

        HashMap<String, List<WordMetadata>> wordDictionary;

        System.out.println("Parsing post1.txt...");

        wordDictionary = preprocessPostTxt(postPath);

        wordDictToFile(wordDictionary);

    }

    public static HashMap<String,List<WordMetadata>> preprocessPostTxt(String postPath){
        try(BufferedReader br = new BufferedReader(new FileReader(postPath))){

            int count = 0;

            System.out.println("[PostTextPreprocessor] Preprocessing start... This could take a while...");

            String str;
            HashMap<String,List<WordMetadata>> wordDictionary = new HashMap<>();

            while((str = br.readLine()) != null){

                String[] strs = str.split(" ");

                //* 1. Words are converted to lower case.
                //* 2. Non-digit and non-letter characters are removed.
                String term = strs[0].replaceAll("[^A-Za-z0-9]", "").toLowerCase();
                int fileID = Integer.parseInt(strs[1]);
                int wordPosition = Integer.parseInt(strs[2]);

                count++;
                if (count == 1000000){
                    count = 0;
                    System.out.println("[PostTextPreprocessor] Preprocessed 1,000,000 words...");
                }


                if(wordDictionary.containsKey(term)){
                    boolean foundFileID = false;

                    Iterator<WordMetadata> iterator = wordDictionary.get(term).iterator();
                    while(iterator.hasNext()) {
                        WordMetadata wordMetadata = iterator.next();

                        if (wordMetadata.fileID == fileID) {

                            wordMetadata.wordPositions.add(wordPosition);
                            wordMetadata.occurence++;
                            foundFileID = true;
                        }
                    }
                    if(!foundFileID){
                        ArrayList<Integer> wordPositions = new ArrayList<>();
                        wordPositions.add(new Integer(wordPosition));
                        wordDictionary.get(term).add(new WordMetadata(fileID,1,wordPositions));
                    }

                }else{
                    List<WordMetadata> list = new ArrayList<WordMetadata>();
                    ArrayList<Integer> wordPositions = new ArrayList<>();
                    wordPositions.add(new Integer(wordPosition));
                    list.add(new WordMetadata(fileID,1,wordPositions));
                    wordDictionary.put(term,list);
                }
            }

            return wordDictionary;

        } catch (FileNotFoundException e) {
            System.err.println("Cannot find the post1.txt \n" +
                    "please check if you have put the post1.txt in the resources directory");

            return null;
        } catch (IOException e) {
            System.err.println("Errors occur when reading the post1.txt ");
            return null;
        }
    }

    public static void wordDictToFile(HashMap<String,List<WordMetadata>> wordDictionary){
        try {
            System.out.println("[PostTextPreprocessor] Writing File... This could also take a while...");
            int count = 0;
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Resources.invertedFilePath)));

            Iterator iterator = wordDictionary.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry)iterator.next();
                String strToWrite = pair.getKey().toString();
                List<WordMetadata> wordMetadataList = (ArrayList<WordMetadata>)pair.getValue();

                for(int i=0;i<wordMetadataList.size();i++){
                    //word:fileID/occurence/(first word position)
                    strToWrite+=":"+ wordMetadataList.get(i).fileID+"/"+wordMetadataList.get(i).occurence+"/"+wordMetadataList.get(i).wordPositions.get(0);
                    //word:fileID/occurence/(first word position),(second word position)...
                    for(int j=1;j<wordMetadataList.get(i).wordPositions.size();j++){
                        strToWrite+=","+wordMetadataList.get(i).wordPositions.get(j);
                    }
                }

                writer.println(strToWrite);

                count++;
                if (count == 1000000){
                    count = 0;
                    System.out.println("[PostTextPreprocessor] Written 1,000,000 words...");
                }
            }
            writer.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


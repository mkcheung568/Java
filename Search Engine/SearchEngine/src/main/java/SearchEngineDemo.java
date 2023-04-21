import Data.Corpus;
import Data.Query;
import Data.Resources;
import Data.SearchResult;
import Model.Boolean.BooleanModel;
import Model.FeedbackVSM.FeedbackVSM;
import Model.FuzzyBoolean.FuzzyBooleanModel;
import Model.VectorSpace.VectorSpaceModel;
import util.QueryUtil;
import util.TRECResultGenerator;

import java.io.*;
import java.util.Scanner;


public class SearchEngineDemo {
    public static Corpus corpus = new Corpus();

    public static void main(String[] args) throws Exception {
        //UI ui = new UI();

        Scanner myObj = new Scanner(System.in);
        String model;
        String queryType;
        String fileName = null;

        while(true) {
            // the command UI
            System.out.println("\n\nPlease enter the model: [01 | 02 | 03 | 04 | quit]");
            System.out.println("01 = boolean Model, 02 = Vector Space Model, 03 = Fuzzy Boolean Model, 04 = Advance Model, quit = exit");
            model = myObj.nextLine();
            if (model.equals("quit")){
                System.out.println("End program");
                System.exit(0);
            }else{
                System.out.println("The model is " + model);
                if (model.equals("01")){
                    fileName = "Bool";
                } else if (model.equals("02")) {
                    fileName = "VSM";
                } else if (model.equals("03")) {
                    fileName = "Fuzzy";
                } else if (model.equals("04")) {
                    fileName = "Adv";
                }
            }

            System.out.println("\nPlease enter the query file: [01 | 02 | quit]");
            System.out.println("01 = queryT (short queries), 02 = queryTDN (long queries), quit = exit");
            queryType = myObj.nextLine();

            if (queryType.equals("quit")){
                System.out.println("End program");
                System.exit(0);
            }else{
                System.out.println("The query file is " + queryType);
            }

            Scanner myReader = null;
            File file;
            PrintWriter vsmWriter;
            switch (queryType) {
                case "01":
                    file = new File(Resources.queryFilePath);

                    //initialize here
                    fileName = "T-"+fileName+".ret";
                    vsmWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
                    break;
                case "02":
                    file = new File(Resources.queryTDNFilePath);

                    //initialize here
                    fileName = "TDN-"+fileName+".ret";
                    vsmWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
                    break;
                default:
                    file = new File(Resources.queryFilePath);
                    //initialize here
                    fileName = "T-"+fileName+".ret";
                    vsmWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
            }
            try {
                myReader = new Scanner(file);
                String inQuery;


                while (myReader.hasNextLine()) {
                    inQuery = myReader.nextLine();

                    String queryID = inQuery.substring(0, 4).trim();
                    String rawQuery = inQuery.substring(4);
                    String processedQuery = String.join(" ", QueryUtil.cleanQuery(rawQuery));
                    //testing
//                System.out.println("Raw Query: " + rawQuery);
//                System.out.println("Processed Query: " + processedQuery);

                    Query currentQuery = new Query(queryID, processedQuery);

                    // for each current query and print the result
                    System.out.println("[Main] Query: " + currentQuery.getQueryID() + " " + currentQuery.getQueryContent());

                    //call your models here
                    System.out.printf("[Main: Vector Space Model] Writing Results for Query %s\n", currentQuery.queryID);
                    TRECResultGenerator generator = new TRECResultGenerator("HKPU-1");
                    SearchResult[] results;

                    switch (model) {
                        //boolean2
                        case "01":
                            BooleanModel booleanModel = new BooleanModel(corpus);
                            results = booleanModel.getResult(currentQuery.getQueryContent());
                            generator.configure(currentQuery.getQueryID());

                            vsmWriter.print(generator.generateResultFileText(results, corpus));
                            break;

                        //fuzzyBoolean
                        case "03":
                            FuzzyBooleanModel fzModel = new FuzzyBooleanModel(corpus);
                            results = fzModel.getTopSearchResults(currentQuery.query);
                            generator.configure(currentQuery.queryID);
                            vsmWriter.print(generator.generateResultFileText(results, corpus));
                            break;

                        // vectorSpace
                        case "02":
                            VectorSpaceModel vsModel = new VectorSpaceModel(corpus);

                            results = vsModel.getTopSearchResults(currentQuery.query);
                            generator.configure(currentQuery.queryID);

                            vsmWriter.print(generator.generateResultFileText(results, corpus));
                            break;

                        // Advance
                        case "04":
                            FeedbackVSM feedbackVSM = new FeedbackVSM(corpus);
                            results = feedbackVSM.getTopSearchResults(currentQuery.query);
                            generator.configure(currentQuery.queryID);

                            vsmWriter.println(generator.generateResultFileText(results, corpus));
                            break;

                        default:
                            System.out.println("[Main] if u see this msg, u must have problem about choose model");
                    }
                    vsmWriter.flush();
                }
                myReader.close();
                System.out.println("The result file was generated in " + fileName);

                //Code after everything is done here
                vsmWriter.close();

            } catch (FileNotFoundException e) {
                System.out.println("Unable to open query file, please change the path in Data/Resources.java");
                throw new RuntimeException(e);
            }
        }
    }
}

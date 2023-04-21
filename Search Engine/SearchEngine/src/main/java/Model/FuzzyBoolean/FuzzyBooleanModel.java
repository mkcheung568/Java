package Model.FuzzyBoolean;

import Data.Corpus;
import Data.Document;
import Data.SearchResult;
import Data.WordMetadata;

import java.util.*;

import static Model.FuzzyBoolean.BooleanOperator.*;

enum BooleanOperator {
    AND, OR, NOT, LP, RP, LITERAL;

    static BooleanOperator parse(String token) {
        switch (token) {
            case "AND": {
                return AND;
            }
            case "OR": {
                return OR;
            }
            case "NOT": {
                return NOT;
            }
            case "(": {
                return LP;
            }
            case ")": {
                return RP;
            }
            default:
                return LITERAL;
        }
    }
}

public final class FuzzyBooleanModel {
    private final Corpus corpus;
    private final int N;
    private final HashMap<Integer, Integer> fileIdToIndex;
    private final Stack<double[]> operandStack;
    private final Stack<BooleanOperator> operatorStack;

    public FuzzyBooleanModel(Corpus corpus) {
        this.corpus = corpus;
        this.N = corpus.getDocuments().size();
        fileIdToIndex = new HashMap<>();
        operandStack = new Stack<>();
        operatorStack = new Stack<>();
        int index = 0;
        for (Document document : corpus.getDocuments()) {
            fileIdToIndex.put(document.getFileID(), index);
            index++;
        }
    }

    public static void main(String[] args) {
        Corpus corpus = new Corpus();
        FuzzyBooleanModel fuzzyBooleanModel = new FuzzyBooleanModel(corpus);
        List<SearchResult> results = fuzzyBooleanModel.processQuery("( particip AND rio ) OR peac");
        System.out.println("Result of 'particip OR rio'");
        for (SearchResult result : results) {
            System.out.println(result.fileID + ": " + result.score);
        }
        results = fuzzyBooleanModel.processQuery("particip");
        System.out.println("Result of 'particip'");
        for (SearchResult result : results) {
            System.out.println(result.fileID + ": " + result.score);
        }
        results = fuzzyBooleanModel.processQuery("rio");
        System.out.println("Result of 'rio'");
        for (SearchResult result : results) {
            System.out.println(result.fileID + ": " + result.score);
        }
        results = fuzzyBooleanModel.processQuery("peac");
        System.out.println("Result of 'peac'");
        for (SearchResult result : results) {
            System.out.println(result.fileID + ": " + result.score);
        }
        results = fuzzyBooleanModel.processQuery("peac");
        System.out.println("Result of 'peac'");
        for (SearchResult result : results) {
            System.out.println(result.fileID + ": " + result.score);
        }
    }

    private void doAND() {
        System.out.println("AND");
        if (operandStack.size() < 2)
            throw new IllegalArgumentException("Illegal query expression!");
        double[] op1 = operandStack.pop();
        double[] op2 = operandStack.pop();
        double[] res = new double[N];
        for (int i = 0; i < N; i++)
            res[i] = Double.min(op1[i], op2[i]);
        operandStack.push(res);
    }

    private void doOR() {
        System.out.println("OR");
        if (operandStack.size() < 2)
            throw new IllegalArgumentException("Illegal query expression!");
        double[] op1 = operandStack.pop();
        double[] op2 = operandStack.pop();
        double[] res = new double[N];
        for (int i = 0; i < N; i++)
            res[i] = Double.max(op1[i], op2[i]);
        operandStack.push(res);
    }

    private void doNOT() {
        System.out.println("NOT");
        if (operatorStack.size() < 1)
            throw new IllegalArgumentException("Illegal query expression!");
        double[] op0 = operandStack.pop();
        double[] res = new double[N];
        for (int i = 0; i < N; i++)
            res[i] = 1 - op0[i];
        operandStack.push(res);
    }

    private double[] membership(double[] termWeights) {
        // modify weights to range (0,1)
        double sum = Arrays.stream(termWeights).sum();
        if (sum == 0) {
            throw new ArithmeticException();
        }
        for (int i = 0; i < termWeights.length; i++)
            termWeights[i] = termWeights[i] / sum;
        return termWeights;
    }

    private double[] buildOperand(String term) {
        double[] operand = new double[N];
        List<WordMetadata> wordMetadataList = corpus.getInvertedIndex().get(term);
        if(wordMetadataList == null)
            return operand;
        for (WordMetadata metadata : wordMetadataList)
            operand[fileIdToIndex.get(metadata.fileID)] = metadata.occurence;
        return membership(operand);
    }

    /**
     * Start processing a query string using the fuzzy boolean model. Support parenthesis processing. The query string
     * looks like: NOT w1 AND w2 OR (!(w3 OR w4))
     * <br>
     * Note that NOT has the highest priority.
     *
     * @param query
     *         the query string, boolean operators and literals must be separated with space.
     */
    public ArrayList<SearchResult> processQuery(String query) {
        Scanner scanner = new Scanner(query);
        String token;
        BooleanOperator operator;
        while (scanner.hasNext()) {
            token = scanner.next();
            operator = BooleanOperator.parse(token);
            if (operator == LITERAL)
                operandStack.push(buildOperand(token));
            else {
                if (operator == RP) {
                    if (operatorStack.isEmpty())
                        throw new IllegalArgumentException("Parenthesis mismatch!");
                    BooleanOperator booleanOperator;
                    while ((booleanOperator = operatorStack.pop()) != LP) {
                        if (booleanOperator == AND)
                            doAND();
                        else if (booleanOperator == OR)
                            doOR();
                        if (operatorStack.isEmpty())
                            throw new IllegalArgumentException("Parenthesis mismatch!");
                    }
                } else {
                    if (operator != LP && operandStack.isEmpty())
                        throw new IllegalArgumentException(operator.name() + " should be in the middle of operands!");
                    operatorStack.push(operator);
                }
            }
            if (!operatorStack.isEmpty() && operator != NOT && operatorStack.peek() == NOT)
                doNOT();
        }
        while (!operatorStack.isEmpty()) {
            BooleanOperator booleanOperator = operatorStack.pop();
            if (booleanOperator == AND)
                doAND();
            else if (booleanOperator == OR)
                doOR();
            else if (booleanOperator == LP)
                throw new IllegalArgumentException("Parenthesis mismatch!");
        }
        ArrayList<SearchResult> results = new ArrayList<>();
        double[] scores = operandStack.pop();
        for (Document document : corpus.getDocuments()) {
            int fileId = document.getFileID();
            results.add(new SearchResult(fileId, scores[fileIdToIndex.get(fileId)]));
        }
        results.sort(new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return Double.compare(o2.score, o1.score);
            }
        });
        return results;
    }

    /**
     * Returns top 1000 search results for creating TREC Search Result
     */
    public SearchResult[] getTopSearchResults(ArrayList<String> queryList) {
        StringBuilder query = new StringBuilder(queryList.get(0));
        for (String term : queryList)
            query.append(" OR ").append(term);
        List<SearchResult> results = processQuery(query.toString());
        int limit = Math.min(results.size(), 1000);
        SearchResult[] topResults = new SearchResult[limit];
        for (int i = 0; i < limit; i++)
            topResults[i] = results.get(i);
        return topResults;
    }
}

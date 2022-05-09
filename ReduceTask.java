
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReduceTask implements Runnable{
    private List<Map<Integer, Integer>> dictList;
    private List<ArrayList<String>> wordsList;
    private String docName;

    public ReduceTask(List<Map<Integer, Integer>> dictList, List<ArrayList<String>> wordsList,
                      String docName) {
        this.dictList = dictList;
        this.wordsList = wordsList;
        this.docName = docName;
    }

    public int fibo(int n)
    {
        if (n <= 1)
            return n;
        return fibo(n - 1) + fibo(n - 2);
    }

    @Override
    public void run() {
        Map<Integer, Integer> dict = dictList.get(0);

        //Se vor unii dictionarele din etapa Map
        for (int i = 1; i < dictList.size(); i++) {
            Map<Integer, Integer> aux = dictList.get(i);
            for (Map.Entry<Integer, Integer> entry: aux.entrySet()) {
                if (dict.containsKey(entry.getKey())) {
                    dict.put(entry.getKey(), dict.get(entry.getKey()) + entry.getValue());
                }
                else {
                    dict.put(entry.getKey(), entry.getValue());
                }
            }
        }

        //Se initializeaza variabilele pentru calcularea rangului si al cuvintelor de lungime maxima
        Float rank = Float.valueOf(0);
        Integer wordsNr = 0;
        Integer maxValue = 0;
        Integer maxNr = 0;
        for (Map.Entry<Integer, Integer> entry: dict.entrySet()) {
            rank += fibo(entry.getKey() + 1) * entry.getValue();
            wordsNr += entry.getValue();

            //Pentru ca in Map sunt puse in ordine crescatoare, lungimea maxima va fi ultima
            maxValue = entry.getKey();
            maxNr = entry.getValue();
        }

        //Se elimina calea din numele fisierului
        int indexName = docName.lastIndexOf("/");
        docName = docName.substring(indexName + 1);
        
        Tema2.results.add(docName + "," + String.format("%.2f", rank / wordsNr) + "," + maxValue + "," + maxNr);
    }
}

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tema2 {
    //String ce retine delimitatoarele de cuvinte
    public static String wordLimits = ";:/?˜\\.,><'[]{}()!@#$%ˆ&- +’=*”| /\t\n\r";
    //dictResults si wordsResults vor retine rezultatele din etapa Map
    public static List<AbstractMap.SimpleEntry<String, Map<Integer, Integer>>> dictResults =
            Collections.synchronizedList(new ArrayList<>());
    public static List<AbstractMap.SimpleEntry<String, ArrayList<String>>> wordsResults =
            Collections.synchronizedList(new ArrayList<>());
    //results retine rezultatele finale
    public static List<String> results = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        //Se va mapa un numar pentru fiecare document
        Map<Integer, String> mapDocumentNr = new HashMap<>();

        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        int fragDimension = 0;
        int documentsNr = 0;
        int ind = 0;

        ExecutorService executorMap = Executors.newFixedThreadPool(Integer.parseInt(args[0]));
        ExecutorService executorReduce = Executors.newFixedThreadPool(Integer.parseInt(args[0]));
        Scanner scan = null;

        File file = new File(args[1]);
        
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scan.hasNextLine()) {
            //Prima data se va citii cat are un fragment de procesat
            if (ind == 0) {
                fragDimension = Integer.parseInt(scan.nextLine());
            }
            //Apoi se va citi numarul de documente de procesat
            else if (ind == 1) {
                documentsNr = Integer.parseInt(scan.nextLine());
            }
            /*
            In final, pentru fiecare fisier citit se va asocia un numar, se vor determina
            cate task-uri de tip Map se vor executa in functie de lungimea fisierului
            */
            else if (ind >= 2) {
                String filename = scan.nextLine();
                mapDocumentNr.put(ind - 2, filename);

                File fileProcess = new File(filename);
                
                int fileLength = (int) fileProcess.length();
                int tasksNr = (int) fileLength / fragDimension;
                
                for (int i = 0; i < tasksNr + 1; i++) {
                    /*
                    Ultimul task va putea sa proceseze mai putin decat dimensiunea data
                    ca parametru
                    */
                    if (i == tasksNr) {
                        executorMap.submit(new MapTask(filename, i * fragDimension,
                                fileLength - i * fragDimension));
                        break;
                    }
                    executorMap.submit(new MapTask(filename, i * fragDimension, fragDimension));
                }
            }
            ind++;
        }
        executorMap.shutdown();
        //Se va astepta terminarea tuturor task-urilor
        try {
            executorMap.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < documentsNr; i++) {
            /*
            Pentru fiecare document se va creea o lista de dictionare si liste de cuvinte
            obtinute in etapa de Map
            */
            List<Map<Integer, Integer>> dictList = new ArrayList<>();
            List<ArrayList<String>> wordsList = new ArrayList<>();
            for (int j = 0; j < dictResults.size(); j++) {
                //Daca dictionarul actual apartine documentului actual, se va adauga in lista
                if (Objects.equals(dictResults.get(j).getKey(), mapDocumentNr.get(i))) {
                    dictList.add(dictResults.get(j).getValue());
                }
                //La fel pentru lista de cuvinte
                if (Objects.equals(wordsResults.get(j).getKey(), mapDocumentNr.get(i))) {
                    wordsList.add(wordsResults.get(j).getValue());
                }
            }
            executorReduce.submit(new ReduceTask(dictList, wordsList, mapDocumentNr.get(i)));
        }

        executorReduce.shutdown();
        try {
            executorReduce.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Rezultatele finale se vor sorta dupa rank
        results.sort(new SortByRank());
        try {
            //Apoi se vor scrie in fisierul de iesire
            FileWriter writer = new FileWriter(args[2]);
            for (int i = 0; i < results.size(); i++) {
                writer.write(results.get(i));
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.util.*;

public class MapTask implements Runnable {
    private String docName;
    private int offset, dimension;

    public MapTask(String docName, int offset, int dimension) {
        this.docName = docName;
        this.offset = offset;
        this.dimension = dimension;
    }

    @Override
    public void run() {
        FileInputStream in = null;
        boolean start = false, end = false;
        Map<Integer, Integer> mapWordsCount = new HashMap<>();

        Integer maxim = 0;
        
        //O lista ce va retine cuvintele de lungime maxima
        ArrayList<String> words = new ArrayList<>();
        
        try {
            in = new FileInputStream(docName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //In result se va retine rezultatul initial al citirii
        byte[] result = new byte[dimension];

        StringBuilder res = null;
        try {
            /*
            Daca offset-ul nu este 0, se va verifica daca actualul fragment de procesat incepe
            in mijlocul unui cuvant
            */
            if (offset != 0) {
                /*
                Se vor sari offset-1 caractere si se vor citi urmatoarele doua, primul fiind
                ultimul din fragmentul anterior si celalalt primul din cel actual
                */
                in.skip(offset - 1);
                char lastCarac = (char) in.read();
                char firstCarac = (char) in.read();
                /*
                Daca ambele nu se regasesc in lista de separatori, fragmentul incepe in mijlocul
                unui cuvant
                */
                if (Tema2.wordLimits.indexOf(lastCarac) == -1 &&
                    Tema2.wordLimits.indexOf(lastCarac) == -1) {
                    start = true;
                }
                //Se revine la pozitia unde incepe fragmentul
                in.skip(-1);
            }

            /*
            Se citeste fragmentul si se verifica daca acesta se termina in mijlocul unui cuvant
            asemanator cu verificarea de la inceput
            */
            in.read(result, 0, dimension);
            res = new StringBuilder(new String(result));

            if (in.available() != 0) {
                char lastCarac = (char) in.read();
                if (Tema2.wordLimits.indexOf(lastCarac) == -1 &&
                        Tema2.wordLimits.indexOf(res.charAt(res.length() - 1)) == -1) {
                    end = true;
                }
                in.skip(-1);
            }

            //Daca incepe in mijlocul unui cuvant, se va ignora acea parte din cuvant
            if (start) {
                while (Tema2.wordLimits.indexOf(res.charAt(0)) == -1) {
                    res = new StringBuilder(res.substring(1));
                }
            }

            //Daca se termina in mijlocul unui cuvant se va citi in continuare si restul cuvantului
            if (end) {
                while (!Tema2.wordLimits.contains(Objects.toString(res.charAt(res.length() - 1)))) {
                    if (in.available() == 0)
                        break;
                    char carac = (char) in.read();
                    res.append(carac);
                }
            }
            in.close();

            //Se imparte fragmentul in cuvinte
            StringTokenizer stringTokenizer = new StringTokenizer(res.toString(), Tema2.wordLimits);
            
            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                Integer dimWord = token.length();

                //Daca deja s-au gasit cuvinte cu dimensiunea respectiva, se va incrementa numarul lor
                if (mapWordsCount.containsKey(dimWord)) {
                    mapWordsCount.put(dimWord, mapWordsCount.get(dimWord) + 1);
                }
                else {
                    mapWordsCount.put(dimWord, 1);
                }

                /*
                Daca lungimea actuala este mai mare decat maximul, se adauga cuvantul in lista, iar
                maximul devine dimensiunea actuala
                */
                if (dimWord > maxim) {
                    maxim = dimWord;
                    words = new ArrayList<>();
                    words.add(token);
                }

                //Daca actualul cuvant este la fel de mare ca maximul, se va adauga in lista
                else if (dimWord == maxim) {
                    words.add(token);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Se va adauga maparea si lista de cuvinte la rezultate
        Tema2.dictResults.add(new AbstractMap.SimpleEntry<>(docName, mapWordsCount));
        Tema2.wordsResults.add(new AbstractMap.SimpleEntry<>(docName, words));
    }
}

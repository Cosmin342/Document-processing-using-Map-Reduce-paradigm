import java.util.Comparator;
import java.util.StringTokenizer;

class SortByRank implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        //Se vor impartii cele doua stringuri dupa virgula
        StringTokenizer tokenizer = new StringTokenizer(o1, ",");
        tokenizer.nextToken();
        float val1 = Float.parseFloat((tokenizer.nextToken()));
        
        tokenizer = new StringTokenizer(o2, ",");
        tokenizer.nextToken();
        float val2 = Float.parseFloat(tokenizer.nextToken());
        
        /*
        A doua parte din fiecare string reprezinta rank-ul, iar in functie de
        acesta se va realiza sortarea descrescatoare
        */
        if (val1 == val2) {
            return 0;
        }
        if (val1 < val2) {
            return 1;
        }
        return -1;
    }
}
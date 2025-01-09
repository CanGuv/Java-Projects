import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static ArrayList<String> targetWords = new ArrayList<>();

//    public static void main(String[] args) throws FileNotFoundException {
////        Scanner in = new Scanner(new File("wordlist.txt"));
////        while(in.hasNext()){
////            targetWords.add(in.next());
////        }
//
//        readList();
//        getWord();
//    }

    public static String getWord(){
        Random r = new Random();
        String word = targetWords.get(r.nextInt(targetWords.size()));
        System.out.println("The target word is: " + word);
        return word;
    }

    public static void readList() throws FileNotFoundException {
        Scanner in = new Scanner(new File("wordlist.txt"));
        while(in.hasNext()){
            targetWords.add(in.next());
        }
    }
}

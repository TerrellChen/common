package terrell.common.algorithm.stringsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Terrell Chen
 */
public class Kmp {
    private static void kmpMakeNext(char[] s, int[] next) {
        System.out.println("Next");
        int len = s.length;
        next[0] = 0;
        for (int i = 1, k = 0; i < len; i++) {
            while (k > 0 && s[k] != s[i]) {
                k = next[k - 1];
            }
            if (s[k] == s[i]) {
                k++;
            }
            next[i] = k;
        }
        for (int i : next) {
            System.out.println(i);
        }
    }

    private static int kmp(char[] text, char[] pattern) {
        System.out.println("Kmp");
        int textLength = text.length;
        int patternLength = pattern.length;
        int[] next = new int[patternLength];
        kmpMakeNext(pattern, next);
        int i=0,j=0;
        while (i<textLength && j < patternLength){
            System.out.println("i: " + i + " j: " + j);
            System.out.println(print(i, text));
            System.out.println(print(j, pattern));
            if (text[i] != pattern[j]){
                if (i==0 || j==0){
                    i++;
                } else {
                    j = next[j - 1];
                }
            } else {
                i++;
                j++;
            }
        }
        if (j == patternLength){
            return i-j;
        }
        return -1;
    }

    private static String print(int startOffset, char[] array){
        List<String> list = new ArrayList<>();
        for (int i=startOffset;i<array.length;i++){
            list.add(String.valueOf(array[i]));
        }

        return list.stream().collect(Collectors.joining(""));
    }
}

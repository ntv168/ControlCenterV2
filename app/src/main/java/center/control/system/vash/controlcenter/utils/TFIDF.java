package center.control.system.vash.controlcenter.utils;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Thuans on 4/17/2017.
 */
public class TFIDF {
    public static double tf(Map<String,Integer> wordCout, String term) {
        double allCount = 0;
        Iterator it = wordCout.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            allCount += (Integer) pair.getValue();
        }
        int termCount = wordCout.get(term);
        System.out.println(termCount +"   "+ allCount);
        return termCount / allCount;
    }
    
    public static double idf(Map<Integer,Map<String,Integer>> targetSet, String term) {
        double functionContainTerm = 0;
        double numberOfFunction = targetSet.entrySet().size();

        Iterator it = targetSet.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Map<String, Integer> frequence = (Map<String, Integer>) pair.getValue();
            if (frequence.get(term) != null){
                functionContainTerm++;
            }
        }
        return (double) (Math.log(numberOfFunction / functionContainTerm)/Math.log(2));
    }

    public static double createTfIdf(Map<Integer, Map<String, Integer>> targetSet, String term, int targetId) {

        Map<String,Integer> wordCout = targetSet.get(targetId);
//        System.out.println("tf: "+tf(wordCout, term));
//        System.out.println("idf: "+idf(functionSet, term));
        return tf(wordCout, term) * idf(targetSet, term);
    }
}

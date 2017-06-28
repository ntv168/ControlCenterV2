package center.control.system.vash.controlcenter.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.TargetObject;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;


/**
 * Created by Thuans on 4/28/2017.
 */

public class BotUtils {
    private static String TAG = "Bot utillss";
    private static String BOT_NAME = "<bot-name>";
    private static String BOT_ROLE = "<bot-role>";
    private static String OWNER_NAME = "<owner-name>";
    private static String OWNER_ROLE = "<owner-role>";
    private static String TARGET_OBJECT = "<target-object>";
    private static String RESULT_VALUE = "<result-value>";
    public static DetectFunctionEntity findBestFunctDetected(List<TermEntity> foundTerm){
        Map<Integer,Double> sumTfidfIntent = new HashMap<>();
        double maxTfidf = 0;
        int bestIntentId = -1;
        for (TermEntity term: foundTerm){
            if (term.getDetectFunctionId() != -1) {
                double currentPoint = term.getTfidfPoint();
                if (sumTfidfIntent.containsKey(term.getDetectFunctionId())) {
                    currentPoint += sumTfidfIntent.get(term.getDetectFunctionId());
                }
                sumTfidfIntent.put(term.getDetectFunctionId(), currentPoint);
                if (currentPoint > maxTfidf) {
                    maxTfidf = currentPoint;
                    bestIntentId = term.getDetectFunctionId();
                }
            }
        }

        if (bestIntentId != -1) {
            DetectIntentSQLite sqLite = new DetectIntentSQLite();
            DetectFunctionEntity res = sqLite.findFunctionById(bestIntentId);
            res.setDetectScore(maxTfidf);
            Log.d(TAG, res.getFunctionName()+ " function founded "+maxTfidf);
            return res;
        } else
                return null;
    }
    public static DetectSocialEntity findBestSocialDetected(List<TermEntity> foundTerm){
        Map<Integer,Double> sumTfidfIntent = new HashMap<>();
        double maxTfidf = 0;
        int bestIntentId = -1;
        for (TermEntity term: foundTerm){
            if (term.getDetectSocialId() != -1) {
                double currentPoint = term.getTfidfPoint();
                if (sumTfidfIntent.containsKey(term.getDetectSocialId())) {
                    currentPoint += sumTfidfIntent.get(term.getDetectSocialId());
                }
                sumTfidfIntent.put(term.getDetectSocialId(), currentPoint);
                if (currentPoint > maxTfidf) {
                    maxTfidf = currentPoint;
                    bestIntentId = term.getDetectSocialId();
                }
            }
        }
        if (bestIntentId != -1) {
            DetectIntentSQLite sqLite = new DetectIntentSQLite();
            DetectSocialEntity res = sqLite.findSocialById(bestIntentId);
            res.setDetectScore(maxTfidf);
            Log.d(TAG, res.getName()+ " social founded "+maxTfidf);
            return res;
        } else
            return null;
    }
    public static DeviceEntity findBestDevice(List<TargetTernEntity> termTargets) {
        Map<Integer,Double> sumTfidfTarget = new HashMap<>();
        double maxTfidf = 0;
        int bestDeviceId = -1;
        for (TargetTernEntity termTarget: termTargets){
            if (termTarget.getDetectDeviceId() != -1) {
                double currentPoint = termTarget.getTfidfPoint();
                if (sumTfidfTarget.containsKey(termTarget.getDetectDeviceId())) {
                    currentPoint += sumTfidfTarget.get(termTarget.getDetectDeviceId());
                }
                sumTfidfTarget.put(termTarget.getDetectDeviceId(), currentPoint);
                if (currentPoint > maxTfidf) {
                    maxTfidf = currentPoint;
                    bestDeviceId= termTarget.getDetectDeviceId();
                }
            }
        }
        Log.d(TAG,"best device "+ bestDeviceId+ " founded");
        if (bestDeviceId != -1) {
            return DeviceSQLite.findById(bestDeviceId);
        } else
            return null;

    }


    public static Map<Integer,Map<String,Integer>> readTargettoHashMap(List<? extends TargetObject> objs) {
        Map<Integer,Map<String,Integer>> targetNameWordCount =  new HashMap<>();
        for (TargetObject obj: objs) {
            int objId = obj.getId();
            String words[] = (obj.getName()+ " "+obj.getNickName()).split("( )+");
            for (int i = 0; i < words.length; i++){
                words[i] = words[i].toLowerCase();
            }
            Map<String, Integer> wordCount =  new HashMap<>();
            wordCount = updateWordCount(wordCount, words);
            targetNameWordCount.put(objId, wordCount);
        }
        return targetNameWordCount;
    }
    private static Map<String, Integer> updateWordCount(Map<String, Integer> wordCount, String[] words) {
        Map<String, Integer> tmp = wordCount;
        for (int i = 0; i<words.length ; i++){
            int count = 1;
            if (tmp.get(words[i]) != null){
                count =  tmp.get(words[i]);
                count++;
            }
            tmp.put(words[i], count);
            Log.d(TAG,words[i] +  "  co: "+count);
        }
        return tmp;
    }

    public static String completeSentence(String sentence, String resultValue, String targetObject) {
        SmartHouse house = SmartHouse.getInstance();
        String result = sentence.replaceAll(BOT_NAME, house.getBotName());
        result = result.replaceAll(BOT_ROLE, house.getBotRole());
        result = result.replaceAll(OWNER_NAME, house.getOwnerName());
        result = result.replaceAll(OWNER_ROLE, house.getOwnerRole());
        result = result.replaceAll(RESULT_VALUE, resultValue);
        result = result.replaceAll(TARGET_OBJECT, targetObject);

        Log.d(TAG,result);
        return result;
    }
    public static DetectSocialEntity getSocialByName(String name){
        DetectIntentSQLite sqLite = new DetectIntentSQLite();
        DetectSocialEntity reply = sqLite.findSocialByName(name);
        if (reply == null){
            Log.e(TAG,"Không tìm thấy intent " +name);
        }
        return reply;
    }

    public static AreaEntity findBestArea(List<TargetTernEntity> termTargets) {
        Map<Integer,Double> sumTfidfTarget = new HashMap<>();
        double maxTfidf = 0;
        int bestAreaId = -1;
        for (TargetTernEntity termTarget: termTargets){
            if (termTarget.getDetectAreaId() != -1) {
                double currentPoint = termTarget.getTfidfPoint();
                if (sumTfidfTarget.containsKey(termTarget.getDetectAreaId())) {
                    currentPoint += sumTfidfTarget.get(termTarget.getDetectAreaId());
                }
                sumTfidfTarget.put(termTarget.getDetectAreaId(), currentPoint);
                if (currentPoint > maxTfidf) {
                    maxTfidf = currentPoint;
                    bestAreaId= termTarget.getDetectAreaId();
                }
            }
        }
        Log.d(TAG,"best area "+ bestAreaId+ " founded");
        if (bestAreaId != -1) {
            return AreaSQLite.findById(bestAreaId);
        } else
            return null;
    }

    public static ScriptEntity findBestScript(List<TargetTernEntity> termTargets) {
        Map<Integer,Double> sumTfidfTarget = new HashMap<>();
        double maxTfidf = 0;
        int bestModeId = -1;
        for (TargetTernEntity termTarget: termTargets){
            if (termTarget.getDetectScriptId() != -1) {
                double currentPoint = termTarget.getTfidfPoint();
                if (sumTfidfTarget.containsKey(termTarget.getDetectScriptId())) {
                    currentPoint += sumTfidfTarget.get(termTarget.getDetectScriptId());
                }
                sumTfidfTarget.put(termTarget.getDetectScriptId(), currentPoint);
                if (currentPoint > maxTfidf) {
                    maxTfidf = currentPoint;
                    bestModeId= termTarget.getDetectScriptId();
                }
            }
        }
        Log.d(TAG,"best mode "+ bestModeId+ " founded");
        if (bestModeId != -1) {
            return ScriptSQLite.findById(bestModeId);
        } else
            return null;
    }
//    public static IntentLearned getIntentById(int id){
//        IntentLearnedSQLite intentLearnedSQLite = new IntentLearnedSQLite();
//        IntentLearned reply = intentLearnedSQLite.findReplyById(id);
//        if (reply == null){
//            Log.e(TAG,"Không tìm thấy intent để speakkkkkk " + id);
//        }
//        return reply;
//    }
//
//    public static DetectIntent findDetectByFunction(String functionName) {
//        DetectIntentSQLite detectIntentSQLite = new DetectIntentSQLite();
//        return detectIntentSQLite.findByName(functionName);
//    }
}

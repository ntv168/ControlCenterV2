package center.control.system.vash.controlcenter.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.configuration.CommandEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.TargetObject;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
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
    private static String TARGET_OBJECT = "<target>";
    private static String RESULT_VALUE = "<result-value>";
    private static String notLearnSentence;

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
    public static DeviceEntity findBestDevice(List<TargetTernEntity> termTargets, int areaId) {
        Map<Integer,Double> sumTfidfTarget = new HashMap<>();
        double maxTfidf = 0;
        DeviceEntity bestDevice = null;
        for (TargetTernEntity termTarget: termTargets){
            if (termTarget.getDetectDeviceId() != -1) {
                double currentPoint = termTarget.getTfidfPoint();
                if (sumTfidfTarget.containsKey(termTarget.getDetectDeviceId())) {
                    currentPoint += sumTfidfTarget.get(termTarget.getDetectDeviceId());
                }
                sumTfidfTarget.put(termTarget.getDetectDeviceId(), currentPoint);
                if (currentPoint > maxTfidf ) {
                    DeviceEntity dev =DeviceSQLite.findById(termTarget.getDetectDeviceId());
                    if (dev.getAreaId() == areaId || areaId == -1) {
                        maxTfidf = currentPoint;
                        bestDevice = dev;
                    }
                }
            }
        }
        Log.d(TAG,"best device "+ bestDevice+ " founded");
            return bestDevice ;

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
        if (sentence == null){
            if (notLearnSentence == null) {
                DetectSocialEntity soc = getSocialByName(ConstManager.NOT_LEARN_YET);
                if (soc.getQuestionPattern() == null) {
                    Log.e(TAG, "not learn yet không có câu question");
                } else {
                    notLearnSentence = completeSentence(soc.getQuestionPattern(), "", "");
                }
            }
            return notLearnSentence;
        } else {
            String result = sentence.replaceAll(BOT_NAME, house.getBotName());
            result = result.replaceAll(BOT_ROLE, house.getBotRole());
            result = result.replaceAll(OWNER_NAME, house.getOwnerName());
            result = result.replaceAll(OWNER_ROLE, house.getOwnerRole());
            result = result.replaceAll(RESULT_VALUE, resultValue);
            result = result.replaceAll(TARGET_OBJECT, targetObject);

            Log.d(TAG, result);
            return result;
        }
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
    public static String getTime(){
        Date time = new Date();
        Log.d(TAG,time.toString());
        String result = "";
        result += time.getHours()+" giờ "+time.getMinutes()+" phút ";
        return result;
    }
    public static String getDay(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        String result = "";
//        Log.d(TAG,time.getMonth()+ " "+time.getYear()+" "+time.getDate()+" "+time.getDay());
        result += " ngày "+day+" tháng "+month+" ";
        return result;
    }
    public void saveDeviceTFIDFTerm(List<DeviceEntity> listDevices) {

        Map<Integer,Map<String,Integer>> trainingSetMap = BotUtils.readTargettoHashMap(listDevices);
        Map<Integer,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int deviceId = (int) pair.getKey();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;
                termTfidf = TFIDF.createTfIdf(cloneForCalculate, term, deviceId);
                if (termTfidf ==0.0){
                    termTfidf = 1.0;
                }
                Log.d(TAG, termTfidf + "   " + term + "   " + deviceId);

                TargetTernEntity targetTerm = new TargetTernEntity();
                targetTerm.setDetectDeviceId(deviceId);
                targetTerm.setDetectAreaId(-1);
                targetTerm.setDetectScriptId(-1);
                targetTerm.setTfidfPoint(termTfidf);
                targetTerm.setContent(" "+term+" ");
                sqLite.insertTargetTerm(targetTerm);
            }
        }
    }
    public void saveAreaTFIDFTerm(List<AreaEntity> listArea) {
        Map<Integer,Map<String,Integer>> trainingSetMap = BotUtils.readTargettoHashMap(listArea);
        Map<Integer,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int areaId = (int) pair.getKey();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;

                    termTfidf = TFIDF.createTfIdf(cloneForCalculate, term, areaId);
                if (termTfidf ==0.0){
                    termTfidf = 1.0;
                }
                    Log.d(TAG,termTfidf+"   "+term+"   "+areaId);

                TargetTernEntity targetTerm = new TargetTernEntity();
                targetTerm.setDetectAreaId(areaId);
                targetTerm.setDetectDeviceId(-1);
                targetTerm.setDetectScriptId(-1);
                targetTerm.setTfidfPoint(termTfidf);
                targetTerm.setContent(" "+term+" ");
                sqLite.insertTargetTerm(targetTerm);
            }
        }
    }

    public void saveScriptTFIDFTerm(List<ScriptEntity> listScript) {
        Map<Integer,Map<String,Integer>> trainingSetMap = BotUtils.readTargettoHashMap(listScript);
        Map<Integer,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int scriptId = (int) pair.getKey();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;

                    termTfidf = TFIDF.createTfIdf(cloneForCalculate, term, scriptId);
                if (termTfidf ==0.0){
                    termTfidf = 1.0;
                }
                    Log.d(TAG,termTfidf+" ");

                TargetTernEntity targetTerm = new TargetTernEntity();
                targetTerm.setDetectScriptId(scriptId);
                targetTerm.setDetectAreaId(-1);
                targetTerm.setDetectDeviceId(-1);
                targetTerm.setTfidfPoint(termTfidf);
                targetTerm.setContent(" "+term+" ");
                sqLite.insertTargetTerm(targetTerm);
            }
        }
    }

    public static String getVerbByIntent(String functionName) {
        switch (functionName){
            case ConstManager.FUNCTION_TURN_ON:
                return "bật";
            case ConstManager.FUNCTION_TURN_OFF:
                return "tắt";
            case ConstManager.FUNCTION_INC_TEMP:
                return "tăng nhiệt độ";
            case ConstManager.FUNCTION_DEC_TEMP:
                return "giảm nhiệt độ";
        }
        return "";
    }
    public static void implementCommand(DetectFunctionEntity functionIntent, DeviceEntity device, ScriptEntity mode){
        CommandEntity cmd = new CommandEntity();
        List<CommandEntity> cmds = new ArrayList<>();
        switch (functionIntent.getFunctionName()){
            case ConstManager.FUNCTION_TURN_ON:
                cmd.setDeviceId(device.getId());
                cmd.setDeviceState("on");
                break;
            case ConstManager.FUNCTION_TURN_OFF:
                cmd.setDeviceId(device.getId());
                cmd.setDeviceState("off");
                break;
            case ConstManager.FUNCTION_DEC_TEMP:
                cmd.setDeviceId(device.getId());
                if (DeviceEntity.remoteTypes.contains(device.getType())) {
                    cmd.setDeviceState("dec");
                } else {
                    cmd.setDeviceState("off");
                }
                break;
            case ConstManager.FUNCTION_INC_TEMP:
                cmd.setDeviceId(device.getId());
                if (DeviceEntity.remoteTypes.contains(device.getType())) {
                    cmd.setDeviceState("inc");
                } else {
                    cmd.setDeviceState("on");
                }
                break;
            case ConstManager.FUNCTION_START_MODE:
                cmds = ScriptSQLite.getCommandByScriptId(mode.getId());
                break;
            case ConstManager.FUNCTION_STOP_MODE:
                cmds = ScriptSQLite.getCommandByScriptId(mode.getId());
                for (CommandEntity cm : cmds){
                    cm.inverseDeviceState();
                }
                break;
        }
        if (cmd.getDeviceState() != null) {

            SmartHouse.getInstance().addCommand(cmd);
        }
        if (cmds.size()>0){
            for (CommandEntity cm : cmds){

                SmartHouse.getInstance().addCommand(cmd);
            }
        }
    }

    public static String getAttributeByFunction(String functionName, AreaEntity area) {
        switch (functionName){
            case "checkPerson":
                return area.getPersonDetect();
            case "checkSecurity":
                return area.getSafety();
            case "checkLight":
                return area.getLight();
            case "checkTemperature":
                return area.getTemperature();
            case "checkSound":
                return area.getSound();
            case "checkElectricUsing":
                return area.getElectricUsing();
        }
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

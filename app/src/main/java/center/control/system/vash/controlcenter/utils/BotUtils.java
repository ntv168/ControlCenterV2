package center.control.system.vash.controlcenter.utils;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import center.control.system.vash.controlcenter.area.AreaEntity;
import center.control.system.vash.controlcenter.area.AreaSQLite;
import center.control.system.vash.controlcenter.command.CommandEntity;
import center.control.system.vash.controlcenter.device.DeviceEntity;
import center.control.system.vash.controlcenter.device.DeviceSQLite;
import center.control.system.vash.controlcenter.device.TargetObject;
import center.control.system.vash.controlcenter.nlp.CurrentContext;
import center.control.system.vash.controlcenter.nlp.DetectFunctionEntity;
import center.control.system.vash.controlcenter.nlp.DetectIntentSQLite;
import center.control.system.vash.controlcenter.nlp.DetectSocialEntity;
import center.control.system.vash.controlcenter.nlp.OwnerTrainEntity;
import center.control.system.vash.controlcenter.nlp.TargetTernEntity;
import center.control.system.vash.controlcenter.nlp.TermEntity;
import center.control.system.vash.controlcenter.nlp.TermSQLite;
import center.control.system.vash.controlcenter.panel.ControlPanel;
import center.control.system.vash.controlcenter.script.ScriptEntity;
import center.control.system.vash.controlcenter.script.ScriptSQLite;


/**
 * Created by Thuans on 4/28/2017.
 */

public class BotUtils {
    private static String TAG = "Bot utills nè::::";
    public static String BOT_NAME = "<bot-name>";
    public static String BOT_ROLE = "<bot-role>";
    public static String OWNER_NAME = "<owner-name>";
    public static String OWNER_ROLE = "<owner-role>";
    public static String TARGET_OBJECT = "<target>";
    public static String RESULT_VALUE = "<result-value>";
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
                    if (dev!= null && (dev.getAreaId() == areaId || areaId == -1)) {
                        maxTfidf = currentPoint;
                        bestDevice = dev;
                    }
                }
            }
        }
        Log.d(TAG,"best device "+ bestDevice+ " founded");
            return bestDevice ;

    }
    public static double findBestDeviceTfidf(List<TargetTernEntity> termTargets) {
        Map<Integer,Double> sumTfidfTarget = new HashMap<>();
        double maxTfidf = 0;
        for (TargetTernEntity termTarget: termTargets){
            if (termTarget.getDetectDeviceId() != -1) {
                double currentPoint = termTarget.getTfidfPoint();
                if (sumTfidfTarget.containsKey(termTarget.getDetectDeviceId())) {
                    currentPoint += sumTfidfTarget.get(termTarget.getDetectDeviceId());
                }
                sumTfidfTarget.put(termTarget.getDetectDeviceId(), currentPoint);
                if (currentPoint > maxTfidf ) {
                    maxTfidf = currentPoint;
                }
            }
        }
        return maxTfidf ;
    }


    public static Map<Integer,Map<String,Integer>> readTargettoHashMap(List<? extends TargetObject> objs) {
        Map<Integer,Map<String,Integer>> targetNameWordCount =  new HashMap<>();
        for (TargetObject obj: objs) {
            int objId = obj.getId();
            String words[] = (obj.getName()+ " "+obj.getName()+ " "+obj.getName()+ " "+
                    obj.getName()+ " "+obj.getName()+ " "+obj.getNickName()).split("( )+");
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
//            Log.d(TAG,words[i] +  "  co: "+count);
        }
        return tmp;
    }

    public static String completeSentence(String sentence, String resultValue, String targetObject) {
        SmartHouse house = SmartHouse.getInstance();
        if (sentence == null){
            if (notLearnSentence == null) {
                DetectSocialEntity soc = getSocialById(ConstManager.NOT_LEARN_YET);
                if (soc.getReplyPattern() == null) {
                    Log.e(TAG, "not learn yet không có câu question");
                } else {
                    notLearnSentence = soc.getReplyPattern();
                    notLearnSentence = notLearnSentence.replaceAll(BOT_NAME, house.getBotName());
                    notLearnSentence = notLearnSentence.replaceAll(BOT_ROLE, house.getBotRole());
                    notLearnSentence = notLearnSentence.replaceAll(OWNER_NAME, house.getOwnerName());
                    notLearnSentence = notLearnSentence.replaceAll(OWNER_ROLE, house.getOwnerRole());
                    notLearnSentence = notLearnSentence.replaceAll(RESULT_VALUE, resultValue);
                    notLearnSentence = notLearnSentence.replaceAll(TARGET_OBJECT, targetObject);
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

            Log.d(TAG, result.trim());
            return result.trim();
        }
    }
    public static DetectSocialEntity getSocialById(int id){
        DetectIntentSQLite sqLite = new DetectIntentSQLite();
        DetectSocialEntity reply = sqLite.findSocialById(id);
        if (reply == null){
            Log.e(TAG,"Không tìm thấy intent id: " +id);
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
    public static double findBestArreaTfidf(List<TargetTernEntity> termTargets) {
        Map<Integer,Double> sumTfidfTarget = new HashMap<>();
        double maxTfidf = 0;
        for (TargetTernEntity termTarget: termTargets){
            if (termTarget.getDetectAreaId() != -1) {
                double currentPoint = termTarget.getTfidfPoint();
                if (sumTfidfTarget.containsKey(termTarget.getDetectAreaId())) {
                    currentPoint += sumTfidfTarget.get(termTarget.getDetectAreaId());
                }
                sumTfidfTarget.put(termTarget.getDetectAreaId(), currentPoint);
                if (currentPoint > maxTfidf) {
                    maxTfidf = currentPoint;
                }
            }
        }
            return maxTfidf;
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
    public static double findBestScriptTfidf(List<TargetTernEntity> termTargets) {
        Map<Integer,Double> sumTfidfTarget = new HashMap<>();
        double maxTfidf = 0;
        for (TargetTernEntity termTarget: termTargets){
            if (termTarget.getDetectScriptId() != -1) {
                double currentPoint = termTarget.getTfidfPoint();
                if (sumTfidfTarget.containsKey(termTarget.getDetectScriptId())) {
                    currentPoint += sumTfidfTarget.get(termTarget.getDetectScriptId());
                }
                sumTfidfTarget.put(termTarget.getDetectScriptId(), currentPoint);
                if (currentPoint > maxTfidf) {
                    maxTfidf = currentPoint;
                }
            }
        }

        return maxTfidf;
    }

    public void saveFunctionTFIDFTerm(Map<String,Map<String,Integer>> trainingSetMap) {

        Map<String,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            String functionName = (String)pair.getKey();
            int functId = DetectIntentSQLite.findFunctionByName(functionName).getId();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;
                termTfidf = TFIDFIntent.createTfIdf(cloneForCalculate, term, functionName);

                TermEntity termEnt = new TermEntity();
                termEnt.setDetectFunctionId(functId);
                termEnt.setDetectSocialId(-1);
                termEnt.setTfidfPoint(termTfidf);
                termEnt.setContent(" "+term.trim()+" ");
                sqLite.insertHumanTerm(termEnt);
//                Log.d(TAG, termTfidf + "   " + term + "  "+functionName+" - "+functId);
            }
        }
    }
    public void saveSocialTFIDFTerm(Map<String,Map<String,Integer>> trainingSetMap) {
        Map<String,Map<String,Integer>> cloneForCalculate = new HashMap<>(trainingSetMap);
        TermSQLite sqLite= new TermSQLite();
        Iterator it = trainingSetMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            String socialName = (String)pair.getKey();
            int socId = DetectIntentSQLite.findSocialByName(socialName).getId();
            Map<String, Integer> wordCount = (Map<String, Integer>) pair.getValue();

            Iterator wit = wordCount.entrySet().iterator();
            while (wit.hasNext()){
                Map.Entry termPair = (Map.Entry) wit.next();
                String term = (String) termPair.getKey();
                double termTfidf =1.0;
                termTfidf = TFIDFIntent.createTfIdf(cloneForCalculate, term, socialName);

                TermEntity termEnt = new TermEntity();
                termEnt.setDetectFunctionId(-1);
                termEnt.setDetectSocialId(socId);
                termEnt.setTfidfPoint(termTfidf);
                termEnt.setContent(" "+term.trim()+" ");
                sqLite.insertHumanTerm(termEnt);
//                Log.d(TAG, termTfidf + "   " + term + "  "+socialName+" "+socId);
            }
        }
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
//                Log.d(TAG, termTfidf + "   " + term + "   " + deviceId);

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
//                    Log.d(TAG,termTfidf+"   "+term+"   "+areaId);

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
//                    Log.d(TAG,termTfidf+" ");

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


    public static void implementCommand(DetectFunctionEntity functionIntent, DeviceEntity device, ScriptEntity mode){
        CommandEntity cmd = new CommandEntity();
        List<CommandEntity> cmds = new ArrayList<>();
        switch (functionIntent.getId()){
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
                SmartHouse.getInstance().addCommand(cm);
            }
        }
    }

    public static String getAttributeByFunction(int functId, AreaEntity area) {

        switch (functId){
            case ConstManager.CHECK_SECURITTY:
                return area.getSafetyBot();
            case ConstManager.CHECK_LIGHT:
                return area.getLight();
            case ConstManager.CHECK_TEMPERATUR:
                return area.getTemperatureBot();
            case ConstManager.CHECK_PERSON:
                return area.getDetect();
            case ConstManager.CHECK_ELECTRIC:
                return area.getElectricUsing();
        }
        return null;
    }
    public static String processDeviceOnly(DetectFunctionEntity functionIntent, List<TargetTernEntity> termTargets){
        CurrentContext current = CurrentContext.getInstance();
        DeviceEntity deviceOnly = BotUtils.findBestDevice(termTargets, -1);
        current.setDetectedFunction(functionIntent);
        current.setDetectSocial(null);
        if (deviceOnly != null) {
            Log.d(TAG, "Tim thấy thiet bị  thôi " + deviceOnly.getName());
            current.setDevice(deviceOnly);
            current.setScript(null);

            AreaEntity area = AreaSQLite.findById(deviceOnly.getAreaId());
            String target = deviceOnly.getName() + " trong " + area.getName();
            current.setArea(area);
            return completeSentence(functionIntent.getRemindPattern(), "", target);
        } else {
            Log.d(TAG, "Khong thấy gì hết ngoài " + functionIntent.getFunctionName());
            DetectSocialEntity askDevice = BotUtils.getSocialById(ConstManager.SOCIAL_ASK_DEVICEONLY);
            current.setDetectSocial(askDevice);
            return completeSentence(askDevice.getQuestionPattern(),"",
                    ConstManager.getVerbByIntent(functionIntent.getId()));
        }
    }
    public static String processDeviceInArea(DetectFunctionEntity functionIntent, List<TargetTernEntity> termTargets){
        CurrentContext current = CurrentContext.getInstance();
        current.setDetectedFunction(functionIntent);
        current.setDetectSocial(null);
        if (current.getArea() != null){
            DeviceEntity device = BotUtils.findBestDevice(termTargets,current.getArea().getId());
            if (device == null) {
                Log.d(TAG, "Tim thấy không gian " + current.getArea().getName() + " mà không tìm thấy thiết bị");
                DetectSocialEntity askWhichDevice = BotUtils.getSocialById(ConstManager.SOCIAL_ASK_DEVICEAREA);
                current.setDetectSocial(askWhichDevice);

                return completeSentence(askWhichDevice.getQuestionPattern(),"",
                        ConstManager.getVerbByIntent(functionIntent.getId())+" "+ current.getArea().getName());
            } else {
                Log.d(TAG, "Tìm thấy cả hai" + device.getName()+current.getArea().getName());
                current.setScript(null);
                current.setDevice(device);
                BotUtils.implementCommand(functionIntent,device,null);
                return "Xác nhận " + ConstManager.getVerbByIntent(current.getDetectedFunction().getId()) +
                        " " + current.getDevice().getName()+" trong "+current.getArea().getName();
            }
        } else {
            DetectSocialEntity notUnderReply = BotUtils.getSocialById(ConstManager.NOT_UNDERSTD);
            current.setDetectSocial(notUnderReply);
            return completeSentence(notUnderReply.getQuestionPattern(), "", "");
        } 
    }
    public static String botReplyToSentence(String humanSay){
        humanSay = " "+humanSay+" ";
        TermSQLite termSQLite = new TermSQLite();
        List<TargetTernEntity> termTargets = TermSQLite.getTargetInSentence(humanSay);
        DetectSocialEntity currentSocial = CurrentContext.getInstance().getDetectSocial();
        DetectFunctionEntity currentFunct = CurrentContext.getInstance().getDetectedFunction();
        AreaEntity area = BotUtils.findBestArea(termTargets);

        if (currentSocial!= null && currentSocial.getId() == ConstManager.SOCIAL_ASK_DEVICEAREA){
            return processDeviceInArea(CurrentContext.getInstance().getDetectedFunction(),termTargets);
        } else if (currentSocial!= null && currentSocial.getId() == ConstManager.SOCIAL_ASK_DEVICEONLY){
            return processDeviceOnly(CurrentContext.getInstance().getDetectedFunction(),termTargets);
        } else if (currentSocial!= null && currentSocial.getId() == ConstManager.SOCIAL_ASK_MODE){
            return processMode(CurrentContext.getInstance().getDetectedFunction(),termTargets);
        } else if (currentFunct!= null && currentFunct.getFunctionName().contains("check") && area!=null){
            String resultVal = BotUtils.getAttributeByFunction(currentFunct.getId(),area);
            String replyComplete ="";
            if (resultVal == null){
                replyComplete=completeSentence(currentFunct.getFailPattern(), resultVal, area.getName());
            } else {
                replyComplete=completeSentence(currentFunct.getSuccessPattern(), resultVal, area.getName());
            }
            return replyComplete;
        }  else {
            List<TermEntity> terms = termSQLite.getHumanIntentInSentence(humanSay);
//        Log.d(TAG,termSQLite.getAllTerms().size()+" s");
            DetectFunctionEntity functFound = BotUtils.findBestFunctDetected(terms);
            DetectSocialEntity socialFound = BotUtils.findBestSocialDetected(terms);
            if (functFound != null && socialFound != null) {
                double functionTfidf = functFound.getDetectScore();
                if (functFound.getId() == ConstManager.FUNCTION_START_MODE)
                    functionTfidf +=  BotUtils.findBestScriptTfidf(termTargets);
                else
                functionTfidf += BotUtils.findBestArreaTfidf(termTargets) + BotUtils.findBestDeviceTfidf(termTargets);
                Log.d(TAG, "funct point: " + functionTfidf + " soc point " + socialFound.getDetectScore());
                if (functionTfidf > socialFound.getDetectScore()) {
                    return processFunction(termTargets, functFound);
                } else {
                    return processSocial(socialFound);
                }
            } else if (functFound == null && socialFound != null) {
                return processSocial(socialFound);
            } else if (functFound != null && socialFound == null) {
                return processFunction(termTargets, functFound);
            } else {
                DetectSocialEntity notUnderReply = BotUtils.getSocialById(ConstManager.NOT_UNDERSTD);
                CurrentContext.getInstance().setDetectSocial(notUnderReply);
                return completeSentence(notUnderReply.getQuestionPattern(), "", "");
            }
        }
    }
    private static String processMode(DetectFunctionEntity functionIntent,List<TargetTernEntity> termTargets){
        CurrentContext current = CurrentContext.getInstance();
        ScriptEntity mode = BotUtils.findBestScript(termTargets);
        if (mode != null) {
            Log.d(TAG,"Tim thay mode "+mode.getName());
            current.setScript(mode);
            current.setDevice(null);
            BotUtils.implementCommand(functionIntent,null,mode);
            Log.d(TAG,"Xác nhận "+ConstManager.getVerbByIntent(functionIntent.getId())+" chế độ "+mode.getName());
            return "Xác nhận "+ConstManager.getVerbByIntent(functionIntent.getId())+" chế độ "+mode.getName();
        } else {
            Log.d(TAG,"Khong thay mode ");
            DetectSocialEntity askWhichMode = BotUtils.getSocialById(ConstManager.SOCIAL_ASK_MODE);
            return completeSentence(askWhichMode.getQuestionPattern(),
                    ConstManager.getVerbByIntent(functionIntent.getId()), "");
        }
    }
    private static String processFunction(List<TargetTernEntity> termTargets, DetectFunctionEntity functionIntent){
        CurrentContext current = CurrentContext.getInstance();
        current.setDetectedFunction(functionIntent);
        current.setDetectSocial(null);
        if (ConstManager.FUNCTION_FOR_SCRIPT.contains(functionIntent.getFunctionName())){
            return processMode(functionIntent,termTargets);
        } else if (ConstManager.FUNCTION_FOR_DEVICE.contains(functionIntent.getFunctionName())){
            AreaEntity findArea = BotUtils.findBestArea(termTargets);
            current.setArea(findArea);
            if (findArea !=null) {
                return  processDeviceInArea(functionIntent, termTargets);
            } 
            return processDeviceOnly(functionIntent,termTargets);
        } else if (functionIntent.getFunctionName().contains("check")){
            AreaEntity area = BotUtils.findBestArea(termTargets);
            if (area != null) {
                area = SmartHouse.getAreaById(area.getId());
                Log.d(TAG,"Thay phong de check"+area.getName() + "  "+functionIntent.getId());
                String resultVal = BotUtils.getAttributeByFunction(functionIntent.getId(),area);
                String replyComplete ="";
                if (resultVal == null){
                    replyComplete=completeSentence(functionIntent.getFailPattern(), resultVal, area.getName());
                } else {
                    replyComplete=completeSentence(functionIntent.getSuccessPattern(), resultVal, area.getName());
                }
                return replyComplete;
                //
            }  
            Log.d(TAG,"Khong tim thay phong");
            Log.d(TAG,functionIntent.getRemindPattern() +"");
            return completeSentence(functionIntent.getRemindPattern(),"",""); 
        } else if (functionIntent.getId() == ConstManager.SHOW_CAMERA) {
            AreaEntity area = BotUtils.findBestArea(termTargets);
            if (area != null) {
                current.setArea(area);
                Log.d(TAG,"Thay phong de check"+area.getName());

                return "Hình từ "+area.getName()+" đây";
            }
            Log.d(TAG,"Khong tim thay phong");
            return completeSentence(functionIntent.getRemindPattern(),"","");
        }else  {
            return notLearnSentence;
        }
    }
    public static String processSocial(DetectSocialEntity socialIntent){
        CurrentContext current = CurrentContext.getInstance();
        current.setDetectSocial(socialIntent);
        SmartHouse house = SmartHouse.getInstance();
        String result = "";
        Log.d(TAG, SmartHouse.getInstance().getCurrentState().getName());
        Log.d(TAG,"handle social ne" +socialIntent.getName());
        switch (socialIntent.getId()){
            case ConstManager.SOCIAL_WHAT_TIME:
                result = ConstManager.getTime(); break;
            case ConstManager.SOCIAL_WHAT_DAY:
                result = ConstManager.getDay(); break;
            case ConstManager.SOCIAL_WHAT_SEX:
                result = "con gái"; break;
            case ConstManager.OWNER_LEAVE:
                house.setCurrentState(house.getStateById(ConstManager.NO_BODY_HOME_STATE));
                CurrentContext.getInstance().renew();
//                Log.d(TAG," no body "+ house.getCurrentState().getName());
                break;
            case ConstManager.SOCIAL_AGREE:
                 if (SmartHouse.getInstance().getCurrentState().getId() == ConstManager.NO_BODY_HOME_STATE) {
                     Log.d(TAG, "owner leave turn off foaulll");
                     SmartHouse.getInstance().turnOffAll();
                     break;
                 } else
                if (current.getDetectedFunction() != null ){
                    if (current.getDevice() != null) {
                        implementCommand(current.getDetectedFunction(), current.getDevice(), null);
                        String res = "Xác nhận " + ConstManager.getVerbByIntent(current.getDetectedFunction().getId()) +
                                " " + current.getDevice().getName()+" trong "+current.getArea().getName();
                        return res;
                    } else  if (current.getScript() != null) {
                        if (current.isSchedulerMode()) {
                            current.getScript().setEnabled(false);
                            SmartHouse.getInstance().disableToday(current.getScript().getId());
                            if (current.getScript().isOnlyOneTime()) {
                                ScriptSQLite.deleteModeById(current.getScript().getId());
                            }
                            implementCommand(current.getDetectedFunction(),null,current.getScript());
                            Log.d(TAG,"Scheduler acted");
                        }
                        implementCommand(current.getDetectedFunction(), null, current.getScript());
                        String res  ="Xác nhận " + ConstManager.getVerbByIntent(current.getDetectedFunction().getId()) +
                                " chế độ " + current.getScript().getName();
                        return res;
                    }
                } else {
                    DetectSocialEntity notUnderReply = BotUtils.getSocialById(ConstManager.NOT_UNDERSTD);
                    current.setDetectSocial(notUnderReply);
                    return completeSentence(notUnderReply.getQuestionPattern(), "", "");
                }
            case ConstManager.SOCIAL_DENY:
                if (current.getDetectedFunction() != null ){
                   current.renew();
                    return completeSentence(socialIntent.getReplyPattern(),"","");
                } else {
                    DetectSocialEntity notUnderReply = BotUtils.getSocialById(ConstManager.NOT_UNDERSTD);
                    current.setDetectSocial(notUnderReply);
                    return completeSentence(notUnderReply.getQuestionPattern(), "", "");
                }
        }
        return completeSentence(socialIntent.getReplyPattern(), result, "");
    }
    public static Map<String,Map<String,Integer>> updateFuncts(List<OwnerTrainEntity> trained, Map<String, Map<String, Integer>> functionMap) {
        for (OwnerTrainEntity train : trained){
            String []words = train.getWords().split("( )+");
            Map<String,Integer> currentCount = functionMap.get(train.getName());
            if (currentCount != null) {
                for (int i = 0; i < words.length; i++) {
                    Integer currWordCout = currentCount.get(words[i]);
                    currentCount.put(words[i], (currWordCout != null) ? currWordCout + 1 : 1);
                }
                functionMap.put(train.getName(), currentCount);
            }else {
                Log.d(TAG, "Owner dạy ngu quá");
            }
        }
        return functionMap;
    }

}

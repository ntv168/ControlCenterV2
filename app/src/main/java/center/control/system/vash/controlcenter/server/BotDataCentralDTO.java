package center.control.system.vash.controlcenter.server;

import java.util.List;
import java.util.Map;

/**
 * Created by Thuans on 6/26/2017.
 */
public class BotDataCentralDTO {

    List<SocialIntentDTO> socials;
    List<FunctionIntentDTO> functions;
    Map<String, Map<String, Integer>> socialMap;
    Map<String, Map<String, Integer>> functionMap;

    public Map<String, Map<String, Integer>> getSocialMap() {
        return socialMap;
    }

    public void setSocialMap(Map<String, Map<String, Integer>> socialMap) {
        this.socialMap = socialMap;
    }

    public Map<String, Map<String, Integer>> getFunctionMap() {
        return functionMap;
    }

    public void setFunctionMap(Map<String, Map<String, Integer>> functionMap) {
        this.functionMap = functionMap;
    }

    public List<SocialIntentDTO> getSocials() {
        return socials;
    }

    public void setSocials(List<SocialIntentDTO> socials) {
        this.socials = socials;
    }

    public List<FunctionIntentDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionIntentDTO> functions) {
        this.functions = functions;
    }
}

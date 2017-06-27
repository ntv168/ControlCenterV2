package center.control.system.vash.controlcenter.server;

import java.util.List;

/**
 * Created by Thuans on 6/26/2017.
 */
public class BotDataCentralDTO {
    List<TermDTO> termDTOS;
    List<SocialIntentDTO> socials;
    List<FunctionIntentDTO> functions;

    public List<TermDTO> getTermDTOS() {
        return termDTOS;
    }

    public void setTermDTOS(List<TermDTO> termDTOS) {
        this.termDTOS = termDTOS;
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

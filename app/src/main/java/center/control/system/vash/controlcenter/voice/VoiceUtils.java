package center.control.system.vash.controlcenter.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Sam on 4/20/2017.
 */

public class VoiceUtils {
    private static VoiceUtils singleton;
    private String content;
    private TextToSpeech tts;
    private OnSpeakFinish listener;

    public static synchronized void initializeInstance(final Context context, OnSpeakFinish listener) {
        if (singleton == null) {
            singleton = new VoiceUtils();
            singleton.listener = listener;
            singleton.tts = new TextToSpeech(context,
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status == TextToSpeech.SUCCESS) {
                                singleton.tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                    @Override
                                    public void onStart(String utteranceId) {
//                                        Log.d("voice utls","startttt");
                                    }

                                    @Override
                                    public void onDone(String utteranceId) {
                                        singleton.listener.onFinish();
//                                        Log.d("voice utls","xonggggg");
                                    }

                                    @Override
                                    public void onError(String utteranceId) {
//                                        Log.d("voice utls","errrrrr");
                                    }
                                });
                                singleton.tts.setLanguage(new Locale("vi","VN"));
                                Log.d("VOICEUTILSSSS","ok");
                                singleton.listener.onInitFinish();
                            }else {
                                Log.d("VOICEUTIL","Khong noi duoc roi");
                            }
                        }
                    });
        }
    }
    public static VoiceUtils getInstance() {
        return singleton;
    }

    private VoiceUtils() {
    }


    public static  void speak(String sentence){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"1");
        singleton.tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, map);
//         singleton.tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static void stopSpeakApi() {
        if (singleton.tts != null){
            singleton.tts.stop();
            singleton.tts.shutdown();
        }
        singleton = null;
    }
    public interface OnSpeakFinish{
        public void onFinish();
        public void onInitFinish();
    }
}

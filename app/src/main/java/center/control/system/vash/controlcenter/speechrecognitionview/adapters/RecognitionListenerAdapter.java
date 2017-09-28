package center.control.system.vash.controlcenter.speechrecognitionview.adapters;

import android.os.Bundle;
import android.speech.RecognitionListener;

/**
 * Created by Sam on 9/27/2017.
 */

public class RecognitionListenerAdapter implements RecognitionListener {
    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int error) {
    }

    @Override
    public void onResults(Bundle results) {
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }
}

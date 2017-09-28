package center.control.system.vash.controlcenter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import center.control.system.vash.controlcenter.R;
import center.control.system.vash.controlcenter.speechrecognitionview.RecognitionProgressView;
import center.control.system.vash.controlcenter.speechrecognitionview.adapters.RecognitionListenerAdapter;

public class SpeechDialogClass extends Dialog {

    private SpeechRecognizer speechRecognizer;

    public SpeechDialogClass(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_recognizer_dialog);
        int[] colors = {
                ContextCompat.getColor(getContext(), R.color.color1),
                ContextCompat.getColor(getContext(), R.color.color2),
                ContextCompat.getColor(getContext(), R.color.color3),
                ContextCompat.getColor(getContext(), R.color.color4),
                ContextCompat.getColor(getContext(), R.color.color5)
        };

        int[] heights = { 20, 24, 18, 23, 16 };



        final RecognitionProgressView recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                showResults(results);
            }
        });
        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.setCircleRadiusInDp(2);
        recognitionProgressView.setSpacingInDp(2);
        recognitionProgressView.setIdleStateAmplitudeInDp(2);
        recognitionProgressView.setRotationRadiusInDp(10);
        recognitionProgressView.play();

        Button listen = (Button) findViewById(R.id.listen);
        Button reset = (Button) findViewById(R.id.reset);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecognition();
                recognitionProgressView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startRecognition();
                    }
                }, 50);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognitionProgressView.stop();
                recognitionProgressView.play();
            }
        });
    }

    private void startRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"vi");
        speechRecognizer.startListening(intent);
    }

    private void showResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Toast.makeText(getContext(), matches.get(0), Toast.LENGTH_LONG).show();
    }

}

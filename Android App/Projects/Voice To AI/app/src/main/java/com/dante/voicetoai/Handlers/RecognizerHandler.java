package com.dante.voicetoai.Handlers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.widget.Toast;


import com.dante.voicetoai.Methods.RequestSender;

import java.util.ArrayList;
import java.util.Locale;

public class RecognizerHandler {

    Activity BaseActivity;
    Context ActivityContext;

    RequestSender RqSender;

    //Constructor initialized
    public RecognizerHandler(Activity activity, Context context) {
        /* APP RELATIF */
        this.BaseActivity = activity;
        this.ActivityContext = context;

        /* CLASS RELATIF */
        this.RqSender = new RequestSender(this.ActivityContext);
    }


    public void start(){
        Intent recognize_intent = this.getIntent();

        try {
            // start recognition
            this.BaseActivity.startActivityForResult(recognize_intent, 666);
            Toast.makeText(this.ActivityContext, "En écoute ...", Toast.LENGTH_SHORT).show();

        } catch (ActivityNotFoundException a) {
            Toast.makeText(this.ActivityContext, "La reconnaissance vocale n'est pas supporté sur l'appareil.", Toast.LENGTH_SHORT).show();
        }

    }


    public Intent getIntent() {
        // intent recognizer
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, " ");

        return intent;

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case 666: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String msg = result.get(0);

                    // Envoi au serveur
                    this.RqSender.sendRequest(msg.toString());

                    Toast.makeText(this.ActivityContext, "Envoie de : " + msg.toString(), Toast.LENGTH_LONG).show();

                }
                break;
            }
        }
    }

}
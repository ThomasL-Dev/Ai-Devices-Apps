package com.dante.ai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.startService(new Intent(this, Socket_Service.class));

        // Toast.makeText(this, "app started", Toast.LENGTH_SHORT).show();

        recognizer();


    }

    private void recognizer() {
        // intent recognizer
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                " ");
        try {
            // start recognition
            startActivityForResult(intent, 666);
            //Toast.makeText(getApplicationContext(), "start recognition.", Toast.LENGTH_SHORT).show();

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Désolé! Speech recognition n'est pas supporté sur l'appareil.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 666: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String msg = result.get(0);

                    // Envoi au serveur
                    SendMessage send = new SendMessage();
                    send.setMsg(msg);
                    send.execute();

                    Toast.makeText(getApplicationContext(), "Envoie de : " + msg, Toast.LENGTH_LONG).show();

                }
                break;
            }
        }
    }
}
package com.dante.voicetoai;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.dante.voicetoai.Handlers.RecognizerHandler;
import com.dante.voicetoai.Services.ServerConnexionService;

public class MainActivity extends AppCompatActivity {

    Context BASE_CONTEXT;

    RecognizerHandler recognizer_handler;

    Button btnOptions;
    ImageButton recognizer_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Constuct App ---
        this.BASE_CONTEXT = getApplicationContext();

        // init ui functions
        this.initOptionsBtn();
        this.initRecognizerBtn();

        // start services
        this.startServices();

        // start voice recognition
        recognizer_handler = new RecognizerHandler(this, getApplicationContext());
        recognizer_handler.start();




    }

    private void startServices(){
        this.startService(new Intent(this, ServerConnexionService.class));
    }


    private void initRecognizerBtn(){
        this.recognizer_btn = findViewById(R.id.recognizer_btn);
        this.recognizer_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recognizer_handler = new RecognizerHandler(MainActivity.this, getApplicationContext());
                recognizer_handler.start();
            }
        });
    }


    private void initOptionsBtn(){
        this.btnOptions = findViewById(R.id.options_btn);
        this.btnOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent options_activity = new Intent(BASE_CONTEXT, OptionsActivity.class);
                startActivity(options_activity);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        recognizer_handler.onActivityResult(requestCode, resultCode, result);
    }


}
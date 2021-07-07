package com.dante.jarvis;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;

public class ConsoleActivity extends Activity {

    TextView entryMsg;
    ImageButton sendBtn;

    public String MSG_TO_SEND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        entryMsg = findViewById(R.id.entrySend);
        sendBtn = findViewById(R.id.btnSend);

        // envoie lors de lappuie onClick
        entryMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSG_TO_SEND = entryMsg.getText().toString();

                // Envoi au serveur
                SendMessage send = new SendMessage();
                send.execute();

                entryMsg.setText("");
            }
        });

        // envoie lors de lappuie sur le bouton
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSG_TO_SEND = entryMsg.getText().toString();

                // Envoi au serveur
                SendMessage send = new SendMessage();
                send.execute();

                entryMsg.setText("");
            }
        });

        //Intent intent = getIntent();
        //String ip = intent.getStringExtra("ipKey"); //if it's a string you stored.
        //String port = intent.getStringExtra("portKey"); //if it's a string you stored.


    }// --- Fin OnCreate ---


    ///////////////////////////////
    // class pour envoyer au server
    class SendMessage extends AsyncTask<Void,Void,Void> {
        PrintWriter pw;

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // envoie du message
                                    // on recup le socket pour envoyer
                pw = new PrintWriter(SocketHandler.getSocket().getOutputStream());
                pw.write(MSG_TO_SEND);
                pw.flush();

            } catch (UnknownHostException e) {
                System.out.println("Fail");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Fail");
                e.printStackTrace();
            }
            return null;
        }
    }

}// ------------------------------

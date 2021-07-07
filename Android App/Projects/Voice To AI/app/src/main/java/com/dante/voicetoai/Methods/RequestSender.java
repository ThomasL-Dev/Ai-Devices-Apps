package com.dante.voicetoai.Methods;

import android.content.Context;
import android.os.AsyncTask;

import com.dante.voicetoai.Handlers.SocketHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class RequestSender extends AsyncTask<Void,Void,Void> {

    Context ActivityContext;
    Socket socketServer;
    String MSG_TO_SEND;
    PrintWriter pw;

    public RequestSender(Context context){
        this.ActivityContext = context;
    }

    @Override
    protected Void doInBackground(Void...params){
        try {

            // envoie du message
            socketServer = SocketHandler.getSocket();
            pw = new PrintWriter(socketServer.getOutputStream());
            pw.write(MSG_TO_SEND);
            pw.flush();

        }catch (Exception _e){
            try {

                Memory server_addr_memory = new Memory(this.ActivityContext);
                String memory_url_value = server_addr_memory.getServerAddrMemory().getString(Memory.SERVER_URL_KEY, "http://localhost");
                String user_token_value = server_addr_memory.getServerAddrMemory().getString(Memory.USER_TOKEN_KEY, "token");

                if (memory_url_value.endsWith("/")){
                    // do nothing
                    memory_url_value += "api/make/request";
                }else {
                    memory_url_value += memory_url_value + "/";
                    memory_url_value += "api/make/request";
                }

                memory_url_value += "?token=" + user_token_value;
                memory_url_value += "&input=" + MSG_TO_SEND;


                URL url = new URL(memory_url_value);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                }
                finally {
                    urlConnection.disconnect();
                }


            }catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sendRequest(String string){
        MSG_TO_SEND = string;
        this.execute();
    }

}

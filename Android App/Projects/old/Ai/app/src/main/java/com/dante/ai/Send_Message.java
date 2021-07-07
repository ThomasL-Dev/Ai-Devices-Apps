package com.dante.ai;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendMessage extends AsyncTask<Void,Void,Void> {

    Socket socketServer;
    String MSG_TO_SEND;
    PrintWriter pw;

    @Override
    protected Void doInBackground(Void...params){
        try {

            // envoie du message
            socketServer = Socket_Handler.getSocket();
            pw = new PrintWriter(socketServer.getOutputStream());
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

    public void setMsg(String string){
        MSG_TO_SEND = string;
    }
}

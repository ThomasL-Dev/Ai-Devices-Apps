package com.dante.ai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SocketHandler;

public class Socket_Service extends Service {

    ///////////////////////
    // -- SOCKET CLIENT --
    // Socket
    public Socket socketServer;
    // ip & port du server
    public String SERVER_IP;
    public int SERVER_PORT;


    /////////////////
    // -- MEMOIRE --
    SharedPreferences ADRESSE_SERVER_MEMORY;
    // ip & port
    public static final String SERVER_ADRESSE_KEY = "serverAddresse";
    public static final String IP_KEY = "ipKey";
    public static final String PORT_KEY = "portKey";


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android


        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();


        Toast.makeText(getApplicationContext(), "Connecting to server ...", Toast.LENGTH_SHORT).show();

        //////////////////////////////
        // --- restore les OPTIONS ---
        // - Server
        ADRESSE_SERVER_MEMORY = getSharedPreferences(SERVER_ADRESSE_KEY, Context.MODE_PRIVATE);

        if (ADRESSE_SERVER_MEMORY.contains(IP_KEY)) {
            Toast.makeText(this, ADRESSE_SERVER_MEMORY.getString(IP_KEY, ""), Toast.LENGTH_SHORT).show();
        }

        if (ADRESSE_SERVER_MEMORY.contains(PORT_KEY)) {
            Toast.makeText(this, String.valueOf(ADRESSE_SERVER_MEMORY.getInt(PORT_KEY, 0)), Toast.LENGTH_SHORT).show();
        }


        /////////////////////////////
        // --- Connexion au server ---
        SERVER_IP = ADRESSE_SERVER_MEMORY.getString(IP_KEY, "192.168.1.15");
        SERVER_PORT = ADRESSE_SERVER_MEMORY.getInt(PORT_KEY, 33000);

        new Thread(new Runnable() {
            public void run() {
                try {
                    // connexion au server
                    socketServer = new Socket(SERVER_IP, SERVER_PORT);
                    Socket_Handler.setSocket(socketServer);
                    System.out.println("[SOCKET SERVICE] : Connected to server '" + SERVER_IP + ":" + SERVER_PORT + "'");


                } catch (UnknownHostException e) {
                    System.out.println("[SOCKET SERVICE] : Cannot connect to server '" + SERVER_IP + ":" + SERVER_PORT + "'");

                    e.printStackTrace();

                } catch (IOException e) {
                    System.out.println("[SOCKET SERVICE] : Cannot connect to server '" + SERVER_IP + ":" + SERVER_PORT + "'");

                    e.printStackTrace();
                }
            }
        }).start();


        //start a separate thread and start listening to your network object
    }


    @Override
    public void onDestroy() {
        // DO NOTHING ON DESTROYING

    }


}
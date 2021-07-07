package com.dante.voicetoai.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.dante.voicetoai.Handlers.SocketHandler;
import com.dante.voicetoai.Methods.Memory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnexionService extends Service {

    // socket
    public Socket SocketConnServer;

    // ip & port du server
    public String SERVER_IP;
    public int SERVER_PORT;

    Memory server_addr_memory;



    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("[SERVER CONNEXION SERVICE]" + " : Started");
        Toast.makeText(getApplicationContext(), "Connecting to server ...", Toast.LENGTH_SHORT).show();

        server_addr_memory = new Memory(this.getApplicationContext());
        server_addr_memory.getServerAddrMemory();

        SERVER_IP = server_addr_memory.getServerAddrMemory().getString(Memory.SERVER_IP_KEY, "0.0.0.0");
        SERVER_PORT = server_addr_memory.getServerAddrMemory().getInt(Memory.SERVER_PORT_KEY, 0);

        new Thread(new Runnable() {

            public void run() {
                try {
                    // connexion au server
                    SocketConnServer = new Socket(SERVER_IP, SERVER_PORT);
                    SocketHandler.setSocket(SocketConnServer);
                    System.out.println("[SERVER CONNEXION SERVICE]" + " : Connected to server '" + SERVER_IP + ":" + SERVER_PORT + "'");


                } catch (UnknownHostException e) {
                    System.out.println("[SERVER CONNEXION SERVICE]" + " : Cannot connect to server '" + SERVER_IP + ":" + SERVER_PORT + "'");

                    e.printStackTrace();

                } catch (IOException e) {
                    System.out.println("[SERVER CONNEXION SERVICE]" + " : Cannot connect to server '" + SERVER_IP + ":" + SERVER_PORT + "'");

                    e.printStackTrace();

                } catch (Exception e) {
                    System.out.println("[SERVER CONNEXION SERVICE]" + " : Cannot connect to server '" + SERVER_IP + ":" + SERVER_PORT + "'");
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onDestroy() {
        // DO NOTHING ON DESTROYING
    }



    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);

    }

}
package com.dante.voicetoai.Methods;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Memory {

    Context context;

    SharedPreferences SERVER_ADDR;

    // KEYS
    public static final String SERVER_ADDR_KEY = "server_addr_key";
    public static final String SERVER_IP_KEY = "server_ip_key";
    public static final String SERVER_PORT_KEY = "server_port_key";
    public static final String SERVER_URL_KEY = "server_url_key";
    public static final String USER_TOKEN_KEY = "user_token_key";





    public Memory(Context context){
        this.context = context;
    }


    public void updateServerAddrValue(String ip, Integer port, String url, String token){
        SharedPreferences.Editor serv_editor = SERVER_ADDR.edit();
        serv_editor.putString(SERVER_IP_KEY, ip);
        serv_editor.putInt(SERVER_PORT_KEY, port);
        serv_editor.putString(SERVER_URL_KEY, url);
        serv_editor.putString(USER_TOKEN_KEY, token);
        serv_editor.commit();
    }


    public SharedPreferences getServerAddrMemory(){
        SERVER_ADDR = this.context.getSharedPreferences(SERVER_ADDR_KEY, Context.MODE_PRIVATE);
        return SERVER_ADDR;
    }





}

package com.dante.ai;

import java.net.Socket;

public class Socket_Handler {

    private static Socket socket;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        Socket_Handler.socket = socket;
    }


}

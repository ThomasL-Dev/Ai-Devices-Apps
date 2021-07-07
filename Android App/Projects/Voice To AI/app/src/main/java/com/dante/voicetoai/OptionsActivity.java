package com.dante.voicetoai;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dante.voicetoai.Methods.Memory;

public class OptionsActivity extends AppCompatActivity {

    Memory server_addr_memory;

    Button btnUpdate;
    Button close_opt_btn;

    EditText editIp;
    EditText editPort;
    EditText editUrl;
    EditText editToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);

        // set width of floating activty to match parent
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // init widgets
        btnUpdate = findViewById(R.id.update_btn);
        close_opt_btn = findViewById(R.id.close_opt_btn);

        editIp = findViewById(R.id.editIp);
        editPort = findViewById(R.id.editPort);
        editUrl = findViewById(R.id.editUrl);
        editToken = findViewById(R.id.editToken);

        server_addr_memory = new Memory(getApplicationContext());

        // get ip & port values
        String memory_ip_value = server_addr_memory.getServerAddrMemory().getString(Memory.SERVER_IP_KEY, "0.0.0.0");
        Integer memory_port_value = server_addr_memory.getServerAddrMemory().getInt(Memory.SERVER_PORT_KEY, 0);
        String memory_url_value = server_addr_memory.getServerAddrMemory().getString(Memory.SERVER_URL_KEY, "http://localhost");
        String user_token_value = server_addr_memory.getServerAddrMemory().getString(Memory.USER_TOKEN_KEY, "token");

        // set values in inputtext
        this.setAddrInputText(memory_ip_value, memory_port_value, memory_url_value, user_token_value);

        this.initUpdateBtn();
        this.initCloseBtn();
    }


    public void setAddrInputText(String ip, int port, String url, String token){
        editIp.setText(ip);
        editPort.setText(String.valueOf(port));
        editUrl.setText(url);
        editToken.setText(token);

    }


    private void initCloseBtn(){
        close_opt_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OptionsActivity.this.finish();
            }
        });
    }

    private void initUpdateBtn(){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String ip_value = editIp.getText().toString();
                Integer port_value = Integer.parseInt(editPort.getText().toString());
                String url_value = editUrl.getText().toString();
                String token_value = editToken.getText().toString();

                server_addr_memory.updateServerAddrValue(ip_value, port_value, url_value, token_value);

                OptionsActivity.this.restart_app();

            }
        });
    }

    public void restart_app(){
        Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
        System.exit(0);
    }

}
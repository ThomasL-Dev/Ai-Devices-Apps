package com.dante.jarvis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppsListActivity extends Activity {
    
    ListView layout;
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);


        //new GetListAppThreaded(getApplicationContext()).loadInBackground();
        // load
        loadApps();
        loadListView();
        // click
        addClickListener();
        addOnLongClickListener();
    }// ---------- FIN ONCREATE ---------


    private PackageManager manager;
    private List<AppDetail> apps;
    private void loadApps(){
        manager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            if (!app.name.toString().equals(getApplicationContext().getPackageName().toString())){
                apps.add(app);
            }else {}

        }
    }


    private ListView list;
    private void loadListView(){
        list = findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, R.layout.list_item, apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appLabel = convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);

                TextView appName = convertView.findViewById(R.id.item_app_name);
                appName.setText(apps.get(position).name);

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }




    private void addOnLongClickListener(){
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // trying to create shortcut
                //Toast.makeText(AppsListActivity.this, "long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                AppsListActivity.this.startActivity(i);
            }
        });
    }


    // -- TEST --



    
    // TEST THREADING
    public class GetListAppThreaded extends AsyncTaskLoader<ArrayList<AppDetail>> {
        private PackageManager manager;
        private List<AppDetail> apps;

        final PackageManager mPm;

        public GetListAppThreaded(@NonNull Context context) {
            super(context);
            mPm = context.getPackageManager();
        }

        @Nullable
        @Override
        public ArrayList<AppDetail> loadInBackground() {
            manager = getPackageManager();
            apps = new ArrayList<AppDetail>();

            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities) {
                AppDetail app = new AppDetail();
                app.label = ri.loadLabel(manager);
                app.name = ri.activityInfo.packageName;
                app.icon = ri.activityInfo.loadIcon(manager);
                if (!app.name.toString().equals(getApplicationContext().getPackageName().toString())) {
                    apps.add(app);
                } else {
                }
            }
            return null;
        }
    }



}



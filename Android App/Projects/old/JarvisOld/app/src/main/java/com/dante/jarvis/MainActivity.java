package com.dante.jarvis;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.StateListDrawable;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GestureDetectorCompat;

public class MainActivity extends Activity{

	// -- DECLARE WIDGET (Boutton, text, image etc ... )
	HomeView mainLayout;
	ConstraintLayout bgLayout;
	// - TITLE
	TextView labelHour;
	TextView labelKbps;
	// - Mainframe
	TextView labelConnectedTo;
	TextView labelMs;
	// - Sliding Options
	SlidingDrawer slidingDrawerOption;
	EditText entryIpOption;
	EditText entryPortOption;
	ImageButton saveButtonOption;
	Switch switchWallpaper;
	ImageButton btnConsole;
	// - SlidingDrawer Console
	//ImageButton sendButtonConsole;
	//EditText entryConsole;


	///////////////////////
	// -- SOCKET CLIENT --
	// Socket
	public Socket socketServer;
	// message a envoyé au server
	public String MSG_TO_SEND;
	// ip & port du server
	public String SERVER_IP;
	public int SERVER_PORT;

	/////////////////
	// -- MEMOIRE --
	SharedPreferences SERVER_MEMORY;
	SharedPreferences WALLPAPER_MEMORY;
	// ip & port
	public static final String SERVER_ADRESSE_KEY = "serverAddresse";
	public static final String IP_KEY = "ipKey";
	public static final String PORT_KEY = "portKey";
	// Wallpaper
	public static final String WALLPAPER_KEY = "wallpaper";
	public static final String ACTIVE_WallPaper_KEY = "acitveKey";


	///////////////////
	// -- APP DRAWER --
	DrawerAdapter drawerAdapterObject;
	GridView drawerGrid;
	@SuppressWarnings("deprecation")
	SlidingDrawer slidingDrawer;
	Pac[] pacs;
	PackageManager pm;
	AppWidgetManager mAppWidgetManager;
	LauncherAppWidgetHost mAppWidgetHost;
	int REQUEST_CREATE_APPWIDGET = 900;
	int REQUEST_CREATE_SHORTCUT  = 700;
	int numWidgets;
	SharedPreferences globalPrefs;
	static Activity activity;
	
	static boolean appLaunchable = true;

	// TEST



	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// -- check perimmssions
		ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
		
		activity = this;
		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mAppWidgetHost = new LauncherAppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
		globalPrefs    = PreferenceManager.getDefaultSharedPreferences(this);


		// --- INIT WIDGET (Boutton, text, image etc ... ) ---
		mainLayout = findViewById(R.id.home_view); // mainlayout customview home
		// - TITLE
		labelKbps = findViewById(R.id.kbpsLabel);
		labelHour = findViewById(R.id.timeLabel);
		//// - Mainfrme
		labelMs = findViewById(R.id.labelMs);
		labelConnectedTo = findViewById(R.id.labelConnectedTo);
		// - Sliding Console
		//sendButtonConsole = findViewById(R.id.sendButtonConsole);
		//entryConsole = findViewById(R.id.entryConsole);
		// - Sliding Option
		slidingDrawerOption = findViewById(R.id.slidingDrawerOption);
		saveButtonOption = findViewById(R.id.saveButtonOption);
		entryIpOption = findViewById(R.id.entryIpOption);
		entryPortOption = findViewById(R.id.entryPortOption);
		switchWallpaper = findViewById(R.id.switchWallpaper);
		btnConsole = findViewById(R.id.btnConsole);


		//////////////////////////////
		// --- MAINLAYOUT LISTENER ---
		mainLayout.setOnTouchListener(new OnDoubleTapListener(this) {
			@Override
			public void onDoubleTap(MotionEvent e) {
				recognizer();
			}
		});


		//////////////////////////////
		// --- restore les OPTIONS ---
		// - Server
		SERVER_MEMORY = getSharedPreferences(SERVER_ADRESSE_KEY, Context.MODE_PRIVATE);
		if (SERVER_MEMORY.contains(IP_KEY)) {
			entryIpOption.setText(SERVER_MEMORY.getString(IP_KEY, ""));
		}
		if (SERVER_MEMORY.contains(PORT_KEY)) {
			entryPortOption.setText(String.valueOf(SERVER_MEMORY.getInt(PORT_KEY, 0)));
		}
		// - wallpaper
		WALLPAPER_MEMORY = getSharedPreferences(WALLPAPER_KEY, Context.MODE_PRIVATE);
		if (WALLPAPER_MEMORY.contains(ACTIVE_WallPaper_KEY)) {
			if (WALLPAPER_MEMORY.getString(ACTIVE_WallPaper_KEY, "").equals("true")) {
				switchWallpaper.setChecked(true);
				if (switchWallpaper.isChecked()) {
					setWallpaper();
				}
			}
		}


		////////////////////////////////
		// AFFICHAGE & UPDATE DE LHEURE
		new Thread(new Runnable() {
			String timeofping = "";
			@Override
			public void run() {
				while (true) {
					final String currentDate = new SimpleDateFormat("EEEE dd MMMM yyyy  ", Locale.getDefault()).format(new Date());
					final String dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							labelHour.setText(currentDate + "  " + dateFormat);
						}
					});

					try {
						Thread.sleep(900);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();


		///////////////////////////////
		// AFFICHAGE & UPDATE DE  PING
		new Thread(new Runnable() {
			String timeofping = "";
			@Override
			public void run() {
				while (true) {
					Runtime runtime = Runtime.getRuntime();
					try {
						long a = System.currentTimeMillis() % 1000;
						Process ipProcess = runtime.exec("/system/bin/ping -c 1 -W 1 8.8.8.8");
						ipProcess.waitFor();
						timeofping = String.valueOf(System.currentTimeMillis() % 1000 - a);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (timeofping.startsWith("-")){
									labelMs.setText("-- ms");
								}else{
									labelMs.setText(timeofping + " ms");
								}
							}
						});
						try {
							Thread.sleep(900);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();


		////////////////////////////////////////////
		// AFFICHAGE & UPDATE DE la vitesse reseaux
		new Thread(new Runnable() {
			String mDownloadSpeedOutput;
			String mUnits;

			@RequiresApi(api = 23)
			@Override
			public void run() {

				while (true) {

					long mRxBytesPrevious = TrafficStats.getTotalRxBytes();

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					long mRxBytesCurrent = TrafficStats.getTotalRxBytes();

					long mDownloadSpeed = mRxBytesCurrent - mRxBytesPrevious;

					float mDownloadSpeedWithDecimals;

					if (mDownloadSpeed >= 1000000000) {
						mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000000000;
						mUnits = " GB";
					} else if (mDownloadSpeed >= 1000000) {
						mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000000;
						mUnits = " MB";
					} else {
						mDownloadSpeedWithDecimals = (float) mDownloadSpeed / (float) 1000;
						mUnits = " KB";
					}


					if (!mUnits.equals(" KB") && mDownloadSpeedWithDecimals < 100) {
						mDownloadSpeedOutput = String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals);
					} else {
						mDownloadSpeedOutput = Integer.toString((int) mDownloadSpeedWithDecimals);
					}

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							labelKbps.setText(String.valueOf(mDownloadSpeedOutput) + mUnits);
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();



		//////////////////////////////
		// --- Connexion au server ---
		SERVER_IP = SERVER_MEMORY.getString(IP_KEY, "0.0.0.0");
		SERVER_PORT = SERVER_MEMORY.getInt(PORT_KEY, 0);

		new Thread(new Runnable() {
			public void run() {
				try {
					// connexion au server
					socketServer = new Socket(SERVER_IP, SERVER_PORT);
					SocketHandler.setSocket(socketServer);
					labelConnectedTo.setText("Connecté à "+ SERVER_IP + ":" + SERVER_PORT);
				} catch (UnknownHostException e) {
					System.out.println("Fail");
					labelConnectedTo.setText("Impossible de se connecté à "+ SERVER_IP + ":" + SERVER_PORT);
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Fail");
					labelConnectedTo.setText("Impossible de se connecté à "+ SERVER_IP + ":" + SERVER_PORT);
					e.printStackTrace();
				}
			}
		}).start();
		Toast.makeText(getApplicationContext(), "Server Adresse sauvegardée: \n" + SERVER_IP + ":" + SERVER_PORT, Toast.LENGTH_LONG).show();



		////////////////////////////////
		// -- LISTENER SLIDER OPTIONS --
		switchWallpaper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (switchWallpaper.isChecked()){
					SharedPreferences.Editor wall_editor = WALLPAPER_MEMORY.edit();
					wall_editor.putString(ACTIVE_WallPaper_KEY, "true");
					wall_editor.commit();
					setWallpaper();
				} else{
					SharedPreferences.Editor wall_editor = WALLPAPER_MEMORY.edit();
					wall_editor.putString(ACTIVE_WallPaper_KEY, "false");
					wall_editor.commit();
					disableWallpaper();
				}

			}
		});

		saveButtonOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveServerConfig(saveButtonOption);
			}
		});

		btnConsole.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new Thread(new Runnable() {
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								slidingDrawerOption.close();
							}
						});
					}
				}).start();

				Intent intenOption = new Intent(MainActivity.this, ConsoleActivity.class);
				//myIntent.putExtra("ipKey", SERVER_IP); //Optional parameters
				//myIntent.putExtra("portKey", String.valueOf(SERVER_PORT)); //Optional parameters
				MainActivity.this.startActivity(intenOption);
			}
		});



		//////////////////////////////////////////////////
		// ------------- INIT APPS DRAWER ----------------
		drawerGrid = findViewById(R.id.contentAppdrawer);
		slidingDrawer = findViewById(R.id.appdrawer);
		pm =getPackageManager();
		new LoadApps().execute();
		addAppsToHome();
		addShortcutsToHome();
		//set_pacs(true);
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				appLaunchable=true;
			}
		});
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(new PacReceiver(), filter); 
	} // -------------------------- FIN OnCreate --------------------------------


	// TEST
	// FIN TEST


	// --- FONCTIONS ---
	public void onDestroy() {
		try {
			socketServer.close();
			labelConnectedTo.setText("Connexion fermé avec le server");
			Toast.makeText(getApplicationContext(), "Connexion fermé avec le server" , Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}



	private void setWallpaper(){
		final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		mainLayout.post(new Runnable() {
			@Override
			public void run() {
				mainLayout.setBackground(wallpaperDrawable);
			}
		});
	}



	private void disableWallpaper(){
		mainLayout.post(new Runnable() {
			@Override
			public void run() {
				mainLayout.setBackgroundColor(Color.parseColor("#101010"));
			}
		});
	}



	private void recognizer() {
		// intent recognizer
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				" ");
		try {
			// start recognition
			startActivityForResult(intent, 666);
			//Toast.makeText(getApplicationContext(), "start recognition.", Toast.LENGTH_SHORT).show();

		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(), "Désolé! Speech recognition n'est pas supporté sur l'appareil.", Toast.LENGTH_SHORT).show();
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 666: {
				if (resultCode == RESULT_OK && null != data) {
					ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					// set the message_to_server
					MSG_TO_SEND = result.get(0);

					Toast.makeText(getApplicationContext(), "Envoie de : " + MSG_TO_SEND, Toast.LENGTH_LONG).show();

					// Envoi au serveur
					SendMessage send = new SendMessage();
					send.execute();

				}
				break;
			}
		}
	}



	///////////////////////////////
	// class pour envoyer au server
	class SendMessage extends AsyncTask<Void,Void,Void> {
		PrintWriter pw;
		@Override
		protected Void doInBackground(Void...params){
			try {

				// envoie du message
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
	}



	///////////////////////////////////
	// enregistrement des config server
	public void saveServerConfig(View view) {
		SERVER_IP = entryIpOption.getText().toString();

		try {
			SERVER_PORT = Integer.parseInt(entryPortOption.getText().toString());
		}catch (Exception e){
			SERVER_PORT = 0;
		}


		SharedPreferences.Editor serv_editor = SERVER_MEMORY.edit();

		serv_editor.putString(IP_KEY, SERVER_IP);
		serv_editor.putInt(PORT_KEY, SERVER_PORT);
		serv_editor.commit();


		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), "Server Adresse enregistré ! ("+ SERVER_IP+":"+SERVER_PORT+")", Toast.LENGTH_LONG).show();
			}
		});

		// restart
		Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, mPendingIntent);
		System.exit(0);

	}


	/////////////////////////////////////////////////////////////
	// ---------------- CHECK PERMISSIONS FUNCTIONS -------------
	// Function to check and request permission.
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1: {

				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}



	////////////////////////
	// -- Launcher config --
	void selectTheme(){
		Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
		
		Intent filter = new Intent(Intent.ACTION_MAIN);
		filter.addCategory("com.anddoes.launcher.THEME");
		
		intent.putExtra(Intent.EXTRA_INTENT, filter);
		
		startActivityForResult(intent, R.id.REQUEST_PICK_THEME);
	}


	void selectShortcut(){
		Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
		intent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
		startActivityForResult(intent, R.id.REQUEST_PICK_SHORTCUT);
	}


	void selectWidget() {
	    int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
	    Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
	    pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	    addEmptyData(pickIntent);
	    startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	void addEmptyData(Intent pickIntent) {
	    ArrayList customInfo = new ArrayList();
	    pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
	    ArrayList customExtras = new ArrayList();
	    pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
	};


	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK ) {
			if (requestCode == R.id.REQUEST_PICK_APPWIDGET) {
				configureWidget(data);
			}
			else if (requestCode == REQUEST_CREATE_APPWIDGET) {
				createWidget(data);
			}
			else if (requestCode == R.id.REQUEST_PICK_SHORTCUT){
				configureShortcut(data);
			}
			else if (requestCode == REQUEST_CREATE_SHORTCUT){
				createShortcut(data);
			}
			else if (requestCode == R.id.REQUEST_PICK_THEME){
				globalPrefs.edit().putString("theme", data.getComponent().getPackageName()).commit();
				set_pacs(false);
			}

		}
		else if (resultCode == RESULT_CANCELED && data != null) {
			int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
			if (appWidgetId != -1) {
				mAppWidgetHost.deleteAppWidgetId(appWidgetId);
			}
		}
	}*/


	void configureShortcut(Intent data){
		startActivityForResult(data, REQUEST_CREATE_SHORTCUT);
	}


	public void createShortcut(Intent intent){
		Intent.ShortcutIconResource iconResource = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
		Bitmap icon                              = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
		String shortcutLabel                     = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		Intent shortIntent                       = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		
		if (icon==null){
			if (iconResource!=null){
				Resources resources =null;
				try {
					resources = pm.getResourcesForApplication(iconResource.packageName);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				} 
				if (resources != null) {
				    int id = resources.getIdentifier(iconResource.resourceName, null, null); 
				    if(resources.getDrawable(id) instanceof StateListDrawable) {
				    	Drawable d = ((StateListDrawable)resources.getDrawable(id)).getCurrent();
				    	icon = ((BitmapDrawable)d).getBitmap();
				    }else
				    	icon = ((BitmapDrawable)resources.getDrawable(id)).getBitmap();
				}
			}
		}
		

		if (shortcutLabel!=null && shortIntent!=null && icon!=null){
			ShortcutSerializableData objectData = SerializationTools.loadSerializedShortcutData();
			if (objectData == null)
				objectData = new ShortcutSerializableData();

			if (objectData.apps == null)
				objectData.apps = new ArrayList<ShortcutPac>();

			ShortcutPac pacToAdd = new ShortcutPac();
			pacToAdd.x = 100;
			pacToAdd.y = 100;
			pacToAdd.URI = shortIntent.toUri(0);
			pacToAdd.label = shortcutLabel;
			pacToAdd.icon = icon;

			if (MainActivity.activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				pacToAdd.lanscape = true;
			else
				pacToAdd.lanscape = false;

			pacToAdd.cacheIcon();
			objectData.apps.add(pacToAdd);
			pacToAdd.addToHome(this, mainLayout);
			SerializationTools.serializeShortcutData(objectData);
		}
		
	}
	
	private void configureWidget(Intent data) {
	    Bundle extras = data.getExtras();
	    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
	    AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
	    if (appWidgetInfo.configure != null) {
	        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
	        intent.setComponent(appWidgetInfo.configure);
	        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	        startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
	    } else {
	        createWidget(data);
	    }
	}
	
	public void createWidget(Intent data) {
	    Bundle extras = data.getExtras();
	    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
	    AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
	    LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
	    hostView.setAppWidget(appWidgetId, appWidgetInfo);
	    
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mainLayout.getWidth()/3, mainLayout.getHeight()/3);
	    lp.leftMargin = numWidgets * (mainLayout.getWidth()/3);
	    
	    hostView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				System.out.println("LONG PRESSED WIDGET");
				v.setBackgroundColor(Color.RED);
				return false;
			}
		});

		bgLayout.addView(hostView,lp);
	    slidingDrawer.bringToFront();
	    numWidgets ++;
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    mAppWidgetHost.startListening();
	}
	@Override
	protected void onStop() {
	    super.onStop();
	    mAppWidgetHost.stopListening();
	}
	
	public class LoadApps extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
			pacs = new Pac[pacsList.size()];
			for(int I=0;I<pacsList.size();I++){
				pacs[I]= new Pac();
				pacs[I].icon=pacsList.get(I).loadIcon(pm);
				pacs[I].packageName=pacsList.get(I).activityInfo.packageName;
				pacs[I].name=pacsList.get(I).activityInfo.name;
				pacs[I].label=pacsList.get(I).loadLabel(pm).toString();
			}
			new SortApps().exchange_sort(pacs);
			themePacs();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			if (drawerAdapterObject == null){
				drawerAdapterObject = new DrawerAdapter(activity, pacs);
				drawerGrid.setAdapter(drawerAdapterObject);
				drawerGrid.setOnItemClickListener(new DrawerClickListener(activity, pacs, pm));
				drawerGrid.setOnItemLongClickListener(new DrawerLongClickListener(activity, slidingDrawer, mainLayout, pacs));
			}else{
				drawerAdapterObject.pacsForAdapter = pacs;
				drawerAdapterObject.notifyDataSetInvalidated();
			}
		}
		
		
		
	}

	public void addShortcutsToHome(){
		ShortcutSerializableData data  = SerializationTools.loadSerializedShortcutData();
		if (data!=null){
			for (ShortcutPac pacToAddToHome : data.apps){
				pacToAddToHome.addToHome(this, mainLayout);
			}

		}
	}
	
	public void addAppsToHome(){
		AppSerializableData data  = SerializationTools.loadSerializedData();
		if (data!=null){
			for (Pac pacToAddToHome : data.apps){
				pacToAddToHome.addToHome(this, mainLayout);
			}
			
		}
	}
	
	public void set_pacs(boolean init){
		Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
		pacs = new Pac[pacsList.size()];
		for(int I=0;I<pacsList.size();I++){
			pacs[I]= new Pac();
			pacs[I].icon=pacsList.get(I).loadIcon(pm);
			pacs[I].packageName=pacsList.get(I).activityInfo.packageName;
			pacs[I].name=pacsList.get(I).activityInfo.name;
			pacs[I].label=pacsList.get(I).loadLabel(pm).toString();
		}
		new SortApps().exchange_sort(pacs);
		themePacs();
		
		if (init){
			drawerAdapterObject = new DrawerAdapter(this, pacs);
			drawerGrid.setAdapter(drawerAdapterObject);
			drawerGrid.setOnItemClickListener(new DrawerClickListener(this, pacs, pm));
			drawerGrid.setOnItemLongClickListener(new DrawerLongClickListener(this, slidingDrawer, mainLayout, pacs));
		}else{
			drawerAdapterObject.pacsForAdapter = pacs;
			drawerAdapterObject.notifyDataSetInvalidated();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void themePacs() {
	    	//theming vars-----------------------------------------------
			final int ICONSIZE = Tools.numtodp(65, MainActivity.this);
			Resources themeRes = null;
			String resPacName =globalPrefs.getString("theme", "");
			String iconResource = null;
			int intres=0;
			int intresiconback = 0;
			int intresiconfront = 0;
			int intresiconmask = 0;
			float scaleFactor = 1.0f;
			
			Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
			p.setAntiAlias(true);
			
			Paint origP = new Paint(Paint.FILTER_BITMAP_FLAG);
			origP.setAntiAlias(true);
			
			Paint maskp= new Paint(Paint.FILTER_BITMAP_FLAG);
			maskp.setAntiAlias(true);
			maskp.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
			
			if (resPacName.compareTo("")!=0){
				try{themeRes =pm.getResourcesForApplication(resPacName);}catch(Exception e){};
				if (themeRes!=null){
					String[] backAndMaskAndFront =ThemeTools.getIconBackAndMaskResourceName(themeRes,resPacName);
					if (backAndMaskAndFront[0]!=null)
						intresiconback=themeRes.getIdentifier(backAndMaskAndFront[0],"drawable",resPacName);
					if (backAndMaskAndFront[1]!=null)
						intresiconmask=themeRes.getIdentifier(backAndMaskAndFront[1],"drawable",resPacName);
					if (backAndMaskAndFront[2]!=null)
					intresiconfront=   themeRes.getIdentifier(backAndMaskAndFront[2],"drawable",resPacName);
				}
			}
			
			Options uniformOptions = new BitmapFactory.Options();
			uniformOptions.inScaled=false;
			uniformOptions.inDither=false;
			uniformOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			Canvas origCanv;
			Canvas canvas;
			scaleFactor=ThemeTools.getScaleFactor(themeRes,resPacName);
			Bitmap back=null;
			Bitmap mask=null;
			Bitmap front=null;
			Bitmap scaledBitmap = null;
			Bitmap scaledOrig = null;
			Bitmap orig = null;
			
			if (resPacName.compareTo("")!=0 && themeRes!=null){
				try{
					if (intresiconback!=0)
						back =BitmapFactory.decodeResource(themeRes,intresiconback,uniformOptions);
				}catch(Exception e){}
				try{
					if (intresiconmask!=0)
						mask = BitmapFactory.decodeResource(themeRes,intresiconmask,uniformOptions);
				}catch(Exception e){}
				try{
					if (intresiconfront!=0)
						front = BitmapFactory.decodeResource(themeRes,intresiconfront,uniformOptions);
				}catch(Exception e){}
			}
			//theming vars-----------------------------------------------		    
		    BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = false;
		    options.inPreferredConfig = Config.RGB_565;
		    options.inDither = true;
		    
		    for(int I=0;I<pacs.length;I++) {
				if (themeRes!=null){
					iconResource=null;
					intres=0;
					iconResource=ThemeTools.getResourceName(themeRes, resPacName, "ComponentInfo{"+pacs[I].packageName+"/"+pacs[I].name+"}");
					if (iconResource!=null){
						intres = themeRes.getIdentifier(iconResource,"drawable",resPacName);
					}
					
				    if (intres!=0){//has single drawable for app
				    	pacs[I].icon = new BitmapDrawable(BitmapFactory.decodeResource(themeRes,intres,uniformOptions));
					}else{
						orig=Bitmap.createBitmap(pacs[I].icon.getIntrinsicWidth(), pacs[I].icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
						pacs[I].icon.setBounds(0, 0, pacs[I].icon.getIntrinsicWidth(), pacs[I].icon.getIntrinsicHeight());
						pacs[I].icon.draw(new Canvas(orig));
					
						scaledOrig =Bitmap.createBitmap(ICONSIZE, ICONSIZE, Config.ARGB_8888);
						scaledBitmap = Bitmap.createBitmap(ICONSIZE, ICONSIZE, Config.ARGB_8888);
						canvas = new Canvas(scaledBitmap);
						if (back!=null){
							canvas.drawBitmap(back, Tools.getResizedMatrix(back, ICONSIZE, ICONSIZE), p);
						}
						
						origCanv=new Canvas(scaledOrig);
						orig=Tools.getResizedBitmap(orig, ((int)(ICONSIZE*scaleFactor)), ((int)(ICONSIZE*scaleFactor)));
						origCanv.drawBitmap(orig, scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2 ,scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2, origP);
						
						if (mask!=null){
							origCanv.drawBitmap(mask,Tools.getResizedMatrix(mask, ICONSIZE, ICONSIZE), maskp);
						}
					
						if (back!=null){
							canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);
						}else
							canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);
						
						if (front!=null)
						    canvas.drawBitmap(front,Tools.getResizedMatrix(front, ICONSIZE, ICONSIZE), p);
						
						pacs[I].icon = new BitmapDrawable(scaledBitmap);
					}
				}		
		    }

		    
		    front=null;
		    back=null;
		    mask=null;
		    scaledOrig=null;
		    orig=null;
		    scaledBitmap=null;
		    canvas=null;
		    origCanv=null;
		    p=null;
		    maskp=null;
		    resPacName=null;
		    iconResource=null;
			intres=0;
	}
	
	
	public class PacReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			new LoadApps().execute();
		}
		
	}

}

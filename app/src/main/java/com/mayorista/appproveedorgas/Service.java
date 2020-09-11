package com.mayorista.appproveedorgas;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class Service extends android.app.Service {
    //--
    private NotificationManagerCompat notificationManager;
    private App apli;
    //---
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service";
    private static Service mCurrentService;

    private int counter = 0;
    //---
    private DatabaseHelper db;
    private int id_order = 0;
    //---
    public static Socket SOCKET;
    public String HOST_NODEJS = "http://34.71.251.155:9000";

    //-----
    public static final String ACTION = "com.mayorista.appproveedorgas.mapsfragment";
    public static final String ACTION2 = "com.mayorista.appproveedorgas.pedidoactivity";

    public Service() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }

        mCurrentService = this;

        apli = new App();
        notificationManager = NotificationManagerCompat.from(this);
        if (db == null)
            this.db = new DatabaseHelper(getApplicationContext());
        if (SOCKET == null)
            InitSocketIO();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "restarting Service !!");
        counter = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }
        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }
        */
        if (db == null)
            this.db = new DatabaseHelper(getApplicationContext());

        //startTimer();

        if (SOCKET == null)
            InitSocketIO();
        //----check data

        if (intent.getExtras() != null) {
            String evento = intent.getStringExtra("event");
            String data = intent.getStringExtra("data");
            Log.d("Data Service", evento + " " + data);
            try {
                SOCKET.emit(evento, new JSONObject(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        //return START_STICKY;
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Service socket", "This is the service's notification connection", R.drawable.ic_directions_bike_wihte_24dp));
                Log.i(TAG, "restarting foreground successful");
                if (SOCKET == null)
                    InitSocketIO();
                //startTimer();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        //stoptimertask();
    }


    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    //private static Timer timer;
    //private static TimerTask timerTask;
    //long oldTime = 0;
    /*
    public void startTimer() {
        Log.i(TAG, "Starting timer");
        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        Log.i(TAG, "Scheduling...");
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }
     */

    /**
     * it sets the timer to print the counter every x seconds
     */
    /*
    public void initializeTimerTask() {
        Log.i(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }
     */

    //------- socket
    //---------
    void InitSocketIO() {
        final JSONObject json_connect = new JSONObject();
        IO.Options opts = new IO.Options();
        // opts.forceNew = true;
        opts.reconnection = true;
        opts.query = "auth_token=thisgo77";
        try {
            json_connect.put("ID", "US01");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            SOCKET = IO.socket(HOST_NODEJS, opts);
            SOCKET.connect();
            // SOCKET.io().reconnectionDelay(10000);
            Log.d(TAG, "Node connect ok");
            //conect();
        } catch (URISyntaxException e) {
            Log.d(TAG, "Node connect error");
        }

        SOCKET.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //SOCKET.emit("new connect", json_connect);
                conect();
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER connect " + date);
            }
        });

        SOCKET.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER disconnect " + date);
            }
        });

        SOCKET.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER reconnect " + my_date);
            }
        });

        SOCKET.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER timeout " + my_date);
            }
        });

        SOCKET.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER reconnecting " + my_date);
            }
        });
        SOCKET.on("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    String msg = jsonObject.getString("message");
                    Log.d(TAG, jsonObject.toString());
                    if (msg.equals("ok")) {
                        Log.d(TAG, "AUTH ok");
                        sentDataToast("Conectado");
                    } else {
                        Log.e(TAG, "AUTH error");
                        sentDataToast("Verifique Conexion");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("send order", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {

                    //String msg = jsonObject.getString("message");
                    Log.d(TAG, "send Order: " + jsonObject.toString());
                    //JSONObject data = jsonObject.getJSONObject("data");
                    double lat = jsonObject.getDouble("latitude");
                    double lng = jsonObject.getDouble("longitude");
                    //final LatLng latLng = new LatLng(lat,lng);
                    int id = jsonObject.getInt("order_id");
                    int time = jsonObject.getInt("time");
                    int dis = jsonObject.getInt("distance");
                    List<Mpedido_detalle> det = new ArrayList<>();
                    JSONArray detalle = jsonObject.getJSONArray("order_detail");
                    for (int i = 0; i < detalle.length(); i++) {
                        JSONObject d = detalle.getJSONObject(i);
                        Mpedido_detalle ped = new Mpedido_detalle(
                                d.getInt("quantity"),
                                d.getJSONObject("product_id").getString("description"),
                                d.getDouble("unit_price")
                        );
                        det.add(ped);
                    }
                    //---
                    showAlert(lat, lng, id, time, dis, det);
                    //------
                    agregarPedido(lat, lng, id);
                    //------
                    /*
                    if(getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                agregarPedido(latLng,id,true);
                            }
                        });
                    }
                    else{
                    }
                    */
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        SOCKET.on("confirm order provider", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {

                    int sts = jsonObject.getInt("status");
                    confirm_order(sts);

                    Log.d(TAG, "Confirm Order: " + jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("cancel order provider", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                Log.d(TAG, "Cancel Order: " + jsonObject.toString());
                try {
                    int status = jsonObject.getInt("status");
                    cancel_order(status);
                    sentDataToast("Pedido cancelado");
                    mostrarAlertaCancelado(
                            jsonObject
                                    .getJSONObject("data")
                                    .getJSONObject("order_id")
                                    .getInt("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("delivered order provider", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                Log.d(TAG, "delivered Order: " + jsonObject.toString());
                try {
                    int status = jsonObject.getInt("status");
                    delivered_order(status);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    //-----
    private void conect() {
        Log.d(TAG, "emitiendo new conect");
        JSONObject data = new JSONObject();
        account cuenta = db.getAcountToken();
        try {
            data.put("ID", cuenta.getDni());
            data.put("type", "provider");
            data.put("company_id", cuenta.getCompany_id());
            Log.d(TAG, "conect " + data.toString());
            SOCKET.emit("new connect", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //---
    private void sentDataToast(String mensaje) {
        Intent toastIntent = new Intent(ACTION);
        toastIntent.putExtra("toastMessage", mensaje);
        sendBroadcast(toastIntent);
    }

    //--
    private void agregarPedido(double lat, double lng, int id) {
        Intent toastIntent = new Intent(ACTION);
        toastIntent.putExtra("agregarmPedido", "si");
        toastIntent.putExtra("lat", String.valueOf(lat));
        toastIntent.putExtra("lng", String.valueOf(lng));
        toastIntent.putExtra("id", String.valueOf(id));
        sendBroadcast(toastIntent);
    }

    //---
    private void confirm_order(int st) {
        //--- hide notifications ----
        notificationManager.cancelAll();
        //----
        Intent toastIntent = new Intent(ACTION2);
        toastIntent.putExtra("confirm_order", "si");
        toastIntent.putExtra("status", String.valueOf(st));
        sendBroadcast(toastIntent);
    }

    //---
    private void cancel_order(int st) {
        //--- hide notifications ----
        notificationManager.cancelAll();
        //----
        Intent toastIntent = new Intent(ACTION2);
        toastIntent.putExtra("cancel_order", "si");
        toastIntent.putExtra("status", String.valueOf(st));
        sendBroadcast(toastIntent);
    }

    private void delivered_order(int st) {
        Intent toastIntent = new Intent(ACTION2);
        toastIntent.putExtra("delivered_order", "si");
        toastIntent.putExtra("status", String.valueOf(st));
        sendBroadcast(toastIntent);
    }

    /**
     * not needed
     */
    /*
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
     */
    public static Service getmCurrentService() {
        return mCurrentService;
    }

    public static void setmCurrentService(Service mCurrentService) {
        Service.mCurrentService = mCurrentService;
    }

    //--------
    private void showAlert(double lat, double lng, int id, int time, int distance, List<Mpedido_detalle> detail) {
        String title = "Pedido a " + distance + " metros\nReferencia " + getStringAddress(lat, lng);

        Intent activityIntent = new Intent(this, HomeActivity.class);
        activityIntent.putExtra("id", String.valueOf(id));
        activityIntent.putExtra("lat", String.valueOf(lat));
        activityIntent.putExtra("lng", String.valueOf(lng));
        activityIntent.putExtra("lng", String.valueOf(lng));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIn = new Intent(this, NotificationReceiver.class);
        broadcastIn.putExtra("id_pedido", String.valueOf(id));
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIn, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIn2 = new Intent(this, PedidoActivity.class);
        broadcastIn2.putExtra("id_pedido", String.valueOf(id));
        broadcastIn2.putExtra("referencia", "Referencia : " + getStringAddress(lat, lng));

        Log.d("Services Alert", String.valueOf(id));
        PendingIntent actionIntent2 = PendingIntent.getActivity(this, 1, broadcastIn2, PendingIntent.FLAG_UPDATE_CURRENT);

        android.app.Notification notification = new NotificationCompat.Builder(this, apli.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_directions_bike_wihte_24dp)
                .setContentTitle(title)
                .setContentText("Tiempo Aprox " + time + " Min")
                .setStyle(dataNotification(detail)
                        .setSummaryText("Tiempo Aprox " + time + " Min"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.WHITE, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setColor(Color.BLUE)
                .setContentIntent(actionIntent2)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_playlist_add_check_white_24dp, "Ver Detalle", actionIntent2)
                .addAction(R.drawable.ic_check_white_24dp, "Confirmar", actionIntent)
                .build();
        notificationManager.notify(1, notification);
    }


    private void mostrarAlertaCancelado(int id) {
        String title = "Se cancelo un pedido";

        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent actionIntent2 = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification notification = new NotificationCompat.Builder(this, apli.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_directions_bike_wihte_24dp)
                .setContentTitle(title)
                .setContentText("Se cancelo el pedido con el ID: " + id)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.WHITE, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setColor(Color.BLUE)
                .addAction(R.drawable.ic_playlist_add_check_white_24dp, "Ver detalle", actionIntent2)
                .build();
        notificationManager.notify(1, notification);
    }

    private NotificationCompat.InboxStyle dataNotification(List<Mpedido_detalle> detail) {
        NotificationCompat.InboxStyle notiImbox = new NotificationCompat.InboxStyle();
        for (Mpedido_detalle mpedido_detalle : detail) {
            notiImbox.addLine(mpedido_detalle.getCantidad() + " " + mpedido_detalle.getDescripcion());
        }
        return notiImbox;
    }

    private String getStringAddress(Double lat, Double lng) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString().substring(0, strReturnedAddress.toString().lastIndexOf(","));
                Log.w(TAG, strReturnedAddress.toString());
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Canont get Address!");
        }
        if(strAdd.contains(","))
            return strAdd.substring(0, strAdd.lastIndexOf(","));
        else
            return "";
    }
}
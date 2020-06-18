package com.example.appproveedorgas;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            if (intent.getStringExtra("id_pedido") != null) {
                int id_pedido = Integer.parseInt(intent.getStringExtra("id_pedido"));
                JSONObject data = new JSONObject();
                try {
                    data.put("order_id", id_pedido);
                    Log.d("Confirm Pedido", "Broatcast Receiver" + data.toString());
                    //SOCKET.emit("confirm order provider",data);
                    ProcessMainClass bck = new ProcessMainClass();
                    bck.launchService(context, "confirm order provider", data);
                    //----
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    //-----

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

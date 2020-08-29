package com.example.appproveedorgas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class PedidoActivity extends AppCompatActivity implements HorizontalScroll.ScrollViewListener, VerticalScroll.ScrollViewListener {
    //--
    ProgressBar progressBar;
    TextView tv_total;
    FloatingActionButton fab_accept;
    FloatingActionButton fab_denied;
    FloatingActionButton fab_notification;
    FloatingActionButton fab_delibered;
    //--
    TextView tv_espera;
    ProgressBar progressBarCon;
    //----
    private int id_pedido = 0;
    //--
    public static Socket SOCKET;
    public String HOST_NODEJS = "http://34.71.251.155:9000";
    private static final String TAG = "Confirm Pedido";
    //----
    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.
    int statusCode;
    String baseUrl = "http://34.71.251.155/api";  // This is the API base URL (GitHub API)
    String url;  // This will hold the full URL which will include the username entered in the etGitHubUser.
    //----
    private DatabaseHelper db;
    //--
    //TextInputEditText et_tiempo;
    //----- table -----
    private final ArrayList<JSONObject> productos = new ArrayList<>();
    //-------------
    private static int SCREEN_HEIGHT;
    private static int SCREEN_WIDTH;
    RelativeLayout relativeLayoutMain;

    RelativeLayout relativeLayoutA;
    RelativeLayout relativeLayoutB;
    RelativeLayout relativeLayoutC;
    RelativeLayout relativeLayoutD;

    TableLayout tableLayoutA;
    TableLayout tableLayoutB;
    TableLayout tableLayoutC;
    TableLayout tableLayoutD;

    TableRow tableRow;
    TableRow tableRowB;

    HorizontalScroll horizontalScrollViewB;
    HorizontalScroll horizontalScrollViewD;

    VerticalScroll scrollViewC;
    VerticalScroll scrollViewD;
    int tableColumnCountB = 0;
    int tableRowCountC = 0;
    private int nro_filas = 0;
    //----- dimens is pixel
    int WidhFirstHeader = 150;
    int WidhHeaderA = 50;
    int WidhHeaderProduct = 300;
    //---------
    BroadcastReceiver updateUIReciver;

    //-----
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        //---
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //---
        this.db = new DatabaseHelper(getApplicationContext());
        //--
        progressBar = findViewById(R.id.pb_detalle);
        tv_total = findViewById(R.id.lbl_subtotal_id);
        //--
        tv_espera = findViewById(R.id.tv_espera);
        progressBarCon = findViewById(R.id.pb_pedido);

        ocultarProgressConf();
        //--
        //et_tiempo = findViewById(R.id.txt_tiempo);
        //--
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            id_pedido = Integer.parseInt(intent.getStringExtra("id_pedido"));
            TextView txtReferencia = findViewById(R.id.txtReferencia);
            txtReferencia.setText(intent.getStringExtra("referencia"));
            postOrderDetail(id_pedido);

        }


        InitSocketIO();
        //----
        //--
        //------ tabla---
        /*
            Mandatory Content
         */
        relativeLayoutMain = (RelativeLayout) findViewById(R.id.relativeLayoutMain);
        getScreenDimension();
        initializeRelativeLayout();
        initializeScrollers();
        initializeTableLayout();
        horizontalScrollViewB.setScrollViewListener(this);
        horizontalScrollViewD.setScrollViewListener(this);
        scrollViewC.setScrollViewListener(this);
        scrollViewD.setScrollViewListener(this);
        addRowToTableA();
        initializeRowForTableB();
        //inicialize header
        addColumnsToTableB("Producto", 0, true);
        //addColumnsToTableB("Cantidad", 1);
        addColumnsToTableB("Precio U", 1, false);
        addColumnsToTableB("Subtotal", 2, false);

        /*
        for(int i=0; i<20; i++){
            initializeRowForTableD(i);
            addRowToTableC(" "+ i);
            for(int j=0; j<tableColumnCountB; j++){
                addColumnToTableAtD(i, "D "+ i + " " + j);
            }
        }
         */
        //---
        fab_accept = findViewById(R.id.fab_accept);
        fab_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                BottomSheetDialogTime bottomSheetDialogtime = new BottomSheetDialogTime();
                bottomSheetDialogtime.show(getSupportFragmentManager(),"Dialog Inferior");

                 */
                confirmOrder();
            }
        });
        fab_denied = findViewById(R.id.fab_denied);
        fab_denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                /*
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 */
            }
        });
        //--
        fab_notification = findViewById(R.id.fab_notification);
        fab_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager(),"Dialog Inferior");

                 */
                sendNotification();
            }
        });

        fab_delibered = findViewById(R.id.fab_delibered);
        fab_delibered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeliberedAlertDialog();
            }
        });

        disabledAllFab();
        disableNotificationBtn();
        //
        //--- response
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.appproveedorgas.pedidoactivity");
        updateUIReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                if (intent != null) {
                    if (intent.getStringExtra("confirm_order") != null) {
                        if (intent.getStringExtra("confirm_order").equals("si")) {
                            int st_co = Integer.parseInt(intent.getStringExtra("status"));
                            ocultarProgressConf();
                            if (st_co == 200) {
                                enableNotificationBtn();
                            }
                            if (st_co == 400) {
                                disabledAllFab();
                          //      Toast.makeText(getApplicationContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (intent.getStringExtra("cancel_order") != null) {
                        if (intent.getStringExtra("cancel_order").equals("si")) {
                            int st_ca = Integer.parseInt(intent.getStringExtra("status"));
                            if (st_ca == 200) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        disabledAllFab();
                                        Toast.makeText(getApplicationContext(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }

                    if (intent.getStringExtra("delivered_order") != null) {
                        if (intent.getStringExtra("delivered_order").equals("si")) {
                            int st_de = Integer.parseInt(intent.getStringExtra("status"));
                            if (st_de == 200) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Pedido completado", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Error delivered", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        }
                    }
                }

            }
        };
        registerReceiver(updateUIReciver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateUIReciver != null)
            unregisterReceiver(updateUIReciver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //----
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //-----
    }

    private void mostrarProgress() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void ocultarProgress() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
    //---

    private void mostrarProgressConf() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarCon.setVisibility(View.VISIBLE);
        tv_espera.setVisibility(View.VISIBLE);

    }

    private void ocultarProgressConf() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarCon.setVisibility(View.INVISIBLE);
        tv_espera.setVisibility(View.INVISIBLE);
    }

    //--
    private void enableNotificationBtn() {
        fab_notification.show();
        fab_accept.hide();
        fab_denied.hide();
        fab_delibered.show();
    }

    private void disableNotificationBtn() {
        fab_notification.hide();
        fab_accept.show();
        fab_denied.show();
        fab_delibered.hide();
    }

    private void disabledAllFab() {
        fab_notification.hide();
        fab_accept.hide();
        fab_denied.hide();
        fab_delibered.hide();
    }

    private void enableDeliberedBtn() {
        fab_delibered.show();
    }

    //---

    private void agregar_fila_tabla(JSONObject jsonObject) throws JSONException {

        initializeRowForTableD(nro_filas);
        //addRowToTableC(String.valueOf(nro_filas+1));
        addRowToTableC(String.valueOf(jsonObject.getInt("cantidad")));
        addColumnToTableAtD(nro_filas, jsonObject.getString("producto"), true);
        //addColumnToTableAtD(nro_filas, );
        DecimalFormat df = new DecimalFormat("#.00");
        double preciou = jsonObject.getDouble("preciou");
        double subtotal = jsonObject.getDouble("subtotal");
        if (preciou <= 0 && subtotal <= 0) {
            addColumnToTableAtD(nro_filas, "", false);
            addColumnToTableAtD(nro_filas, "", false);
        } else {
            addColumnToTableAtD(nro_filas, df.format(preciou), false);
            addColumnToTableAtD(nro_filas, df.format(subtotal), false);
        }
        nro_filas++;
    }

    private void getScreenDimension() {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
        int wfh = (int) (size.x * 0.2);
        if (wfh > WidhFirstHeader) {
            WidhFirstHeader = (int) (size.x * 0.2);
            WidhHeaderA = (int) (size.x * 0.1);
            WidhHeaderProduct = (int) (size.x * 0.4);
        }

    }

    private void initializeRelativeLayout() {
        relativeLayoutA = new RelativeLayout(getApplicationContext());
        relativeLayoutA.setId(R.id.relativeLayoutA);
        relativeLayoutA.setPadding(0, 0, 0, 0);

        relativeLayoutB = new RelativeLayout(getApplicationContext());
        relativeLayoutB.setId(R.id.relativeLayoutB);
        relativeLayoutB.setPadding(0, 0, 0, 0);

        relativeLayoutC = new RelativeLayout(getApplicationContext());
        relativeLayoutC.setId(R.id.relativeLayoutC);
        relativeLayoutC.setPadding(0, 0, 0, 0);

        relativeLayoutD = new RelativeLayout(getApplicationContext());
        relativeLayoutD.setId(R.id.relativeLayoutD);
        relativeLayoutD.setPadding(0, 0, 0, 0);

        //relativeLayoutA.setLayoutParams(new RelativeLayout.LayoutParams(SCREEN_WIDTH/5,SCREEN_HEIGHT/20));
        relativeLayoutA.setLayoutParams(new RelativeLayout.LayoutParams(WidhHeaderA, SCREEN_HEIGHT / 20));
        this.relativeLayoutMain.addView(relativeLayoutA);

        RelativeLayout.LayoutParams layoutParamsRelativeLayoutB = new RelativeLayout.LayoutParams(SCREEN_WIDTH - (SCREEN_WIDTH / 5), SCREEN_HEIGHT / 20);
        layoutParamsRelativeLayoutB.addRule(RelativeLayout.RIGHT_OF, R.id.relativeLayoutA);
        relativeLayoutB.setLayoutParams(layoutParamsRelativeLayoutB);
        this.relativeLayoutMain.addView(relativeLayoutB);

        //RelativeLayout.LayoutParams layoutParamsRelativeLayoutC= new RelativeLayout.LayoutParams(SCREEN_WIDTH/5, SCREEN_HEIGHT - (SCREEN_HEIGHT/20));
        RelativeLayout.LayoutParams layoutParamsRelativeLayoutC = new RelativeLayout.LayoutParams(WidhHeaderA, SCREEN_HEIGHT - (SCREEN_HEIGHT / 20));
        layoutParamsRelativeLayoutC.addRule(RelativeLayout.BELOW, R.id.relativeLayoutA);
        relativeLayoutC.setLayoutParams(layoutParamsRelativeLayoutC);
        this.relativeLayoutMain.addView(relativeLayoutC);

        RelativeLayout.LayoutParams layoutParamsRelativeLayoutD = new RelativeLayout.LayoutParams(SCREEN_WIDTH - (SCREEN_WIDTH / 5), SCREEN_HEIGHT - (SCREEN_HEIGHT / 20));
        layoutParamsRelativeLayoutD.addRule(RelativeLayout.BELOW, R.id.relativeLayoutB);
        layoutParamsRelativeLayoutD.addRule(RelativeLayout.RIGHT_OF, R.id.relativeLayoutC);
        relativeLayoutD.setLayoutParams(layoutParamsRelativeLayoutD);
        this.relativeLayoutMain.addView(relativeLayoutD);

    }

    private void initializeScrollers() {
        horizontalScrollViewB = new HorizontalScroll(getApplicationContext());
        horizontalScrollViewB.setPadding(0, 0, 0, 0);

        horizontalScrollViewD = new HorizontalScroll(getApplicationContext());
        horizontalScrollViewD.setPadding(0, 0, 0, 0);

        scrollViewC = new VerticalScroll(getApplicationContext());
        scrollViewC.setPadding(0, 0, 0, 0);

        scrollViewD = new VerticalScroll(getApplicationContext());
        scrollViewD.setPadding(0, 0, 0, 0);

        horizontalScrollViewB.setLayoutParams(new ViewGroup.LayoutParams(SCREEN_WIDTH - (SCREEN_WIDTH / 5), SCREEN_HEIGHT / 20));
        scrollViewC.setLayoutParams(new ViewGroup.LayoutParams(SCREEN_WIDTH / 5, SCREEN_HEIGHT - (SCREEN_HEIGHT / 20)));
        scrollViewD.setLayoutParams(new ViewGroup.LayoutParams(SCREEN_WIDTH - (SCREEN_WIDTH / 5), SCREEN_HEIGHT - (SCREEN_HEIGHT / 20)));
        horizontalScrollViewD.setLayoutParams(new ViewGroup.LayoutParams(SCREEN_WIDTH - (SCREEN_WIDTH / 5), SCREEN_HEIGHT - (SCREEN_HEIGHT / 20)));

        this.relativeLayoutB.addView(horizontalScrollViewB);
        this.relativeLayoutC.addView(scrollViewC);
        this.scrollViewD.addView(horizontalScrollViewD);
        this.relativeLayoutD.addView(scrollViewD);

    }

    private void initializeTableLayout() {
        tableLayoutA = new TableLayout(getApplicationContext());
        tableLayoutA.setPadding(0, 0, 0, 0);
        tableLayoutB = new TableLayout(getApplicationContext());
        tableLayoutB.setPadding(0, 0, 0, 0);
        tableLayoutB.setId(R.id.tableLayoutB);
        tableLayoutC = new TableLayout(getApplicationContext());
        tableLayoutC.setPadding(0, 0, 0, 0);
        tableLayoutD = new TableLayout(getApplicationContext());
        tableLayoutD.setPadding(0, 0, 0, 0);

        //TableLayout.LayoutParams layoutParamsTableLayoutA= new TableLayout.LayoutParams(SCREEN_WIDTH/5, SCREEN_HEIGHT/20);
        TableLayout.LayoutParams layoutParamsTableLayoutA = new TableLayout.LayoutParams(WidhHeaderA, SCREEN_HEIGHT / 20);
        tableLayoutA.setLayoutParams(layoutParamsTableLayoutA);
        tableLayoutA.setBackgroundColor(getResources().getColor(R.color.white));
        this.relativeLayoutA.addView(tableLayoutA);

        TableLayout.LayoutParams layoutParamsTableLayoutB = new TableLayout.LayoutParams(SCREEN_WIDTH - (SCREEN_WIDTH / 5), SCREEN_HEIGHT / 20);
        tableLayoutB.setLayoutParams(layoutParamsTableLayoutB);
        tableLayoutB.setBackgroundColor(getResources().getColor(R.color.white));
        this.horizontalScrollViewB.addView(tableLayoutB);

        TableLayout.LayoutParams layoutParamsTableLayoutC = new TableLayout.LayoutParams(WidhFirstHeader, SCREEN_HEIGHT - (SCREEN_HEIGHT / 20));
        tableLayoutC.setLayoutParams(layoutParamsTableLayoutC);
        tableLayoutC.setBackgroundColor(getResources().getColor(R.color.white));
        this.scrollViewC.addView(tableLayoutC);

        TableLayout.LayoutParams layoutParamsTableLayoutD = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        tableLayoutD.setLayoutParams(layoutParamsTableLayoutD);
        this.horizontalScrollViewD.addView(tableLayoutD);
    }


    @Override
    public void onScrollChanged(HorizontalScroll scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == horizontalScrollViewB) {
            horizontalScrollViewD.scrollTo(x, y);
        } else if (scrollView == horizontalScrollViewD) {
            horizontalScrollViewB.scrollTo(x, y);
        }
    }

    @Override
    public void onScrollChanged(VerticalScroll scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == scrollViewC) {
            scrollViewD.scrollTo(x, y);
        } else if (scrollView == scrollViewD) {
            scrollViewC.scrollTo(x, y);
        }
    }

    private void addRowToTableA() {
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(WidhHeaderA, SCREEN_HEIGHT / 20);
        tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText("");
        label_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.table_header));
        tableRow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        label_date.setTextSize(getResources().getDimension(R.dimen.cell_text_size));
        tableRow.addView(label_date);
        this.tableLayoutA.addView(tableRow);
    }

    private void initializeRowForTableB() {
        tableRowB = new TableRow(getApplicationContext());
        tableRow.setPadding(0, 0, 0, 0);
        this.tableLayoutB.addView(tableRowB);
    }

    private synchronized void addColumnsToTableB(String text, final int id, boolean product) {
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(WidhFirstHeader, SCREEN_HEIGHT / 20);
        TableRow.LayoutParams layoutParamsTableRowProduct = new TableRow.LayoutParams(WidhHeaderProduct, SCREEN_HEIGHT / 17);
        tableRow.setPadding(3, 3, 3, 4);
        if (product)
            tableRow.setLayoutParams(layoutParamsTableRowProduct);
        else
            tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText(text);
        label_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.table_header));
        label_date.setTextSize(getResources().getDimension(R.dimen.cell_text_size));
        this.tableRow.addView(label_date);
        this.tableRow.setTag(id);
        this.tableRowB.addView(tableRow);
        tableColumnCountB++;
    }

    private synchronized void addRowToTableC(String text) {
        TableRow tableRow1 = new TableRow(getApplicationContext());
        //TableRow.LayoutParams layoutParamsTableRow1= new TableRow.LayoutParams(SCREEN_WIDTH/5, SCREEN_HEIGHT/20);
        TableRow.LayoutParams layoutParamsTableRow1 = new TableRow.LayoutParams(WidhHeaderA, SCREEN_HEIGHT / 20);
        tableRow1.setPadding(5, 3, 0, 4);
        if (nro_filas == 0)
            tableRow1.setBackground(getResources().getDrawable(R.drawable.border_set));
        else
            tableRow1.setBackground(getResources().getDrawable(R.drawable.border_bottom));
        tableRow1.setLayoutParams(layoutParamsTableRow1);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText(text);
        label_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.table_body));
        label_date.setTextSize(getResources().getDimension(R.dimen.cell_text_size));
        tableRow1.addView(label_date);

        TableRow tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(WidhFirstHeader, SCREEN_HEIGHT / 20);
        tableRow.setPadding(0, 0, 0, 0);
        tableRow.setLayoutParams(layoutParamsTableRow);
        tableRow.addView(tableRow1);
        this.tableLayoutC.addView(tableRow, tableRowCountC);
        tableRowCountC++;
    }

    private synchronized void initializeRowForTableD(int pos) {
        TableRow tableRowB = new TableRow(getApplicationContext());
        TableRow.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, SCREEN_HEIGHT / 20);
        tableRowB.setPadding(0, 0, 0, 0);
        tableRowB.setLayoutParams(layoutParamsTableRow);
        this.tableLayoutD.addView(tableRowB, pos);
    }

    private synchronized void addColumnToTableAtD(final int rowPos, String text, boolean product) {
        TableRow tableRowAdd = (TableRow) this.tableLayoutD.getChildAt(rowPos);
        tableRow = new TableRow(getApplicationContext());
        //TableRow.LayoutParams layoutParamsTableRow= new TableRow.LayoutParams(SCREEN_WIDTH/5, SCREEN_HEIGHT/20);
        TableRow.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(WidhFirstHeader, SCREEN_HEIGHT / 20);
        TableRow.LayoutParams layoutParamsTableRowProduct = new TableRow.LayoutParams(WidhHeaderProduct, SCREEN_HEIGHT / 20);
        tableRow.setPadding(3, 3, 3, 4);
        if (nro_filas == 0)
            tableRow.setBackground(getResources().getDrawable(R.drawable.border_set));
        else
            tableRow.setBackground(getResources().getDrawable(R.drawable.border_bottom));
        if (product)
            tableRow.setLayoutParams(layoutParamsTableRowProduct);
        else
            tableRow.setLayoutParams(layoutParamsTableRow);
        TextView label_date = new TextView(getApplicationContext());
        label_date.setText(text);
        label_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.table_body));
        label_date.setTextSize(getResources().getDimension(R.dimen.cell_text_size));
        tableRow.setTag(label_date);
        this.tableRow.addView(label_date);
        tableRowAdd.addView(tableRow);
    }

    /*
    private void createCompleteColumn(String value){
        int i=0;
        int j=tableRowCountC-1;
        for(int k=i; k<=j; k++){
            addColumnToTableAtD(k, value);
        }
    }

    private void createCompleteRow(String value){
        initializeRowForTableD(0);
        int i=0;
        int j=tableColumnCountB-1;
        int pos= tableRowCountC-1;
        for(int k=i; k<=j; k++){
            addColumnToTableAtD(pos, value);
        }
    }
     */

    private void showDeliberedAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);
        //builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Pedido completado");
        builder.setMessage("¿El pedido esta completado?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendDElibered();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setCancelable(false);
        builder.show();

    }

    //-- calculate total
    private void calculateTotal() {
        double total = 0.0;
        try {
            for (JSONObject obj : productos) {
                double st = obj.getDouble("subtotal");
                total += Math.round(st * 100d) / 100d;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_total.setText("S/. " + total);
    }

    //-------
    private void postOrderDetail(int id) {
        mostrarProgress();
        productos.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        account cuenta = db.getAcountToken();
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("order_id", id);
            object.put("staff_id", cuenta.getDni());
            //object.put("selected",select);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = this.baseUrl + "/orderdetail/distributor/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("Volley get", response.toString());
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                JSONObject prod = new JSONObject();
                                prod.put("producto", obj.getJSONObject("product_id").getString("description"));
                                prod.put("cantidad", obj.getInt("quantity"));
                                prod.put("preciou", obj.getDouble("unit_price"));
                                prod.put("subtotal", (obj.getInt("quantity") * obj.getDouble("unit_price")));
                                prod.put("id", obj.getInt("id"));
                                productos.add(prod);
                                agregar_fila_tabla(prod);
                            }
                            calculateTotal();
                            ocultarProgress();
                            if (data.length() > 0) {
                                String sta = data.getJSONObject(0).getJSONObject("order_id").getString("status");
                                if (sta.equals("wait")) {
                                    disableNotificationBtn();
                                } else {
                                    if (sta.equals("confirm")) {
                                        enableNotificationBtn();
                                    } else {
                                        disabledAllFab();
                                    }

                                }
                            } else {
                                disabledAllFab();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley get", "error voley" + error.toString());
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        String msj = obj.getString("message");
                        Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }

            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = db.getToken();
                Log.d("Voley get", token);
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

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

                        // Snackbar.make(getWindow().getDecorView().findViewById(R.id.container), "Conexión Exitosa", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        /*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Realizando pedido", Toast.LENGTH_SHORT).show();

                            }
                        });

                         */
                    } else {
                        Log.e(TAG, "AUTH error");
                        /*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Verifique Conexión", Toast.LENGTH_SHORT).show();
                            }
                        });

                         */
                    }
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

                    final int sts = jsonObject.getInt("status");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ocultarProgressConf();
                            if (sts == 200) {
                                enableNotificationBtn();
                            }
                            if (sts == 400) {
                                disabledAllFab();
                               // Toast.makeText(getApplicationContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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
                    if (status == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                disabledAllFab();
                                Toast.makeText(getApplicationContext(), "Pedido Cancelado", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

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
                    if (status == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Pedido completado", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error delivered", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void conect() {
        Log.d(TAG, "emitiendo new conect");
        JSONObject data = new JSONObject();
        account cuenta = db.getAcountToken();
        try {
            data.put("ID", cuenta.getDni());
            data.put("type", "provider");
            data.put("company_id", cuenta.getCompany_id());
            SOCKET.emit("new connect", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void confirmOrder() {
        JSONObject data = new JSONObject();
        try {
            data.put("order_id", id_pedido);
            Log.d("Confirm Pedido", data.toString());

            Intent NxtAct = new Intent(this, HomeActivity.class);
            NxtAct.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(NxtAct);
            SOCKET.emit("confirm order provider", data);

            sendDataService("confirm order provider", data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mostrarProgressConf();
                }
            });
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification() {
        JSONObject data = new JSONObject();
        try {
            data.put("order_id", id_pedido);
            //SOCKET.emit("send alert",data);
            sendDataService("send alert", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendDElibered() {
        JSONObject data = new JSONObject();
        try {
            data.put("order_id", id_pedido);
            //SOCKET.emit("delivered order provider",data);
            sendDataService("delivered order provider", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    public void OnBottomClicked(String text) {
        Log.d("Mensaje",text);
        sendNotification(text);
    }

    @Override
    public void OnBottomClickedTime(String text) {
        confirmOrder(Integer.parseInt(text));
    }
    */
    //---------
    private void sendDataService(String evento, JSONObject data) {
        ProcessMainClass bck = new ProcessMainClass();
        bck.launchService(getApplicationContext(), evento, data);
    }
}

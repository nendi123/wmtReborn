package nendi.wmtreborn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import nendi.wmtreborn.model.Server;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PencarianIDActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private MaterialSearchView materialSearchView;
    private String[] list;
    private RequestQueue requestQueue;
    private String url = Server.URL+"pelanggan";
    private TextView id_uji,nama, text1, alamat,merk,txtstatus,txtPengujian, serial_number,lainnya;
    private CardView cardView;
    private ProgressBar progressBar;
    private Button button_uji;
    private EditText stand;
    private Switch aSwitch;
    private CardView meteranCardView;
    private Spinner spinner;
    boolean sts = false;
    String idPelanggan="";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    View view = this.getCurrentFocus();

    private static final String TAG_ID = "id_pelanggan";
    private static final String TAG_NAMA_PENGGUNA = "nama";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pencarian_id);

        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_satu);
        ToolBarAtas2.setTitle("Cari ID");
        setSupportActionBar(ToolBarAtas2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedpreferences = getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

       // id_uji = (TextView) findViewById(R.id.txtIDuji);
        nama = (TextView) findViewById(R.id.txtNama);
        alamat = (TextView) findViewById(R.id.txtAlamat);
        text1 = (TextView) findViewById(R.id.text1);
        merk = (TextView) findViewById(R.id.txtMerk);
        //stand = (EditText) findViewById(R.id.estand);
        button_uji = (Button) findViewById(R.id.button4);
        aSwitch = (Switch) findViewById(R.id.switch1);
        txtstatus = (TextView) findViewById(R.id.txtStatus);
        txtPengujian = (TextView) findViewById(R.id.txtPengujian);
        spinner = (Spinner) findViewById(R.id.sp_name);
        serial_number = (EditText) findViewById(R.id.eSerialNumber);
        meteranCardView = (CardView) findViewById(R.id.CardMeteran);
        lainnya = (EditText) findViewById(R.id.eLainnya);

        cardView = (CardView) findViewById(R.id.linearLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        if(aSwitch != null){
            aSwitch.setOnCheckedChangeListener(this);
        }
        cardView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        text1.setText("Belum Ada Data Yang Tampil");

        requestQueue = Volley.newRequestQueue(this);
        materialSearchView = (MaterialSearchView) findViewById(R.id.mysearch);
        materialSearchView.closeSearch();
        materialSearchView.setSuggestions(list);
        materialSearchView.setHint("ID Pelanggan");
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            Handler handler = new Handler();
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(PencarianIDActivity.this, " "+url+"/"+query, Toast.LENGTH_SHORT).show();
                idPelanggan = query;
                editor.putString("id_pelanggan", idPelanggan);
                editor.commit();
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url+"/"+query, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{

                            for(int i=0;i<response.length();i++){
                                final JSONObject head_uji = response.getJSONObject(i);
                                text1.setVisibility(View.GONE);
                                cardView.setVisibility(View.VISIBLE);

                                nama.setText(head_uji.getString(TAG_NAMA_PENGGUNA));
                                alamat.setText(head_uji.getString("alamat"));
                                merk.setText(head_uji.getString("merk"));

                                getStatus(head_uji.getString("id_pelanggan"));

                                editor.putString("id_pelanggan", head_uji.getString("id_pelanggan"));
                                editor.commit();


                                    button_uji.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            Intent intent = new Intent(PencarianIDActivity.this, PengujianActivity.class);
                                            try {
                                                if(!sts){
                                                    saveMeteran(head_uji.getString("id_pelanggan"));
                                                }
                                                //saveHeaduji();
                                                //getId_uji();
                                                intent.putExtra("id_pelanggan", head_uji.getString("id_pelanggan"));
                                                startActivity(intent);
                                                finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            cardView.setVisibility(View.GONE);
                            Toast.makeText(PencarianIDActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                text1.setText("Data Tidak ditemukan");
                                Toast.makeText(PencarianIDActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                                Log.e("Get Error : ", String.valueOf(error));
                            }
                        }
                );

                // Add JsonArrayRequest to the RequestQueue
                requestQueue.add(jsonArrayRequest);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);

        return true;
    }
    private void getStatus(final String id_pelanggan){

        StringRequest request = new StringRequest(Request.Method.POST, "http://mobiwmt.tech/linflow_v1/api/save_pelanggan_meteran.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Integer status = Integer.valueOf(response);

                if(status == 0){
                    txtPengujian.setText("Belum Di Uji");
                }else{

                    txtPengujian.setText("Sudah Di Uji");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){@Override
        protected Map<String,String> getParams(){
            Map<String,String> params = new HashMap<String, String>();
            params.put("id_pelanggan",id_pelanggan);

            return params;
        }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void saveMeteran(final String id_pelanggan){

        StringRequest request = new StringRequest(Request.Method.POST, "http://mobiwmt.tech/linflow_v1/api/save_beda_meteran.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){@Override
        protected Map<String,String> getParams(){
            Map<String,String> params = new HashMap<String, String>();
            params.put("nomor_pelanggan",id_pelanggan);
            if(spinner.getSelectedItem().toString() == "Lainnya"){
                params.put("merk", lainnya.getText().toString());
            }else{
                params.put("merk", spinner.getSelectedItem().toString());
            }
            params.put("sn",serial_number.getText().toString());

            return params;
        }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    public void saveHeaduji(){
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);
        editor.putString("tanggal", todayString);
        editor.commit();
        sharedpreferences = PencarianIDActivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        final String id_operator = sharedpreferences.getString("id_operator","");

        Map<String, Object> jsonParams = new ArrayMap<>();
        //jsonParams.put(TAG_ID_UJI, getIntent().getStringExtra("id_uji"));
        jsonParams.put("id_pelanggan", idPelanggan);
        jsonParams.put("id_operator", id_operator);
        jsonParams.put("tanggal", todayString);
        //jsonParams.put("tekanan", tekanan.getText().toString());
        //jsonParams.put("stand", stand.getText().toString());


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Server.URL+"headuji", new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("Response");
                            Log.e("Success ", ""+res);
                            if(res.equals("true")){

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR : ", "" + error);

                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue = Volley.newRequestQueue(PencarianIDActivity.this);
        requestQueue.add(request);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        sts =b;
        if(b) {
            //do stuff when Switch is ON

            txtstatus.setText("Sesuai");
            meteranCardView.setVisibility(View.INVISIBLE);

        } else {
            //do stuff when Switch if OFF
            txtstatus.setText("Tidak Sesuai");
            meteranCardView.setVisibility(View.VISIBLE);
        }
    }
}

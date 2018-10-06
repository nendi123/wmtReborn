package nendi.wmtreborn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import nendi.wmtreborn.model.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PengujianActivity extends AppCompatActivity {

    private static final String url = Server.URL + Constant.TAG_DETUJI;
    private RequestQueue requestQueue;
    private Button save,reset,uji;
    private EditText uji_awal,uji_akhir,presentase,hasilmaster,hasiluji,hasilpengujian, kesimpulan,stand,tekanan;

    private Double ujiawal,ujiakhir,hasil_master,hasil_uji, hasil_pengujian, presentases;

    private static final String TAG_ID_UJI = "id_uji";
   // private static final String TAG_MASTER_AWAL = "master_awal";
   // private static final String TAG_MASTER_AKHIR = "master_akhir";

    private static final String TAG_METER_AWAL = "counter_awal";
    private static final String TAG_METER_AKHIR = "counter_akhir";
    private static final String TAG_VOLUME = "vol_meter_air";
    private static final String TAG_VOLUME_WMT= "vol_wmt";
    private static final String TAG_MASTER_HASIL = "deviasi_volume";
    private static final String TAG_DEVIASI_PERSEN = "deviasi_persen";
    private static final Integer MAX_LENGTH = 6;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengujian);

        stand = (EditText) findViewById(R.id.eStand);
        uji_awal = (EditText) findViewById(R.id.eCounterA);
        uji_akhir = (EditText) findViewById(R.id.eCounterAkh);

        tekanan = (EditText) findViewById(R.id.eTekanan);
        hasilmaster = (EditText) findViewById(R.id.ehasilmaster);
        hasiluji = (EditText) findViewById(R.id.eHasilUjiMeter);

        presentase = (EditText) findViewById(R.id.presentase);
        hasilpengujian  = (EditText) findViewById(R.id.ehasiluji);
        kesimpulan = (EditText) findViewById(R.id.keterangan);


        sharedpreferences = getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        //hasilmaster.setKeyListener(null);
        hasiluji.setKeyListener(null);
        presentase.setKeyListener(null);
        hasilpengujian.setKeyListener(null);
        kesimpulan.setKeyListener(null);


        save = (Button) findViewById(R.id.btnSave);
        save.setEnabled(false);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if (cekMeteran(uji_awal) == false) {
                    uji_awal.setError("Angka tidak boleh lebih dari 6 digit");
                } else if (cekMeteran(uji_akhir) == false) {
                    uji_akhir.setError("Angka tidak boleh lebih dari 6 digit");
                } else {

                  Date todayDate = Calendar.getInstance().getTime();
                  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                  String todayString = formatter.format(todayDate);
                  editor.putString("tanggal", todayString);
                  editor.commit();
                  sharedpreferences = PengujianActivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
                  final String id_operator = sharedpreferences.getString("id_operator","");
                  final String idPelanggan = sharedpreferences.getString("id_pelanggan", "");

                  Map<String, Object> jsonParams = new ArrayMap<>();
                  //jsonParams.put(TAG_ID_UJI, getIntent().getStringExtra("id_uji"));
                  jsonParams.put("id_pelanggan", idPelanggan);
                  jsonParams.put("id_operator", id_operator);
                  jsonParams.put("tanggal", todayString);
                  jsonParams.put("tekanan", tekanan.getText().toString());
                  jsonParams.put("stand", stand.getText().toString());


                  JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Server.URL+"headuji", new JSONObject(jsonParams),
                          new Response.Listener<JSONObject>() {
                              @Override
                              public void onResponse(JSONObject response) {
                                  try {
                                      String res = response.getString("Response");
                                      Log.e("Success ", ""+res);

                                      sharedpreferences = PengujianActivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
                                      final String id_operator = sharedpreferences.getString("id_operator","");
                                      StringRequest request = new StringRequest(Request.Method.POST, "http://mobiwmt.tech/linflow_v1/api/getMax.php", new Response.Listener<String>() {
                                          @Override
                                          public void onResponse(String response) {
                                              Log.e("Success 2 ", ""+response);
                                              editor.putString("id_uji", response);
                                              editor.commit();

                                              DecimalFormat decimalFormat = new DecimalFormat("#.###");

                                              String df = decimalFormat.format(presentases);
                                              String df2 = decimalFormat.format(hasil_uji);
                                              String df3 = decimalFormat.format(hasil_pengujian);
                                              String df4 = decimalFormat.format(hasil_master);
                                              hasilpengujian.setText(String.valueOf(df3));
                                              hasilmaster.setText(String.valueOf(df4));
                                              hasiluji.setText(String.valueOf(df2));
                                              presentase.setText(df + " %");

                                              String keterangan = "Ganti";
                                              if(presentases >= 2.0 || presentases <= -2.0){
                                                  keterangan = "Ganti";
                                              } else{
                                                  keterangan = "Baik";
                                              }
                                              kesimpulan.setText(String.valueOf(keterangan));
                                              sharedpreferences = PengujianActivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
                                              String id_uji = sharedpreferences.getString("id_uji","");
                                              Log.e("ID Uji ", id_uji);
                                              //Integer Iid_uji = Integer.valueOf(id_uji);
                                              //Iid_uji = Iid_uji+1;
                                              //id_uji = String.valueOf(Iid_uji);
                                              Map<String, Object> jsonParamss = new ArrayMap<>();
                                              //jsonParams.put(TAG_ID_UJI, getIntent().getStringExtra("id_uji"));
                                              jsonParamss.put(TAG_ID_UJI, id_uji);
                                              jsonParamss.put(TAG_METER_AWAL, ujiawal);
                                              jsonParamss.put(TAG_METER_AKHIR, ujiakhir);
                                              jsonParamss.put(TAG_VOLUME, hasil_uji);
                                              jsonParamss.put(TAG_VOLUME_WMT, hasil_master);
                                              jsonParamss.put(TAG_MASTER_HASIL, hasil_pengujian);
                                              jsonParamss.put(TAG_DEVIASI_PERSEN, presentases);
                                              jsonParamss.put("keterangan", keterangan);

                                              JsonObjectRequest requests = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParamss),
                                                      new com.android.volley.Response.Listener<JSONObject>() {
                                                          @Override
                                                          public void onResponse(JSONObject response) {
                                                              try {
                                                                  String res = response.getString("Response");
                                                                  Log.e("Success ", ""+res);
                                                                  if(res.equals("true")){
                                                                      Intent intent = new Intent(PengujianActivity.this, SignatureActivity.class);
                                                                      Toast.makeText(PengujianActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                                                                      startActivity(intent);
                                                                  }
                                                              } catch (JSONException e) {
                                                                  e.printStackTrace();
                                                              }
                                                          }
                                                      },
                                                      new com.android.volley.Response.ErrorListener() {
                                                          @Override
                                                          public void onErrorResponse(VolleyError error) {
                                                              Log.e("ERROR : ", "" + error);
                                                              Toast.makeText(PengujianActivity.this, "Data gagal disimpan", Toast.LENGTH_SHORT).show();
                                                              Intent intent = new Intent(PengujianActivity.this, MainActivity.class);
                                                              startActivity(intent);
                                                          }
                                                      });
                                              requests.setRetryPolicy(new DefaultRetryPolicy(
                                                      0,
                                                      DefaultRetryPolicy.DEFAULT_MAX_RETRIES-1,
                                                      DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                              requestQueue = Volley.newRequestQueue(PengujianActivity.this);
                                              requestQueue.add(requests);

                                          }
                                      }, new Response.ErrorListener() {
                                          @Override
                                          public void onErrorResponse(VolleyError error) {

                                          }
                                      }){@Override
                                      protected Map<String,String> getParams(){
                                          Map<String,String> params = new HashMap<String, String>();
                                          params.put("id_operator",id_operator);

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
                                      requestQueue = Volley.newRequestQueue(PengujianActivity.this);
                                      requestQueue.add(request);

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
                  requestQueue = Volley.newRequestQueue(PengujianActivity.this);
                  requestQueue.add(request);

                }

            }
        });


        reset = (Button) findViewById(R.id.btnCancel);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uji_awal.setText("");
                uji_akhir.setText("");
                presentase.setText("");
                hasilmaster.setText("");
                hasiluji.setText("");
                hasilpengujian.setText("");
                kesimpulan.setText("");
                stand.setText("");
                tekanan.setText("");
            }
        });
        uji = (Button) findViewById(R.id.btnUji);
        uji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cekMeteran(uji_awal)==false){
                    uji_awal.setError("Angka tidak boleh lebih dari 6 digit");
                }else if(cekMeteran(uji_akhir)==false){
                    uji_akhir.setError("Angka tidak boleh lebih dari 6 digit");
                }else{
                    sendPengujian();
                }
            }
        });
    }

    private void sendPengujian(){

        //getId_uji();
        ujiawal = Double.valueOf(uji_awal.getText().toString());
        ujiakhir = Double.valueOf(uji_akhir.getText().toString());

        hasil_master = Double.valueOf(hasilmaster.getText().toString());

        hasil_uji = ujiakhir - ujiawal;

        hasil_pengujian = (hasil_uji-hasil_master);
        presentases = (hasil_pengujian/hasil_master)*100;

        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        String df = decimalFormat.format(presentases);
        String df2 = decimalFormat.format(hasil_uji);
        String df3 = decimalFormat.format(hasil_pengujian);
        String df4 = decimalFormat.format(hasil_master);
        hasilpengujian.setText(String.valueOf(df3));
        hasilmaster.setText(String.valueOf(df4));
        hasiluji.setText(String.valueOf(df2));
        presentase.setText(df + " %");

        String keterangan = "Ganti";
        if(presentases >= 2.0 || presentases <= -2.0){
            keterangan = "Ganti";
        } else{
            keterangan = "Baik";
        }
        kesimpulan.setText(String.valueOf(keterangan));
        save.setEnabled(true);
    }

    public Boolean cekMeteran(EditText editText){
        String temp = editText.getText().toString();
        String[] kata = temp.split("\\.");
        String awal = kata[0];
        if(awal.length() > MAX_LENGTH){
            return false;
        }else{
            return true;
        }
    }
    private void getId_uji(){
        sharedpreferences = PengujianActivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        final String id_operator = sharedpreferences.getString("id_operator","");
        System.out.println(id_operator);
        StringRequest request = new StringRequest(Request.Method.POST, "http://mobiwmt.tech/linflow_v1/api/getMax.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Id Operator = "+response);
                editor.putString("id_uji", response);
                editor.commit();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){@Override
        protected Map<String,String> getParams(){
            Map<String,String> params = new HashMap<String, String>();
            params.put("id_operator",id_operator);

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

    public void saveHeaduji(){
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);
        editor.putString("tanggal", todayString);
        editor.commit();
        sharedpreferences = PengujianActivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        final String id_operator = sharedpreferences.getString("id_operator","");
        final String idPelanggan = sharedpreferences.getString("id_pelanggan", "");

        Map<String, Object> jsonParams = new ArrayMap<>();
        //jsonParams.put(TAG_ID_UJI, getIntent().getStringExtra("id_uji"));
        jsonParams.put("id_pelanggan", idPelanggan);
        jsonParams.put("id_operator", id_operator);
        jsonParams.put("tanggal", todayString);
        jsonParams.put("tekanan", tekanan.getText().toString());
        jsonParams.put("stand", stand.getText().toString());


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Server.URL+"headuji", new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("Response");
                            Log.e("Success ", ""+res);

                            sharedpreferences = PengujianActivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
                            final String id_operator = sharedpreferences.getString("id_operator","");
                            StringRequest request = new StringRequest(Request.Method.POST, "http://mobiwmt.tech/linflow_v1/api/getMax.php", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("Success 2 ", ""+response);
                                    editor.putString("id_uji", response);
                                    editor.commit();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){@Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("id_operator",id_operator);

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
                            requestQueue = Volley.newRequestQueue(PengujianActivity.this);
                            requestQueue.add(request);

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
        requestQueue = Volley.newRequestQueue(PengujianActivity.this);
        requestQueue.add(request);
    }

}

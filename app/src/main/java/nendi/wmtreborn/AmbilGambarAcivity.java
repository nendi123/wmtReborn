package nendi.wmtreborn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static nendi.wmtreborn.model.Server.URL;

public class AmbilGambarAcivity extends AppCompatActivity {

    private Button btCamera,btSave;
    private ImageView ivCamera;

    private static final String TAG = AmbilGambarAcivity.class.getSimpleName();
    private static final int CAMERA_REQUEST_CODE = 7777;
    private RequestQueue requestQueue;
    private static final String url = URL + "uploadfoto";
    private TextView longi,lati;
    Bitmap bitmap;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambil_gambar);

        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_satu);
        setSupportActionBar(ToolBarAtas2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Gambar Lokasi Pelanggan");
        btCamera = (Button) findViewById(R.id.bt_camera);
        btSave = (Button) findViewById(R.id.bt_save);
        ivCamera = (ImageView) findViewById(R.id.iv_camera);

        //getLocation();
        sharedpreferences = getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        btCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //intent khusus untuk menangkap foto lewat kamera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case(CAMERA_REQUEST_CODE) :
                if(resultCode == Activity.RESULT_OK)
                {
                    // result code sama, save gambar ke bitmap

                    bitmap = (Bitmap) data.getExtras().get("data");
                    ivCamera.setImageBitmap(bitmap);
                }
                break;
        }
    }

    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }


    private void UploadImage(){
        sharedpreferences = AmbilGambarAcivity.this.getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        final String id_uji = sharedpreferences.getString("id_uji","");
        final String id_pelanggan = sharedpreferences.getString("id_pelanggan","");

        progressDialog = new ProgressDialog(AmbilGambarAcivity.this);
        progressDialog.setTitle("Upload File");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.mobiwmt.tech/linflow_v1/api/upload.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s = response.trim();
                progressDialog.dismiss();
                    Toast.makeText(AmbilGambarAcivity.this, ""+s, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AmbilGambarAcivity.this, MainActivity.class);
                    startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
               Toast.makeText(AmbilGambarAcivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                Map<String ,String> params = new HashMap<String,String>();

                //params.put("id_uji",id_uji);
                params.put("id_uji",id_uji);
                params.put("id_pelanggan",id_pelanggan);
                params.put("images",image);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

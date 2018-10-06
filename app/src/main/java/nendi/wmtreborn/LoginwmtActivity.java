package nendi.wmtreborn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import nendi.wmtreborn.model.Server;
import nendi.wmtreborn.model.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginwmtActivity extends AppCompatActivity {

    int success;
    ConnectivityManager conMgr;
    private RequestQueue requestQueue;
    private String url = Server.URL + Constant.TAG_LOGIN;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Boolean session = false;
    String id, username;

    private CircularProgressButton sign;
    private EditText userID, passwords;
    private String user, pass;
    private boolean validLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loginwmt);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        sharedpreferences = getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        session = sharedpreferences.getBoolean(Constant.session_status, false);
        username = sharedpreferences.getString(Constant.TAG_USERNAME, null);

        if (session) {
            Intent intent = new Intent(LoginwmtActivity.this, MainActivity.class);
            intent.putExtra(Constant.TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }

        userID = (EditText) findViewById(R.id.userID);
        passwords = (EditText) findViewById(R.id.password);

        user = userID.getText().toString();
        pass = passwords.getText().toString();

        sign = (CircularProgressButton) findViewById(R.id.sign_in_button);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginvalidation()) {
                    sign.startAnimation();
                    login lgn = new login();
                    lgn.setUserID(user);
                    checkLogin();
                }
            }
        });

    }

    private void checkLogin() {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("userid", user);
        jsonParams.put("password", pass);
        jsonParams.put("udid", "1234567");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("Response");
                            if (res.equals("true")) {
                                sign.doneLoadingAnimation(getResources().getColor(R.color.green_success), ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_done_white_48dp)).getBitmap());
                                doLogin();
                                getData_Operator();
                            }
                            sign.revertAnimation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            sign.revertAnimation();
                        }
                        Toast.makeText(LoginwmtActivity.this, "Selamat Datang : " + user, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null != error.networkResponse) {
                            Toast.makeText(LoginwmtActivity.this, "UserID atu Password Salah", Toast.LENGTH_SHORT).show();
                        }
                        sign.revertAnimation();
                    }
                });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private boolean loginvalidation() {
        user = userID.getText().toString();
        pass = passwords.getText().toString();
        validLogin = false;
        if (user.length() == 0 || pass.length() == 0) {
            Toast.makeText(this, "Invalid User ID or Password", Toast.LENGTH_SHORT).show();
            validLogin = false;
        } else {
            validLogin = true;
        }
        return validLogin;
    }

    public void doLogin() {
        editor.putString(Constant.TAG_USERNAME, userID.getText().toString());
        editor.putBoolean(Constant.session_status, true);
        editor.commit();
        Intent intent = new Intent(LoginwmtActivity.this, MainActivity.class);
        startActivity(intent);
    }
    private void getData_Operator(){
        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Server.URL+Constant.TAG_OPERATOR+"/"+userID.getText().toString(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try{

                    for(int i=0;i<response.length();i++){
                        JSONObject operator = response.getJSONObject(i);
                       editor.putString("id_operator", operator.getString("id_operator"));
                       editor.commit();
                       System.out.println(sharedpreferences.getString("id_operator",""));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(LoginwmtActivity.this, "Get Error : "+error, Toast.LENGTH_SHORT).show();
                        Log.e("Get Error : ", String.valueOf(error));
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);


    }
}

package nendi.wmtreborn;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import nendi.wmtreborn.model.Server;
import nendi.wmtreborn.model.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OperatorFragment extends Fragment {

    private RequestQueue requestQueue;
    private static String url = Server.URL;

    private TextView userid, password, namaLengkap, email, no_hp;
    private String User,Password,Nama,Email,No_Telpon;
    private login lgn = new login();
    private ImageView imageView;

    private static final String TAG_USERID = "userid";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_NAMA_PENGGUNA = "nama_operator";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NO_HP = "no_telpon";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    //http://mobiwmt.tech/api/operator/karjo

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // saya get dari sharedpref aja itu User buat tempelin ke web service
        sharedpreferences = getContext().getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        User = sharedpreferences.getString(Constant.TAG_USERNAME, "");

        final View frag2_pesan = inflater.inflate(R.layout.fragment_operator, container, false);

        userid = (TextView) frag2_pesan.findViewById(R.id.txtuserID);
        namaLengkap = (TextView) frag2_pesan.findViewById(R.id.txtNamaL);
        email = (TextView) frag2_pesan.findViewById(R.id.txtEmail);
        no_hp = (TextView) frag2_pesan.findViewById(R.id.txtNoHP);

        url = url +"/"+ User;


        getData_Operator();

        return frag2_pesan;
    }

    private void getData_Operator(){
        requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Server.URL+Constant.TAG_OPERATOR+"/"+User, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{

                            for(int i=0;i<response.length();i++){
                                JSONObject operator = response.getJSONObject(i);
                                User = operator.getString(TAG_USERID);
                                //Password = operator.getString(TAG_PASSWORD);
                                Nama = operator.getString(TAG_NAMA_PENGGUNA);
                                Email = operator.getString(TAG_EMAIL);
                                No_Telpon = operator.getString(TAG_NO_HP);

                                userid.setText(User);
                                //password.setText(Password);
                                namaLengkap.setText(Nama);
                                email.setText(Email);
                                no_hp.setText(No_Telpon);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(getActivity(), "Get Error : "+error, Toast.LENGTH_SHORT).show();
                        Log.e("Get Error : ", String.valueOf(error));
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);


    }

}
package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnRegister, btnLogin, btnForgotPassword;
    private ProgressBar progressBar;
    // url to login
    private static final String url_login = "http://moneyview.atspace.cc/login.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    //private static String URL_LOGIN = "http://192.168.0.103//login.php";
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLoginSession();
        //int userid;
        //pref = getSharedPreferences("userdetails",MODE_PRIVATE);
        //userid = pref.getInt("userid", 0);
        //if(userid > 0){
        //    Intent x = new Intent(MainActivity.this, TransactionActivity.class);
        //    startActivity(x);
        //}

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String pw= etPassword.getText().toString();
                String uName= etUsername.getText().toString();
                if(pw.isEmpty() && uName.isEmpty()){
                    etUsername.setError(getString(R.string.error_field_required));
                    etPassword.setError(getString(R.string.error_field_required));
                } else
                if(pw.isEmpty())
                {
                    etPassword.setError(getString(R.string.error_field_required));

                }else

                if(uName.isEmpty())
                {
                    etUsername.setError(getString(R.string.error_field_required));

                }else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject dataJson = new JSONObject();
                    try{
                        dataJson.put("username", uName);
                        dataJson.put("password", pw);


                    }catch(JSONException e){

                    }

                    postData(url_login,dataJson,1 );

                }
            }
        });
        btnForgotPassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i =new Intent(MainActivity.this, PasswordVerification.class);
                startActivity(i);
            }
        });


        /*btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String username =  etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(!username.isEmpty() || !password.isEmpty()){
                    //Login(username, password);
                }
                else{
                    etUsername.setError("Please insert username");
                    etPassword.setError("Please insert password");
                }
            }
        });*/
    }

    public void checkLoginSession(){
        int userid;
        pref = getSharedPreferences("userdetails",MODE_PRIVATE);
        userid = Integer.parseInt(pref.getString("userid", "0"));
        if(userid != 0){
            Intent x = new Intent(MainActivity.this, TransactionActivity.class);
            startActivity(x);
        }
    }

    public void postData(String url, final JSONObject json, final int option){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(
                Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                switch (option){
                    case 1:checkResponseLogin(response); break;

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //String alert_message;
                //alert_message = error.toString();
                //Toast.makeText(MainActivity.this, "Error" + alert_message, Toast.LENGTH_SHORT).show();
            }

        });
        requestQueue.add(json_obj_req);
    }



    public void checkResponseLogin(JSONObject response)
    {
        Log.i("----Response", response+" "+url_login);
        try {
            if(response.getInt(TAG_SUCCESS)==1){
                progressBar.setVisibility(View.GONE);
                //String userid = response.getString("userid");
                Toast.makeText(this, "Successful login. Welcome " + etUsername.getText().toString() + ".", Toast.LENGTH_SHORT).show();
                pref = getSharedPreferences("userdetails", MODE_PRIVATE); //Open sharedPreference
                SharedPreferences.Editor editor = pref.edit(); //set Editor mode
                editor.putString("username", etUsername.getText().toString());
                editor.commit();
                finish();
                Intent i = new Intent(MainActivity.this, TransactionActivity.class);
                startActivity(i);

            }else{
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    /*private void Login(String username, String password){
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");
                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String username = object.getString("userid").trim();
                                    Toast.makeText(MainActivity.this, "Success Login. Welcome "+username+".", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/
}
package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnGoBack, btnCreate;
    private ProgressBar progressBar;
    // url to register
    private static final String url_register = "http://moneyview.atspace.cc/register.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnCreate = findViewById(R.id.btnCreate);
        progressBar = findViewById(R.id.progressBar);

        btnGoBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String pw= etPassword.getText().toString().trim();
                String uName= etUsername.getText().toString().trim();
                if(pw.isEmpty())
                {
                    etPassword.setError(getString(R.string.error_field_required));

                }else if(uName.isEmpty())
                {
                    etUsername.setError(getString(R.string.error_field_required));

                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(uName).matches()){
                    etUsername.setError("Invalid e-mail format");
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject dataJson = new JSONObject();
                    try{
                        dataJson.put("username", uName);
                        dataJson.put("password", pw);


                    }catch(JSONException e){

                    }

                    postData(url_register,dataJson,1 );

                }
            }
        });
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
                String alert_message;
                alert_message = error.toString();
                Toast.makeText(RegisterActivity.this, "Error" + alert_message, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

        });
        requestQueue.add(json_obj_req);
    }
    public void checkResponseLogin(JSONObject response)
    {
        Log.i("----Response", response+" "+url_register);
        try {
            if(response.getInt(TAG_SUCCESS)==1){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Registration Successful. Welcome " + etUsername.getText().toString() + ".", Toast.LENGTH_SHORT).show();
                finish();
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);

            }else{
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Registration Failed. Username already taken.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}
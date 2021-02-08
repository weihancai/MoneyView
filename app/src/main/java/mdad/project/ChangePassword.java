package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangePassword extends AppCompatActivity {

    EditText etComfirmPassword,etPassword;
    Button btnChangePassword;

    // Response
    String responseServer;

    JSONObject json=null;
    private ProgressDialog pDialog;

    private static final String update_pass = "http://moneyview.atspace.cc/update_password.php";

    private static final String TAG_USER = "user";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PASSWORD = "password";

    SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnChangePassword=(Button)findViewById(R.id.btnChangePassword);
        etComfirmPassword = (EditText)findViewById(R.id.etComfirmPassword);
        etPassword = (EditText)findViewById(R.id.etPassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String email = getIntent().getStringExtra("Email");
                //Log.i("User Email add", email);
                prf = getSharedPreferences("userdetails", MODE_PRIVATE); //Open sharedPreference
                String email = prf.getString("email", null);
                String pass = etPassword.getText().toString().trim();
                String comfPass = etComfirmPassword.getText().toString().trim();
                //Toast.makeText(ChangePassword.this, pass, Toast.LENGTH_LONG).show();

                if (pass.matches(comfPass)) {

                    JSONObject dataJson = new JSONObject();
                    try {
                        dataJson.put("email", email);
                        dataJson.put("password", pass);

                    } catch (JSONException e) {

                    }
                    postData(update_pass, dataJson, 1);

                }
                else{
                    Toast.makeText(ChangePassword.this, "Password Not Matched!", Toast.LENGTH_LONG).show();
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

                    checkResponseEditProduct(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
//                String alert_message;
//                alert_message = error.toString();
//                showAlertDialogue("Error", alert_message);
            }

        });
        requestQueue.add(json_obj_req);
    }


    public void checkResponseEditProduct(JSONObject response)
    {
        try {

            if(response.getInt("success")==1){
                Toast.makeText(ChangePassword.this, "Success", Toast.LENGTH_LONG).show();
                Intent i = new Intent(ChangePassword.this, MainActivity.class);
                startActivity(i);

            }else{
                //Error Response from server
                Toast.makeText(getApplicationContext(),response.toString(), Toast.LENGTH_LONG).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }


    }
}
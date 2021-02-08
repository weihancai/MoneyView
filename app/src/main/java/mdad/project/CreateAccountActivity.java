package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccountActivity extends AppCompatActivity {

    Button btnAccountCreate, btnGoBack2;
    EditText etAccountName;
    SharedPreferences prf;
    private static final String url_createAccount = "http://moneyview.atspace.cc/create_account.php";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        btnAccountCreate = (Button)findViewById(R.id.btnAccountCreate);
        btnGoBack2 = (Button)findViewById(R.id.btnGoBack2);
        etAccountName = (EditText)findViewById(R.id.etAccountName);

        btnGoBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateAccountActivity.this, AccountActivity.class);
                startActivity(i);
            }
        });


        btnAccountCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf = getSharedPreferences("userdetails",MODE_PRIVATE);
                String name = etAccountName.getText().toString();
                if(name.isEmpty())
                {
                    etAccountName.setError(getString(R.string.error_field_required));

                }
                else{
                    JSONObject dataJson = new JSONObject();
                    try{
                        dataJson.put("accountname", name);
                        dataJson.put("userid", prf.getString("userid", null));

                    }catch(JSONException e){

                    }
                    postData(url_createAccount,dataJson,1 );
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
                Toast.makeText(CreateAccountActivity.this, "Error" + alert_message, Toast.LENGTH_SHORT).show();
            }

        });
        requestQueue.add(json_obj_req);
    }
    public void checkResponseLogin(JSONObject response)
    {
        Log.i("----Response", response+" "+url_createAccount);
        try {
            if(response.getInt(TAG_SUCCESS)==1){
                Toast.makeText(this, "Account Creation Successful.", Toast.LENGTH_SHORT).show();
                finish();
                Intent i = new Intent(CreateAccountActivity.this, AccountActivity.class);
                startActivity(i);

            }else{
                Toast.makeText(this, "Account Creation Failed.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}
package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
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

import java.util.Random;

public class PasswordVerification extends AppCompatActivity {


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERS = "users";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_password = "password";

    JSONArray users = null;

    public EditText etEmail;
    public EditText etVeriCode;
    public LinearLayout VeryLayout;
    public Button btnEmail, btnVerified;
    public TextView tvCountDown;
    public static int Security_number;

    SharedPreferences prf;

    private static final String url_get_all_users = "http://moneyview.atspace.cc/get_all_usernames.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_verification);

        VeryLayout=(LinearLayout)findViewById(R.id.VeryLayout);
        btnEmail=(Button)findViewById(R.id.btnEmail);
        btnVerified=(Button)findViewById(R.id.btnVerified);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etVeriCode = (EditText)findViewById(R.id.etVeriCode);
        tvCountDown=(TextView)findViewById(R.id.tvCountDown);

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if (etEmail.getText().toString().isEmpty()) {
                    Toast.makeText(PasswordVerification.this, "Field Require", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("run from onclicl","onclick");
                    postData(url_get_all_users,null );

                }
            }
        });

        btnVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etVeriCode.getText().toString().isEmpty()) {
                    Toast.makeText(PasswordVerification.this, "Field Require", Toast.LENGTH_LONG).show();
                }
                else{
                    int enter_code= Integer.parseInt(etVeriCode.getText().toString());
                    int sec_Number=Security_number;
                    Log.i("Very Code",Security_number+"////"+enter_code);

                    if (enter_code == sec_Number) {
                        String mail = etEmail.getText().toString().trim();
                        Toast.makeText(PasswordVerification.this, "Successfully Verified", Toast.LENGTH_LONG).show();
                        prf = getSharedPreferences("userdetails", MODE_PRIVATE); //Open sharedPreference
                        SharedPreferences.Editor editor = prf.edit(); //set Editor mode
                        editor.putString("email", mail);
                        editor.commit();
                        finish();

                        Intent i = new Intent(PasswordVerification.this, ChangePassword.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(PasswordVerification.this, "Incorrect Verification Code", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });



    }

    private void sendMail() {

        VeryLayout.setVisibility(View.VISIBLE);

        Security_number =(int)(Math.random()*99999);
        String mail = etEmail.getText().toString().trim();
        String subject = "Verification code for MoneyView password recovery ";
        String message = "Hello! \n\n           You are receiving this e-mail because we received a password reset request" +
                " \n for your account\n\nVerification code: "+Security_number+" to reset your password.\n\n" +
                "If you did not request for a password reset, no further action is required.\n\nYours sincerely,\nMoneyViewTeam";

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);

        javaMailAPI.execute();

    }


    public void postData(String url, final JSONObject json){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest json_obj_req = new JsonObjectRequest(
                Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                checkResponse(response, json);

//                String alert_message;
//                alert_message = response.toString();

//                showAlertDialogue("Response", alert_message);

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

    private void checkResponse(JSONObject response, JSONObject creds){
        String key_emamil=etEmail.getText().toString();
        Boolean email_check= false;
        try {
            if(response.getInt(TAG_SUCCESS)==1){

                // products found
                // Getting Array of Products
                users = response.getJSONArray(TAG_USERS);

                // looping through All Products
                for (int i = 0; i < users.length(); i++) {
                    JSONObject c = users.getJSONObject(i);

                    // Storing each json item in variable
                    String user_name = c.getString(TAG_USERNAME);
                    String password = c.getString(TAG_password);
                    Log.i("Get Users",user_name);
                    Log.i("Keyed Email",key_emamil);
                    if(user_name.matches(key_emamil)){
                        email_check=true;
                        Log.i("Email match!!!!","There are match emails");
                    }
                }
                if(email_check==true){
                    sendMail();
                    new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tvCountDown.setText("Seconds remaining: " + millisUntilFinished / 1000);
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            VeryLayout.setVisibility(View.GONE);
                        }
                    }.start();
                }
                else{
                    Toast.makeText(PasswordVerification.this, "Unregistered Email!", Toast.LENGTH_LONG).show();

                }
            }
            else{

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


}
package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EditTransactionActivity extends AppCompatActivity {

    public static String url_allAccounts = "http://moneyview.atspace.cc//get_all_accounts.php";
    public static String url_updateTransaction = "http://moneyview.atspace.cc/update_transaction.php";
    public static String url_deleteTransaction = "http://moneyview.atspace.cc/delete_transaction.php";
    private static final String TAG_SUCCESS = "success";

    String[] expenseCategory = {"Food", "Social Life", "Self Development", "Transportation", "Culture", "Household", "Apparel", "Beauty", "Health", "Education", "Gift", "Other"};
    String[] incomeCategory = {"Allowance", "Salary", "Petty Cash", "Bonus"};
    String[] categoryIdIncomeArr = {"1", "2", "3", "4"};
    String[] categoryIdExpenseArr = {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};

    Spinner spinnerAccount, spinnerCategory;
    EditText etAmount;
    Button btnBack, btnUpdate, btnDelete;
    SharedPreferences prf, prf2;

    ArrayList<String> accounts = new ArrayList<>();
    ArrayList<String> accountsId = new ArrayList<>();

    ArrayAdapter<String> adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        spinnerAccount = (Spinner)findViewById(R.id.spinnerAccount);
        spinnerCategory = (Spinner)findViewById(R.id.spinnerCategory);
        etAmount = (EditText)findViewById(R.id.etAmount);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnDelete = (Button)findViewById(R.id.btnDelete);

        getJSON(url_allAccounts);

        prf2 = getSharedPreferences("transactiondetails",MODE_PRIVATE);
        if(prf2.getString("transactiondetails", null).equals("Expense")) {
            adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, expenseCategory);
        }
        else if(prf2.getString("transactiondetails", null).equals("Income")) {
            adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, incomeCategory);
        }
        spinnerCategory.setAdapter(adapterCategory);

        etAmount.setText(prf2.getString("transactionamount", null));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTransactionActivity.this, TransactionActivity.class);
                startActivity(i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject dataJson = new JSONObject();
                try{
                    dataJson.put("transactionid", prf2.getString("transactionid", null));
                }catch(JSONException e){

                }
                postData(url_deleteTransaction,dataJson,1 );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = etAmount.getText().toString();
                String categoryid = "";
                if(prf2.getString("transactiondetails", null).equals("Expense")) {
                    categoryid = categoryIdExpenseArr[spinnerCategory.getSelectedItemPosition()];
                }
                else if(prf2.getString("transactiondetails", null).equals("Income")) {
                    categoryid = categoryIdIncomeArr[spinnerCategory.getSelectedItemPosition()];
                }
                String accountIdArr[] = new String[accounts.size()];
                for (int j = 0; j < accounts.size(); j++) {
                    // Assign each value to String array
                    accountIdArr[j] = accountsId.get(j);
                }
                String accountid = accountIdArr[spinnerAccount.getSelectedItemPosition()];
                if(spinnerCategory.getSelectedItem() == null || amount.isEmpty() || spinnerAccount.getSelectedItem() == null){
                    Toast.makeText(EditTransactionActivity.this, "Blank inputs are not allowed.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String account = spinnerAccount.getSelectedItem().toString();
                    String category = spinnerCategory.getSelectedItem().toString();
                    JSONObject dataJson = new JSONObject();
                    try{
                        dataJson.put("categoryid", categoryid);
                        dataJson.put("accountid", accountid);
                        dataJson.put("transactionid", prf2.getString("transactionid", null));
                        dataJson.put("transactionamount", etAmount.getText().toString());
                    }catch(JSONException e){

                    }
                    postData(url_updateTransaction,dataJson,1 );
                }
            }
        });

    }
    //this method is actually fetching the json string
    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {
            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                prf = getSharedPreferences("userdetails",MODE_PRIVATE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("account");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String accountid = object.getString("accountid");
                        String userid = object.getString("userid");
                        String accountname = object.getString("accountname");
                        //String accountamount = object.getString("accountamount");
                        if(prf.getString("userid", null).equals(userid)) {
                            accounts.add(accountname);
                            accountsId.add(accountid);
                            //accountAmount.add(accountamount);
                        }
                    }
                    // declaration and initialise String Array
                    String str[] = new String[accounts.size()];
                    String accountIdArr[] = new String[accounts.size()];
                    //String accountAmountArr[] = new String[accounts.size()];
                    // ArrayList to Array Conversion
                    for (int j = 0; j < accounts.size(); j++) {
                        // Assign each value to String array
                        str[j] = accounts.get(j);
                        accountIdArr[j] = accountsId.get(j);
                        //accountAmountArr[j] = accountAmount.get(j);
                    }
                    ArrayAdapter<String> adapterAccounts = new ArrayAdapter<>(EditTransactionActivity.this, android.R.layout.simple_spinner_dropdown_item, str);
                    spinnerAccount.setAdapter(adapterAccounts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

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
                Toast.makeText(EditTransactionActivity.this, "Error" + alert_message, Toast.LENGTH_SHORT).show();
            }

        });
        requestQueue.add(json_obj_req);
    }
    public void checkResponseLogin(JSONObject response)
    {
        Log.i("----Response", response+" "+url_updateTransaction);
        try {
            if(response.getInt(TAG_SUCCESS)==1){
                Toast.makeText(this, "Transaction Changes Successful.", Toast.LENGTH_SHORT).show();
                finish();
                Intent i = new Intent(EditTransactionActivity.this, TransactionActivity.class);
                startActivity(i);

            }else{
                Toast.makeText(this, "Transaction Changes Failed.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}
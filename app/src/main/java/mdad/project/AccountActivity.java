package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    public static String url_allAccounts = "http://moneyview.atspace.cc/get_all_accounts.php";
    public static String url_allTransactions = "http://moneyview.atspace.cc/get_all_transactions.php";
    private static final String TAG_SUCCESS = "success";

    TextView tvTotal2;
    ListView listView;
    Button btnCreateAcc, btnTrans, btnStats, btnLogout;
    SharedPreferences prf;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayAccount = new ArrayList<>();
    ArrayList<String> arrayAccountId = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String[] accountNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        tvTotal2 = (TextView)findViewById(R.id.tvTotal2);
        listView = (ListView)findViewById(R.id.listView);
        btnCreateAcc = (Button)findViewById(R.id.btnCreateAcc);
        btnTrans = (Button)findViewById(R.id.btnTrans);
        btnStats = (Button)findViewById(R.id.btnStats);
        btnLogout = (Button)findViewById(R.id.btnLogout);

        getJSON(url_allAccounts);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prf = getSharedPreferences("accountdetails", MODE_PRIVATE); //Open sharedPreference
                SharedPreferences.Editor editor = prf.edit(); //set Editor mode
                editor.putString("accountid", arrayAccountId.get(position));
                editor.putString("accountname", arrayAccount.get(position));
                editor.commit();
                Intent i = new Intent(AccountActivity.this, EditAccountActivity.class);
                startActivity(i);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf = getSharedPreferences("userdetails",MODE_PRIVATE);
                SharedPreferences.Editor editor = prf.edit();
                editor.clear().commit();
                Intent i = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountActivity.this, CreateAccountActivity.class);
                startActivity(i);
            }
        });

        btnTrans.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountActivity.this, TransactionActivity.class);
                startActivity(i);
            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountActivity.this, StatsActivity.class);
                startActivity(i);
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
                            //arrayList.add("[" + accountname + "] Amount: $" + accountamount);
                            //total = total + Double.parseDouble(accountamount);
                            //SharedPreferences.Editor editor = prf.edit(); //set Editor mode
                            //editor.putString("userid", userid);
                            //editor.commit();
                            arrayAccount.add(accountname);
                            arrayAccountId.add(accountid);
                        }
                        //tvTotal2.setText("Total: $" + total);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getJSON2(url_allTransactions);
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

    //this method is actually fetching the json string
    private void getJSON2(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON2 extends AsyncTask<Void, Void, String> {
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
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                arrayList.clear();
                prf = getSharedPreferences("userdetails",MODE_PRIVATE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                    double[] accountValueArr = new double[arrayAccount.size()];
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String accountname = object.getString("accountname");
                        String transactionamount = object.getString("transactionamount");
                        String userid = object.getString("userid");
                        if(prf.getString("userid", null).equals(userid)) {
                            //arrayList.add("[" + created_at + "] " + "(" + transactiondetails + ") " + categoryname + "   $" + transactionamount + " (" + transactionnote + ")");
                            for(int j = 0; j < arrayAccount.size(); j++){
                                if(arrayAccount.get(j).equals(accountname)){
                                    accountValueArr[j] = accountValueArr[j] + Double.parseDouble(transactionamount);
                                }
                            }
                        }
                    }
                    for(int x = 0; x < arrayAccount.size(); x++){
                        arrayList.add("[" + arrayAccount.get(x) + "] Amount: $" + accountValueArr[x]);
                    }
                    double total = 0;
                    for(int y = 0; y < accountValueArr.length; y++){
                        total = total + accountValueArr[y];
                    }
                    tvTotal2.setText("Total: $" + total);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrayAdapter = new ArrayAdapter<>(AccountActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(arrayAdapter);
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
        GetJSON2 getJSON = new GetJSON2();
        getJSON.execute();
    }
}
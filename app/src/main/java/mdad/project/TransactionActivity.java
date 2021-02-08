package mdad.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TransactionActivity extends AppCompatActivity {
    public static String url_allTransactions = "http://moneyview.atspace.cc/get_all_transactions.php";
    public static String url_yearTransactions = "http://moneyview.atspace.cc/get_year_transactions.php";
    public static String url_monthTransactions = "http://moneyview.atspace.cc/get_month_transactions.php";
    public static String url_weekTransactions = "http://moneyview.atspace.cc/get_week_transactions.php";
    public static String url_todayTransactions = "http://moneyview.atspace.cc/get_today_transactions.php";
    public static String url_allUsers = "http://moneyview.atspace.cc/get_all_users.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TRANSACTIONS = "transactions";
    TextView tvIncome;
    TextView tvExpense;
    TextView tvTotal;
    ListView listView;
    Spinner spinnerMonth, spinnerYear, spinnerFilter;
    Button btnTrans, btnStats, btnAccount, btnFilter, btnFilter2, btnCreateTrans;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> accountIdArr = new ArrayList<>();
    ArrayList<String> accountNameArr = new ArrayList<>();
    ArrayList<String> categoryIdArr = new ArrayList<>();
    ArrayList<String> transIdArr = new ArrayList<>();
    ArrayList<String> transDetailArr = new ArrayList<>();
    ArrayList<String> transAmountArr = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    double income, expense = 0;
    SharedPreferences prf, prf2, prf3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        getJSON3(url_allUsers);

        tvIncome = (TextView)findViewById(R.id.tvIncome);
        tvExpense = (TextView)findViewById(R.id.tvExpense);
        tvTotal = (TextView)findViewById(R.id.tvTotal);
        listView = (ListView)findViewById(R.id.listView);
        btnTrans = (Button)findViewById(R.id.btnTrans);
        btnCreateTrans = (Button)findViewById(R.id.btnCreateTrans);
        btnStats = (Button)findViewById(R.id.btnStats);
        btnAccount = (Button)findViewById(R.id.btnAccount);
        btnFilter = (Button)findViewById(R.id.btnFilter);
        btnFilter2 = (Button)findViewById(R.id.btnFilter2);
        spinnerMonth = (Spinner)findViewById(R.id.spinnerMonth);
        spinnerYear = (Spinner)findViewById(R.id.spinnerYear);
        spinnerFilter = (Spinner)findViewById(R.id.spinnerFilter);
        //prf = getSharedPreferences("userdetails",MODE_PRIVATE);
        //tvIncome.setText(prf.getString("username", null));


        int month = Calendar.getInstance().get(Calendar.MONTH);

        Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        String[] monthItems = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monthItems);
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setSelection(month);

        Spinner spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        ArrayList<String> spinnerYears = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2000; i <= thisYear; i++) {
            spinnerYears.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerYears);
        spinnerYear.setAdapter(adapterYear);
        spinnerYear.setSelection(spinnerYears.size() - 1);

        Spinner spinnerFilter = (Spinner) findViewById(R.id.spinnerFilter);
        String[] filterItems = new String[]{"Today", "This Week", "This Month", "This Year", "All Time"};
        ArrayAdapter<String> adapterFilter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterItems);
        spinnerFilter.setAdapter(adapterFilter);
        spinnerFilter.setSelection(4);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransactionActivity.this, AccountActivity.class);
                startActivity(i);
            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransactionActivity.this, StatsActivity.class);
                startActivity(i);
            }
        });

        btnCreateTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransactionActivity.this, CreateExpenseActivity.class);
                startActivity(i);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] monthNumbers = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
                expense = 0;
                income = 0;
                tvExpense.setText("Expense: $" + expense);
                tvIncome.setText("Income: $" + income);
                tvTotal.setText("Total: $" + (income - expense));
                prf3 = getSharedPreferences("datefilter",MODE_PRIVATE);
                SharedPreferences.Editor editor3 = prf3.edit(); //set Editor mode
                editor3.putString("year", spinnerYear.getSelectedItem().toString());
                editor3.putString("month", monthNumbers[spinnerMonth.getSelectedItemPosition()]);
                editor3.commit();
                //Toast.makeText(TransactionActivity.this, spinnerYear.getSelectedItem().toString()+" "+monthNumbers[spinnerMonth.getSelectedItemPosition()], Toast.LENGTH_SHORT).show();
                getJSON2(url_allTransactions);
            }
        });

        btnFilter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expense = 0;
                income = 0;
                tvExpense.setText("Expense: $" + expense);
                tvIncome.setText("Income: $" + income);
                tvTotal.setText("Total: $" + (income - expense));
                if(spinnerFilter.getSelectedItem().toString() == "Today"){
                    getJSON(url_todayTransactions);
                }
                else if(spinnerFilter.getSelectedItem().toString() == "This Week"){
                    getJSON(url_weekTransactions);
                }
                else if(spinnerFilter.getSelectedItem().toString() == "This Month"){
                    getJSON(url_monthTransactions);
                }
                else if(spinnerFilter.getSelectedItem().toString() == "This Year"){
                    getJSON(url_yearTransactions);
                }
                else if(spinnerFilter.getSelectedItem().toString() == "All Time"){
                    getJSON(url_allTransactions);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor2 = prf2.edit(); //set Editor mode
                editor2.putString("accountid", accountIdArr.get(position));
                editor2.putString("accountname", accountNameArr.get(position));
                editor2.putString("categoryid", categoryIdArr.get(position));
                editor2.putString("transactionid", transIdArr.get(position));
                editor2.putString("transactionamount", transAmountArr.get(position));
                editor2.putString("transactiondetails", transDetailArr.get(position));
                editor2.commit();
                Intent i = new Intent(TransactionActivity.this, EditTransactionActivity.class);
                startActivity(i);
            }
        });

        getJSON(url_allTransactions);
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
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                arrayList.clear();
                prf = getSharedPreferences("userdetails",MODE_PRIVATE);
                prf2 = getSharedPreferences("transactiondetails",MODE_PRIVATE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String username = object.getString("username");
                        String created_at = object.getString("created_at");
                        String categoryname = object.getString("categoryname");
                        String transactiondetails = object.getString("transactiondetails");
                        String accountname = object.getString("accountname");
                        String transactionamount = object.getString("transactionamount");
                        String userid = object.getString("userid");
                        String accountid = object.getString("accountid");
                        String categoryid = object.getString("categoryid");
                        String transactionid = object.getString("transactionid");
                        if(prf.getString("username", null).equals(username)) {
                            arrayList.add("[" + created_at + "] " + "(" + transactiondetails + ") " + categoryname + "   $" + transactionamount + " (" + accountname + ")");
                            accountIdArr.add(accountid);
                            accountNameArr.add(accountname);
                            categoryIdArr.add(categoryid);
                            transDetailArr.add(transactiondetails);
                            transIdArr.add(transactionid);
                            transAmountArr.add(transactionamount);
                            if ("Expense".equals(transactiondetails)) {
                                expense = expense + Double.parseDouble(transactionamount);
                            } else if ("Income".equals(transactiondetails)) {
                                income = income + Double.parseDouble(transactionamount);
                            }
                            SharedPreferences.Editor editor = prf.edit(); //set Editor mode
                            editor.putString("userid", userid);
                            editor.commit();
                        }
                        //.Editor editor2 = prf2.edit(); //set Editor mode
                        //editor2.putString("accountid", accountid);
                        //editor2.putString("accountname", accountname);
                        //editor2.putString("categoryid", categoryid);
                        //editor2.putString("transactionid", transactionid);
                        //editor2.putString("transactionamount", transactionamount);
                        //editor2.commit();
                        tvExpense.setText("Expense: $" + expense);
                        tvIncome.setText("Income: $" + income);
                        tvTotal.setText("Total: $" + (income - expense));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrayAdapter = new ArrayAdapter<>(TransactionActivity.this, android.R.layout.simple_list_item_1, arrayList);
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
        GetJSON getJSON = new GetJSON();
        getJSON.execute();

    }
    //private void loadIntoListView(String json) throws JSONException {
    //    JSONArray jsonArray = new JSONArray(json);
    //    String[] transactions = new String[jsonArray.length()];
    //    for (int i = 0; i < jsonArray.length(); i++) {
    //        prf = getSharedPreferences("userdetails",MODE_PRIVATE);
    //        JSONObject obj = jsonArray.getJSONObject(i);
    //        if(obj.getString("username") == prf.getString("username", null)){
    //            transactions[i] = obj.getString("transactionamount");
    //        }
    //    }
     //   ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, transactions);
    //    listView.setAdapter(arrayAdapter);
    //}

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
                prf2 = getSharedPreferences("transactiondetails",MODE_PRIVATE);
                prf3 = getSharedPreferences("datefilter",MODE_PRIVATE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String username = object.getString("username");
                        String created_at = object.getString("created_at");
                        String categoryname = object.getString("categoryname");
                        String transactiondetails = object.getString("transactiondetails");
                        String accountname = object.getString("accountname");
                        String transactionamount = object.getString("transactionamount");
                        String userid = object.getString("userid");
                        String accountid = object.getString("accountid");
                        String categoryid = object.getString("categoryid");
                        String transactionid = object.getString("transactionid");
                        if(prf.getString("username", null).equals(username)) {
                            prf3 = getSharedPreferences("datefilter", MODE_PRIVATE);
                            String month = prf3.getString("month", null);
                            String year = prf3.getString("year", null);
                            if (created_at.substring(0, 4).trim().equals(year) && created_at.substring(5, 7).trim().equals(month)) {
                                arrayList.add("[" + created_at + "] " + "(" + transactiondetails + ") " + categoryname + "   $" + transactionamount + " (" + accountname + ")");
                                accountIdArr.add(accountid);
                                accountNameArr.add(accountname);
                                categoryIdArr.add(categoryid);
                                transDetailArr.add(transactiondetails);
                                transIdArr.add(transactionid);
                                transAmountArr.add(transactionamount);
                                if ("Expense".equals(transactiondetails)) {
                                    expense = expense + Double.parseDouble(transactionamount);
                                } else if ("Income".equals(transactiondetails)) {
                                    income = income + Double.parseDouble(transactionamount);
                                }
                                SharedPreferences.Editor editor = prf.edit(); //set Editor mode
                                editor.putString("userid", userid);
                                editor.commit();
                            }
                            //.Editor editor2 = prf2.edit(); //set Editor mode
                            //editor2.putString("accountid", accountid);
                            //editor2.putString("accountname", accountname);
                            //editor2.putString("categoryid", categoryid);
                            //editor2.putString("transactionid", transactionid);
                            //editor2.putString("transactionamount", transactionamount);
                            //editor2.commit();
                            tvExpense.setText("Expense: $" + expense);
                            tvIncome.setText("Income: $" + income);
                            tvTotal.setText("Total: $" + (income - expense));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrayAdapter = new ArrayAdapter<>(TransactionActivity.this, android.R.layout.simple_list_item_1, arrayList);
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

    //this method is actually fetching the json string
    private void getJSON3(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON3 extends AsyncTask<Void, Void, String> {
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
                prf = getSharedPreferences("userdetails",MODE_PRIVATE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String username = object.getString("username");
                        String userid = object.getString("userid");
                        if(prf.getString("username", null).equals(username)) {
                            SharedPreferences.Editor editor = prf.edit(); //set Editor mode
                            editor.putString("userid", userid);
                            editor.commit();
                        }
                    }
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
        GetJSON3 getJSON = new GetJSON3();
        getJSON.execute();

    }
}

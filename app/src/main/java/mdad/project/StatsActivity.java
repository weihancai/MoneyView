package mdad.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    public static String url_allTransactions = "http://moneyview.atspace.cc/get_all_transactions.php";
    public static String url_yearTransactions = "http://moneyview.atspace.cc/get_year_transactions.php";
    public static String url_monthTransactions = "http://moneyview.atspace.cc/get_month_transactions.php";
    public static String url_weekTransactions = "http://moneyview.atspace.cc/get_week_transactions.php";
    public static String url_todayTransactions = "http://moneyview.atspace.cc/get_today_transactions.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TRANSACTIONS = "transactions";

    Spinner spinnerStatsFilter, spinnerStatsCategory;
    AnyChartView anyChartView;
    Button btnStatsFilter, btnTrans, btnAccount;
    SharedPreferences prf;

    String[] expenseCategory = {"Food", "Social Life", "Self-Development", "Transportation", "Culture", "Household", "Apparel", "Beauty", "Health", "Education", "Gift", "Other"};
    double[] expense = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    String[] incomeCategory = {"Allowance", "Salary", "Petty Cash", "Bonus"};
    double[] income = {0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        anyChartView = findViewById(R.id.any_chart_view);
        spinnerStatsFilter = findViewById(R.id.spinnerStatsFilter);
        spinnerStatsCategory = findViewById(R.id.spinnerStatsCategory);
        btnStatsFilter = findViewById(R.id.btnStatsFilter);
        btnTrans = findViewById(R.id.btnTrans);
        btnAccount = findViewById(R.id.btnAccount);

        String[] filterItems = new String[]{"Today", "This Week", "This Month", "This Year", "All Time"};
        ArrayAdapter<String> adapterFilter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterItems);
        spinnerStatsFilter.setAdapter(adapterFilter);
        spinnerStatsFilter.setSelection(4);

        String[] categoryItems = new String[]{"Expense", "Income"};
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryItems);
        spinnerStatsCategory.setAdapter(adapterCategory);
        spinnerStatsCategory.setSelection(0);

        getJSON(url_allTransactions);
        Pie pie = AnyChart.pie();
        pie.title("Click Filter button to view data!");
        anyChartView.setChart(pie);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatsActivity.this, AccountActivity.class);
                startActivity(i);
            }
        });

        btnTrans.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatsActivity.this, TransactionActivity.class);
                startActivity(i);
            }
        });

        btnStatsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerStatsFilter.getSelectedItemPosition() == 4){
                    getJSON(url_allTransactions);
                }
                if(spinnerStatsFilter.getSelectedItemPosition() == 3){
                    getJSON(url_yearTransactions);
                }
                if(spinnerStatsFilter.getSelectedItemPosition() == 2){
                    getJSON(url_monthTransactions);
                }
                if(spinnerStatsFilter.getSelectedItemPosition() == 1){
                    getJSON(url_weekTransactions);
                }
                if(spinnerStatsFilter.getSelectedItemPosition() == 0){
                    getJSON(url_todayTransactions);
                }
                if(spinnerStatsCategory.getSelectedItem().toString() == "Income") {
                    List<DataEntry> data = new ArrayList<>();
                    for (int i = 0; i < incomeCategory.length; i++) {
                        data.add(new ValueDataEntry(incomeCategory[i], income[i]));
                    }
                    pie.title("Income");
                    pie.data(data);
                }
                else if(spinnerStatsCategory.getSelectedItem().toString() == "Expense") {
                    List<DataEntry> data = new ArrayList<>();
                    for (int i = 0; i < expenseCategory.length; i++) {
                        data.add(new ValueDataEntry(expenseCategory[i], expense[i]));
                    }
                    pie.title("Expense");
                    pie.data(data);
                }
                for (int x = 0; x < income.length; x++) {
                    income[x] = 0;
                }
                for (int y = 0; y < expense.length; y++) {
                    expense[y] = 0;
                }
            }
        });
    }

    public void setupPieChart(){
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
            for (int i = 0; i < expenseCategory.length; i++) {
                dataEntries.add(new ValueDataEntry(expenseCategory[i], expense[i]));
            }
            pie.data(dataEntries);
            pie.title("Expense");
        anyChartView.setChart(pie);
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
                prf = getSharedPreferences("userdetails",MODE_PRIVATE);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String categoryname = object.getString("categoryname");
                        String transactiondetails = object.getString("transactiondetails");
                        String transactionamount = object.getString("transactionamount");
                        String userid = object.getString("userid");
                        if(prf.getString("userid", null).equals(userid)) {
                            //arrayList.add("[" + created_at + "] " + "(" + transactiondetails + ") " + categoryname + "   $" + transactionamount + " (" + transactionnote + ")");
                            if("Income".equals(transactiondetails)) {
                                for (int x = 0; x < income.length; x++) {
                                    if(incomeCategory[x].equals(categoryname)){
                                        income[x] = income[x] + Double.parseDouble(transactionamount);
                                    }
                                }
                            }
                            else if("Expense".equals(transactiondetails)) {
                                for (int x = 0; x < expense.length; x++) {
                                    if(expenseCategory[x].equals(categoryname)){
                                        expense[x] = expense[x] + Double.parseDouble(transactionamount);
                                    }
                                }
                            }
                        }
                        //tvExpense.setText("Expense: $" + expense);
                        //tvIncome.setText("Income: $" + income);
                        //tvTotal.setText("Total: $" + (income - expense));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //arrayAdapter = new ArrayAdapter<>(TransactionActivity.this, android.R.layout.simple_list_item_1, arrayList);
                //listView.setAdapter(arrayAdapter);
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
}
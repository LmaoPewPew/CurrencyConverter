package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {


    /*TODO::
      7: Store and restore (+ Music player)
      8: Update app
      9: Toast
     */

    /********Set Global Variables Variables********/
    ExchangeRateDatabase db = new ExchangeRateDatabase();
    private ShareActionProvider sap;

    Runnable runnable = () -> {
        Toast toast = Toast.makeText(getApplicationContext(), "ExchangeRate Updated", Toast.LENGTH_SHORT);
        toast.show();
        // notifier.show();
    };

    /********ON CREATE METHODE********/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFunctions();
        spinnerAdapter(new ExchangeRateAdapter(Arrays.asList(db.getCurrencies())));

        updateCurrencies();
    }

    @Override
    protected void onPause() {
        super.onPause();

        TextView valIn = findViewById(R.id.ValInput);
        TextView valOut = findViewById(R.id.ValOutput);
        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("spinner1", spFrom.getSelectedItem().toString());
        editor.putString("spinner2", spTo.getSelectedItem().toString());
        editor.putString("textView1", valIn.getText().toString());
        editor.putString("textView2", valOut.getText().toString());

        editor.apply();
/*
        for (String currency : db.getCurrencies()) {
            editor.putFloat(currency, (float) ExchangeRateDatabase.getExchangeRate(currency));
        }
        */

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        TextView valIn = findViewById(R.id.ValInput);
        TextView valOut = findViewById(R.id.ValOutput);
        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        String valInString = pref.getString("textView1", "");
        String valOutString = pref.getString("textView2", "");
        String spFromString = pref.getString("spinner1", "");
        String spToString = pref.getString("spinner2", "");

        valIn.setText(valInString);
        valOut.setText(valOutString);
        spFrom.setSelection(db.getIndexOf(spFromString));
        spTo.setSelection(db.getIndexOf(spToString));
/*
        for (String currency : db.getCurrencies()) {
            ExchangeRateDatabase.setExchangeRate(currency, pref.getFloat(currency, 0));
        }
        */

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        TextView valOut = findViewById(R.id.ValOutput);
        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        outState.putString("textView", valOut.getText().toString());
        outState.putString("spinner1", spFrom.getSelectedItem().toString());
        outState.putString("spinner2", spTo.getSelectedItem().toString());
    }

    /********ACTIONBAR-MENU********/
    //actionbar menu items
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TextView result = (TextView) findViewById(R.id.ValOutput);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem shareItem = menu.findItem(R.id.item_share);
        sap = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareText(result.getText().toString());

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        TextView valOut = findViewById(R.id.ValOutput);
        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        spFrom.setSelection(Arrays.asList(db.getCurrencies()).indexOf(savedInstanceState.getString("spinner1")));
        spTo.setSelection(Arrays.asList(db.getCurrencies()).indexOf(savedInstanceState.getString("spinner2")));
        valOut.setText(savedInstanceState.getString("textView"));
    }

    // MenuItem Interaction
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_list:
                Intent CurrencyIntent = new Intent(this, Currency_List_Viewer.class);
                startActivity(CurrencyIntent);
                createToast("Currency List");
                break;
            case R.id.item_refresh:
                updateCurrencies();

                Intent MainIntent = new Intent(this, MainActivity.class);
                startActivity(MainIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /********Refresh Cur-Rates********/
    private void updateCurrencies() {
        if (isNetworkAvailable()) {
            createToast("Checking for Updates");
            ExchangeRateAdapter exa = new ExchangeRateAdapter(Arrays.asList(db.getCurrencies()));

            currencyAPI();
            exa.notifyDataSetChanged();
            this.runOnUiThread(runnable);
        } else createToast("internet-connection is unavailable");
    }

    private void currencyAPI() {

        Thread thread = new Thread(() -> {
            String webString = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
            try {
                URL url = new URL(webString);
                URLConnection connection = url.openConnection();
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(connection.getInputStream(), connection.getContentEncoding());
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("Cube")) {
                            String currency = parser.getAttributeValue(null, "currency");
                            String rate = parser.getAttributeValue(null, "rate");
                            if (currency != null && rate != null) {
                                ExchangeRateDatabase.setExchangeRate(currency, Double.parseDouble(rate));
                                Log.d("Currency", currency + " | " + rate);
                            }
                        }
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                Log.e("ErrorURL", "Error with XML: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    /********Smaller MMethods********/
    @SuppressLint("SetTextI18n")
    public void calculation(TextView in, TextView out, Spinner spFrom, Spinner spTo) {
        if (TextUtils.isEmpty(in.getText().toString())) out.setText("0");
        else {
            double conversion = db.convert(Double.parseDouble(in.getText().toString()), spFrom.getSelectedItem().toString(), spTo.getSelectedItem().toString());
            conversion = (double) Math.floor(conversion * 100) / 100;

            out.setText(Double.toString(conversion));
        }
    }

    private void createFunctions() {
        TextView valIn = findViewById(R.id.ValInput);
        TextView valOut = findViewById(R.id.ValOutput);

        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        Button btnCalc = findViewById(R.id.btnCalc);

        spinnerAdapter(new ExchangeRateAdapter(Arrays.asList(db.getCurrencies())));

        btnCalc.setTextSize(20f);
        btnCalc.setOnClickListener(v -> calculation(valIn, valOut, spFrom, spTo));

        checkTheme(valIn);
        checkTheme(valOut);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        sap.setShareIntent(shareIntent);
    }

    public void spinnerAdapter(ExchangeRateAdapter adapter) {
        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);

        spFrom.setSelection(8);
        spTo.setSelection(30);

    }

    private void checkTheme(TextView textView) {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                textView.setTextColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                textView.setTextColor(Color.BLACK);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                textView.setTextColor(Color.DKGRAY);
                break;
        }
    }

    public void createToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}

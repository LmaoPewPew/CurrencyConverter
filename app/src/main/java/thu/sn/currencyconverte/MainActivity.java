package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {


    /********Set Global Variables Variables********/
    ExchangeRateDatabase db = ExchangeRateDatabaseAccess.getExchangeRateDatabase();
    private ShareActionProvider sap;

    Runnable runnable = () -> {
        Toast toast = Toast.makeText(getApplicationContext(), "ExchangeRate Updated", Toast.LENGTH_SHORT);
        toast.show();

    };

    /********ON CREATE METHODE********/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkThemeActionBar();
        createFunctions();
        spinnerAdapter(ExchangeRateDatabaseAccess.getExchangeRateAdapter());

        schedulePeriodicCounting();
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

        WorkRequest rateUpdateRequest = new OneTimeWorkRequest.Builder(ExchangeRateUpdateWorker.class).build();
        WorkManager.getInstance(this).enqueue(rateUpdateRequest);

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


        WorkRequest rateUpdateRequest = new OneTimeWorkRequest.Builder(ExchangeRateUpdateWorker.class).build();
        WorkManager.getInstance(this).enqueue(rateUpdateRequest);

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
        TextView result = findViewById(R.id.ValOutput);
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

        spFrom.setSelection(Arrays.asList(ExchangeRateDatabase.getCurrencies()).indexOf(savedInstanceState.getString("spinner1")));
        spTo.setSelection(Arrays.asList(ExchangeRateDatabase.getCurrencies()).indexOf(savedInstanceState.getString("spinner2")));
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
            case R.id.item_update:
                if (isNetworkAvailable()) {
                    WorkRequest rateUpdateRequest = new OneTimeWorkRequest.Builder(ExchangeRateUpdateWorker.class).build();
                    WorkManager.getInstance(this).enqueue(rateUpdateRequest);

                    Intent MainIntent = new Intent(this, MainActivity.class);
                    startActivity(MainIntent);

                    this.runOnUiThread(runnable);
                } else createToast("Internet Connection Unavailable");
                break;
            case R.id.item_reset:
                Log.d("Reset", "hi");
                resetValues();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void calculation(TextView in, TextView out, Spinner spFrom, Spinner spTo) {
        if (TextUtils.isEmpty(in.getText().toString())) out.setText("0");
        else {
            double conversion = db.convert(Double.parseDouble(in.getText().toString()), spFrom.getSelectedItem().toString(), spTo.getSelectedItem().toString());
            conversion = Math.floor(conversion * 100) / 100;

            out.setText(Double.toString(conversion));
        }
    }

    private void createFunctions() {
        TextView valIn = findViewById(R.id.ValInput);
        TextView valOut = findViewById(R.id.ValOutput);

        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        Button btnCalc = findViewById(R.id.btnCalc);

        spinnerAdapter(ExchangeRateDatabaseAccess.getExchangeRateAdapter());

        btnCalc.setTextSize(20f);
        btnCalc.setOnClickListener(v -> calculation(valIn, valOut, spFrom, spTo));

        TextView textFrom = findViewById(R.id.txtFrom);
        TextView textTo = findViewById(R.id.txtFrom2);

        checkTheme(textFrom);
        checkTheme(textTo);
        checkTheme(valIn);
        checkTheme(valOut);
        checkTheme(btnCalc);

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

    public void checkTheme(TextView textView) {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                textView.setTextColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                textView.setTextColor(Color.BLACK);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                textView.setTextColor(Color.GRAY);
                break;
        }
    }

    public void checkTheme(Button button) {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        button.setTextColor(Color.WHITE);
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                button.setBackgroundColor(Color.parseColor("#282857"));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                button.setBackgroundColor(Color.parseColor("#6088b3"));
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                button.setTextColor(Color.GRAY);
                button.setBackgroundColor(Color.parseColor("#1c8777"));
                break;
        }
    }

    public void checkThemeActionBar() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        ActionBar ab = getSupportActionBar();

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                assert ab != null;
                ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#282857")));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                assert ab != null;
                ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6088b3")));
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                assert ab != null;
                ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1c8777")));
                break;
        }
    }


    public void createToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void schedulePeriodicCounting() {
        WorkManager workManager = WorkManager.getInstance(this);
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();
        PeriodicWorkRequest periodicCounterRequest =
                new PeriodicWorkRequest.Builder(ExchangeRateUpdateWorker.class, 24, TimeUnit.HOURS).setConstraints(constraints).addTag("CurrencyUpdate").build();
        workManager.enqueueUniquePeriodicWork("CurrencyUpdate", ExistingPeriodicWorkPolicy.KEEP, periodicCounterRequest);

    }

    private void resetValues() {
        TextView valIn = findViewById(R.id.ValInput);
        TextView valOut = findViewById(R.id.ValOutput);

        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        valIn.setText("");
        valOut.setText("");
        spFrom.setSelection(8);
        spTo.setSelection(30);
    }

    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

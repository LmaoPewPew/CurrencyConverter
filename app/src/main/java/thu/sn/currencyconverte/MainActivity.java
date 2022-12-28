package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;


public class MainActivity extends AppCompatActivity {


    /**TODO::
     * read flags in CurrencyItemAdapter getFlagId
     * Use CIA for Spinners
     * fix Share Button
     *
     * Currency_List_Viewer: Click Currency -> Open Google Maps with the capital
     *
     * if done, then exercise 5 onward:
     */

    /********Set Global Variables Variables********/

    ExchangeRateDatabase db = new ExchangeRateDatabase();
   private ShareActionProvider sap;

    /********ON CREATE METHODE********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /********Set Local Variables Variables********/
        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        Configuration newConfig = new Configuration();

        /********MethodReference********/
        onConfigurationChanged(newConfig);
        SpinnerAdapterMethod(spFrom, spTo);
        //SpinnerAdapter(spFrom, spTo, new CurrencyItemAdapter(Arrays.asList(db.getCurrencies())));
    }


    /********ACTIONBAR-MENU********/
    //actionbar menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        MenuItem menuItem = menu.findItem(R.id.item_share);
        //sap = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        return super.onCreateOptionsMenu(menu);
    }


    // MenuItem Interaction
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_list:
                Intent i = new Intent(this, Currency_List_Viewer.class);
                startActivity(i);
                break;
            case R.id.item_refresh:
                checkForUpdates();
                break;
            case R.id.item_share:
              share();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /********Orientations********/

    //Phone Rotation Check
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.landscape);
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }

        createFunctions();
    }

    private void createFunctions() {

        TextView valIn = findViewById(R.id.ValInput);
        TextView valOut = findViewById(R.id.ValOutput);

        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);
        SpinnerAdapterMethod(spFrom, spTo);


        Button btnCalc = findViewById(R.id.btnCalc);
        btnCalc.setOnClickListener(v -> calculation(valIn, valOut, spFrom, spTo));
    }

    private void checkForUpdates() {
        Toast.makeText(this, "Checking for Updates", Toast.LENGTH_LONG).show();
    }

    /********Share Function********/
/*
    private void setShareText(String text) {
        ShareActionProvider shareActionProvider = new ShareActionProvider(this);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }
    */
    private void share() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Share");
        sap.setShareIntent(intent);

        Toast.makeText(this, "Share", Toast.LENGTH_LONG).show();
    }

    //Create Spinner Adapter
    public void SpinnerAdapterMethod(Spinner spFrom, Spinner spTo) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, db.getCurrencies());
        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);
    }


    //ExChangeRateAdapter
    public void SpinnerAdapter(Spinner spFrom, Spinner spTo, CurrencyItemAdapter dia) {
        ListView lv = findViewById(R.id.ListView);
        lv.setAdapter(dia);
        spFrom.setAdapter(dia);
        spTo.setAdapter(dia);
    }

    //Button Pressed
    @SuppressLint("SetTextI18n")
    public void calculation(TextView in, TextView out, Spinner spFrom, Spinner spTo) {
        if (TextUtils.isEmpty(in.getText().toString())) out.setText("0");
        else {
            double conversion = db.convert(Double.parseDouble(in.getText().toString()), spFrom.getSelectedItem().toString(), spTo.getSelectedItem().toString());
            conversion = (double) Math.round(conversion * 100) / 100;

            out.setText(Double.toString(conversion));
        }

    }
}

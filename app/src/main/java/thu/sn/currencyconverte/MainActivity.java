package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {


    /*TODO::
      5: Daily Update of Rates via API
      7: Store and restore (+ Music player)
      8: Update app
      9: Toast
     */

    /********Set Global Variables Variables********/

    ExchangeRateDatabase db = new ExchangeRateDatabase();
    private ShareActionProvider sap;

    /********ON CREATE METHODE********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onConfigurationChanged(getResources().getConfiguration());
        SpinnerAdapter(new ExchangeRateAdapter(Arrays.asList(db.getCurrencies())));
    }


    /********ACTIONBAR-MENU********/
    //actionbar menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TextView result = (TextView) findViewById(R.id.ValOutput);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem shareItem = menu.findItem(R.id.item_share);
        sap = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareText(result.getText().toString());

        return true;
    }


    // MenuItem Interaction
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_list:
                Intent listViewerIntent = new Intent(this, Currency_List_Viewer.class);
                startActivity(listViewerIntent);
                break;
            case R.id.item_refresh:
                checkForUpdates();
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
        SpinnerAdapter(new ExchangeRateAdapter(Arrays.asList(db.getCurrencies())));

        Button btnCalc = findViewById(R.id.btnCalc);
        btnCalc.setOnClickListener(v -> calculation(valIn, valOut, spFrom, spTo));
    }

    private void checkForUpdates() {
        Toast.makeText(this, "Checking for Updates", Toast.LENGTH_LONG).show();
    }

    /********Share Function********/

    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        sap.setShareIntent(shareIntent);
    }


    /********Adapter********/
    //Create Spinner Adapter
    public void SpinnerAdapter(ExchangeRateAdapter adapter) {
        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);
    }

    /********Calculate********/
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

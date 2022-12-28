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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//test


public class MainActivity extends AppCompatActivity {

    /********Set Global Variables Variables********/

    ExchangeRateDatabase db = new ExchangeRateDatabase();

    /********ON CREATE METHODE********/

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /********Set Local Variables Variables********/

        Button btnCalc = findViewById(R.id.btnCalc);

        TextView valIn = findViewById(R.id.ValInput);
        TextView valOut = findViewById(R.id.ValOutput);

        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        Configuration newConfig = new Configuration();

/*
        // OnConfigurationChanged StartUp Check
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.landscape);
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }

 */

        /********MethodReference********/
        onConfigurationChanged(newConfig);
        SpinnerAdapterMethod(spFrom, spTo);
        //SpinnerAdapter(spFrom,spTo);
        btnCalc.setOnClickListener(v -> calculation(valIn, valOut, spFrom, spTo));
    }

    /********OVERRIDE METHODS********/
    //3 Pointer-Menu (Actionbar Menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // forward to Next Activity (Actionbar Menu)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.List:
                Intent i = new Intent(this, Currency_List_Viewer.class);
                startActivity(i);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.landscape);
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }


        Spinner spFrom = findViewById(R.id.spFrom);
        Spinner spTo = findViewById(R.id.spTo);

        SpinnerAdapterMethod(spFrom, spTo);
    }

    /********USABLE METHODS********/

    //Phone Rotation Check


    //Create Spinner Adapter
    public void SpinnerAdapterMethod(Spinner spFrom, Spinner spTo) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, db.getCurrencies());
        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);
    }
    //ExChangeRateAdapter
    public void SpinnerAdapter(Spinner spFrom, Spinner spTo) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, db.getCurrencies());
        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);
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

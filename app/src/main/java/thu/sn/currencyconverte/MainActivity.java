package thu.sn.currencyconverte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
//test


public class MainActivity extends AppCompatActivity {

    ExchangeRateDatabase db = new ExchangeRateDatabase();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.List:
                Intent i = new Intent(this, Currency_List_Viewer.class);
                startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Variables
        TextView valIn = (TextView) findViewById(R.id.ValInput);
        TextView valOut = (TextView) findViewById(R.id.ValOutput);

        Button btnCalc = (Button) findViewById(R.id.btnCalc);

        Spinner spFrom = (Spinner) findViewById(R.id.spFrom);
        Spinner spTo = (Spinner) findViewById(R.id.spTo);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, db.getCurrencies());

        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);

//TODO:: App Crashes
        btnCalc.setOnClickListener(v -> {
            if (TextUtils.isEmpty(valIn.getText().toString())) valOut.setText("0");
            else {
                double conversion = db.convert(Double.parseDouble(valIn.getText().toString()), spFrom.getSelectedItem().toString(), spTo.getSelectedItem().toString());
                conversion = (double) Math.round(conversion * 100) / 100;

                valOut.setText(Double.toString(conversion));
            }
        });


    }

}

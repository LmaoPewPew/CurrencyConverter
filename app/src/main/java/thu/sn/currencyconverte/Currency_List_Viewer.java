package thu.sn.currencyconverte;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class Currency_List_Viewer extends AppCompatActivity {

    ExchangeRateDatabase db = new ExchangeRateDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list_viewer);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Currency View");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //https://www.youtube.com/watch?v=aUFdgLSEl0g

        //FOLLOW SCRIPTS
        //Just do the Flags for the spinner, fuck 2.5
        //CustomBaseAdapter cba = new CustomBaseAdapter(getApplicationContext(), db.getCurrencies(), db.getExchangeRate());

        ListView lv = (ListView) findViewById(R.id.curListView);
        //lv.setAdapter(cba);

        lv.setOnItemClickListener((parent, view, position, id) -> mainAct());
    }

    private void mainAct() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /*
    private final List<ExchangeRate> rate_data;
    public CurrencyEditAdapter(List<ExchangeRate> rate_data) {
        this.rate_data = rate_data;
    }
    ExchangeRate rate = rate_data.get(i);
     */
}
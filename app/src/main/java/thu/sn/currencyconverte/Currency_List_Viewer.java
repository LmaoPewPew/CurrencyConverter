package thu.sn.currencyconverte;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class Currency_List_Viewer extends AppCompatActivity {

    /********Set Global Variables Variables********/

    ExchangeRateDatabase db = new ExchangeRateDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list_viewer);
        actionbarSettings();

        ExchangeRateAdapter exa = new ExchangeRateAdapter(Arrays.asList(db.getCurrencies()));

        listViewer(exa);

    }

    private void actionbarSettings() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Currency View");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void mainAct() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void listViewer(ExchangeRateAdapter exa) {
        ListView lv = (ListView) findViewById(R.id.curListView);
        lv.setAdapter(exa);

        lv.setOnItemClickListener((parent, view, position, id) -> mainAct());
    }

    /*
    private final List<ExchangeRate> rate_data;
    public CurrencyEditAdapter(List<ExchangeRate> rate_data) {
        this.rate_data = rate_data;
    }
    ExchangeRate rate = rate_data.get(i);
     */
}
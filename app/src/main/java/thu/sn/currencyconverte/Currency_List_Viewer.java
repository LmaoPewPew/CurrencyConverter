package thu.sn.currencyconverte;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.Arrays;

public class Currency_List_Viewer extends AppCompatActivity {

    /********Set Global Variables Variables********/

    ExchangeRateDatabase db = new ExchangeRateDatabase();

    /********ONCREATE********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list_viewer);
        actionbarSettings();

        /*******Methods********/
        listViewer(new ExchangeRateAdapter(Arrays.asList(db.getCurrencies())));
    }


    /********Show List View********/


    private void listViewer(ExchangeRateAdapter exa) {
        ListView lv = (ListView) findViewById(R.id.ListView);
        lv.setAdapter(exa);
        lv.setOnItemClickListener((parent, view, position, id) -> openMaps());
    }
    private void openMaps() {
        //open GoogleMaps  with the Capital
    }

    /********Simple Methods********/

    private void actionbarSettings() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Currency View");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


}
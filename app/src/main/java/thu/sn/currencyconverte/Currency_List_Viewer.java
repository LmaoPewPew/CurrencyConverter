package thu.sn.currencyconverte;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

        listViewer(new ExchangeRateAdapter(Arrays.asList(db.getCurrencies())));

    }

    /********ActionbarSettings********/

    private void actionbarSettings() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Currency View");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /********Show List View********/

    private void listViewer(ExchangeRateAdapter exa) {
        ListView lv = findViewById(R.id.ListView);
        lv.setAdapter(exa);

        lv.setOnItemClickListener((parent, view, position, id) -> getLocation(position, lv));
    }


    /********Open Maps********/
    private void getLocation(int pos, ListView lv) {
        String location = (String) lv.getItemAtPosition(pos);
        openMaps(db.getCapital(location));
    }

    private void openMaps(String location) {
        //open GoogleMaps  with the Capital
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" + location));
        startActivity(mapIntent);
    }

}
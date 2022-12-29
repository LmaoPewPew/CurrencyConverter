package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ExchangeRateAdapter extends BaseAdapter {
    List<String> currencyList;
    ExchangeRateDatabase exRaDB = new ExchangeRateDatabase();

    public ExchangeRateAdapter(List<String> currency) {
        this.currencyList = currency;
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Object getItem(int position) {
        return currencyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup vGroup) {
        Context context = vGroup.getContext();
        String currency = exRaDB.getCurrencies()[position];

        double exchangeRate = exRaDB.getExchangeRate(currency);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_list_view, null, false);
        }
        TextView tv = view.findViewById(R.id.currencyListView);
        //TODO: remove position!
        tv.setText(currency + "\t\t\t\t\t" + exchangeRate + " (in €)");
        return view;
    }
}

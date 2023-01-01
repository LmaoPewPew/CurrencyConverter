package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

        String imageNameFile = "flag_" + currency.toLowerCase();
        int imageId = context.getResources().getIdentifier(imageNameFile, "drawable", context.getPackageName());


        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_list_view, null, false);
        }

        ImageView imageView = view.findViewById(R.id.currencyImgView);

        imageView.setImageResource(imageId);

        TextView currencyText = view.findViewById(R.id.currencyListView);
        currencyText.setText(currency);

        TextView moneyText = view.findViewById(R.id.currencyExChangeView);
        final double roundedExchangeRate = /**/Math.floor(exchangeRate * 100) / 100; //*/ exchangeRate;
        moneyText.setText(roundedExchangeRate + "€");

        currencyText.setTextColor(Color.DKGRAY);
        moneyText.setTextColor(Color.DKGRAY);
        return view;
    }
}

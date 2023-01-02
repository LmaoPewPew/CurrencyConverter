package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ExchangeRateAdapter extends BaseAdapter {
    List<String> currencyList;
    ExchangeRateDatabase db = new ExchangeRateDatabase();
    private Context context;

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
        String currency = db.getCurrencies()[position];

        DecimalFormat df = new DecimalFormat("0.0000");
        df.setRoundingMode(RoundingMode.DOWN);

        double exchangeRate = Double.parseDouble(df.format(ExchangeRateDatabase.getExchangeRate(currency)));
        String imageNameFile = "flag_" + currency.toLowerCase();

        @SuppressLint("DiscouragedApi") int imageId = context.getResources().getIdentifier(imageNameFile, "drawable", context.getPackageName());


        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_list_view, null, false);
        }

        ImageView imageView = view.findViewById(R.id.currencyImgView);

        imageView.setImageResource(imageId);

        TextView currencyText = view.findViewById(R.id.currencyListView);
        currencyText.setText(currency);

        TextView moneyText = view.findViewById(R.id.currencyExChangeView);

        moneyText.setText(exchangeRate + "€");

        checkTheme(currencyText);
        checkTheme(moneyText);
        return view;
    }

    public void checkTheme(TextView textView) {
        int nightModeFlags = textView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                textView.setTextColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                textView.setTextColor(Color.BLACK);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                textView.setTextColor(Color.DKGRAY);
                break;
        }
    }

}

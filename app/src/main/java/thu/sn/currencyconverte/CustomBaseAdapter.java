package thu.sn.currencyconverte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {
    Context ctx;
    String[] currencyList;

    double[] exchangeList;
    LayoutInflater inflater;


    public CustomBaseAdapter(Context ctx, String[] currency, double[] ex) {
        this.ctx = ctx;
        this.currencyList = currency;
        this.exchangeList = ex;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return currencyList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.activity_list_view, null);
        TextView curList = (TextView) convertView.findViewById(R.id.currencyListView);
        TextView exList = (TextView) convertView.findViewById(R.id.exRateView);
        curList.setText(currencyList[position]);
        exList.setText((int) exchangeList[position]);

        return convertView;
    }
}

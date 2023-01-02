package thu.sn.currencyconverte;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.net.URLConnection;

public class ExchangeRateUpdateWorker extends Worker {

    public ExchangeRateUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        updateCurrencies();
        Log.d("Worker", "do Work");
        return Result.success();
    }

    private void updateCurrencies() {

        Thread thread = new Thread(() -> {
            String webString = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
            try {
                URL url = new URL(webString);
                URLConnection connection = url.openConnection();
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(connection.getInputStream(), connection.getContentEncoding());
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("Cube")) {
                            String currency = parser.getAttributeValue(null, "currency");
                            String rate = parser.getAttributeValue(null, "rate");
                            if (currency != null && rate != null) {
                                ExchangeRateDatabase.setExchangeRate(currency, Double.parseDouble(rate));
                                Log.d("Currency", currency + " | " + rate);
                            }
                        }
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                Log.e("ErrorURL", "Error with XML: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }
    /*
    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

     */
}

package thu.sn.currencyconverte;

import java.util.Arrays;

public class ExchangeRateDatabaseAccess {

    private static ExchangeRateDatabase db = null;
    private static ExchangeRateAdapter adapter = null;

    public static ExchangeRateDatabase getExchangeRateDatabase() {
        if (db == null) {
            db = new ExchangeRateDatabase();
        }
        return db;
    }

    public static ExchangeRateAdapter getExchangeRateAdapter() {
        if (adapter == null) {
            adapter = new ExchangeRateAdapter(Arrays.asList(ExchangeRateDatabase.getCurrencies()));
        }
        return adapter;
    }
}


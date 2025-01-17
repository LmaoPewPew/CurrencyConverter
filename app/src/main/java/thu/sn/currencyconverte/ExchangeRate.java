package thu.sn.currencyconverte;

public class ExchangeRate {
    private final String currencyName;
    private double rateForOneEuro;
    private final String capital;

    public ExchangeRate(String currencyName, String capital, double rateForOneEuro) {
        this.currencyName = currencyName;
        this.rateForOneEuro = rateForOneEuro;
        this.capital = capital;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCapital() {
        return capital;
    }

    public double getRateForOneEuro() {
        return rateForOneEuro;
    }

    public void setRateForOneEuro(double rateForOneEuro) {
        this.rateForOneEuro = rateForOneEuro;
    }
}

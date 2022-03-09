package hasan.mohamed.shehata.myapplication.models;

public class CountryPhoneCode {
    private String name;
    private String phoneCode;
    private String countryCode;

    public CountryPhoneCode() {
    }

    public CountryPhoneCode(String name, String phoneCode, String countryCode) {
        this.name = name;
        this.phoneCode = phoneCode;
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}

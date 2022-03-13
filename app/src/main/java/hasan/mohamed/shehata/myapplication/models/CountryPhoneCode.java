package hasan.mohamed.shehata.myapplication.models;

import android.view.View;
import android.widget.ImageView;

import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;

public class CountryPhoneCode implements ListItemBindableItemContentProvider {
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

    @Override
    public String getPrimaryText() {
        return name;
    }

    @Override
    public String getSecondaryText() {
        return countryCode;
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public void drawLogo(ImageView view) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

    }

    @Override
    public void disposeResources() {

    }

    @Override
    public boolean isEqualTo(ListItemBindableItemContentProvider item) {
        return false;
    }
}

package hasan.mohamed.shehata.myapplication.models;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.Bindable;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.HashMap;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;

public enum Language implements Serializable , ListItemBindableItemContentProvider {
    Arabic("ar") ,
    Danish("da") ,
    German("de") ,
    English("en") ,
    Spanish("es") ,
    Finnish("fi") ,
    French("fr") ,
    Italian("it") ,
    Japanese("ja") ,
    Korean("ko") ,
    Polish("pl") ,
    Portuguese("pt") ,
    Russian("ru") ,
    Swedish("sv") ,
    Thai("th") ,
    Turkish("tr") ,
    Chinese("zh") ,
    Malay("ms") ,
    Norwegian("no") ,
    Vietnamese("vi") ,
    Indonesian("id") ,
    Czech("cs") ,
    Hebrew("he") ,
    Greek("el") ,
    Hindi("hi") ,
    Tagalog("tl") ,
    Serbian("sr") ,
    Romanian("ro") ,
    Traditional_Chinese("zh-HK") ,
    Tamil("ta") ,
    Hungarian("hu") ,
    Dutch("nl") ,
    Persian("fa") ,
    Slovak("sk") ,
    Estonian("et") ,
    Latvian("lv") ,
    Central_Khmer("km");

    //GTRNS Only
    /*
    Afrikaans("af"),
    Albanian("sq"),
    Amharic("am"),
    Armenian("hy"),
    Azerbaijani("az"),
    Basque("be"),
    Bengali("bn"),
    Bosnian("bs"),
    Bulgarian("bg"),
    Catalan("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
    Afrikaans("af"),
     */

    public final String symbol;

    private Language(String symbol) {
        this.symbol = symbol;
    }

    public String getLanguageName(){
        return this.name().replace("_", " ");
    }

    @Override
    public String getPrimaryText() {
        return getLanguageName();
    }

    @Override
    public String getSecondaryText() {
        return symbol;
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public void setIsGroupAdmin(boolean isGroupAdmin) {

    }

    public int getIsAdminCheckBoxVisibility() {
        return View.GONE;
    }

    @Override
    public boolean getIsGroupAdmin() {
        return false;
    }


    @Override
    public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

    }

    @Override
    public void disposeResources() {

    }

    public String getSymbol(){
        return symbol;
    }

    @Override
    public boolean isEqualTo(ListItemBindableItemContentProvider item) {
        return name().equals(((Language)item).getLanguageName());
    }










    private static HashMap<Language, Integer> logos = new HashMap<Language,Integer>(){{
        put(Language.Arabic, R.drawable.arabic);
        put(Language.Danish,R.drawable.unitednations);
        put(Language.German,R.drawable.germany);
        put(Language.English,R.drawable.unitedstates);
        put(Language.Spanish,R.drawable.spain);
        put(Language.Finnish,R.drawable.unitednations);
        put(Language.French,R.drawable.france);
        put(Language.Italian,R.drawable.italy);
        put(Language.Japanese,R.drawable.japan);
        put(Language.Korean,R.drawable.korea);

        put(Language.Polish,R.drawable.poland);
        put(Language.Portuguese,R.drawable.portugal);
        put(Language.Russian,R.drawable.russia);
        put(Language.Swedish,R.drawable.unitednations);
        put(Language.Thai,R.drawable.thailand);
        put(Language.Turkish,R.drawable.turkey);
        put(Language.Chinese,R.drawable.china);
        put(Language.Malay,R.drawable.malaysia);
        put(Language.Norwegian,R.drawable.unitednations);
        put(Language.Vietnamese,R.drawable.unitednations);
        put(Language.Indonesian,R.drawable.indonesia);

        put(Language.Czech,R.drawable.indonesia);
        put(Language.Hebrew,R.drawable.unitednations);
        put(Language.Greek,R.drawable.unitednations);
        put(Language.Hindi,R.drawable.india);
        put(Language.Tagalog,R.drawable.unitednations);
        put(Language.Serbian,R.drawable.serbia);
        put(Language.Romanian,R.drawable.romania);
        put(Language.Traditional_Chinese,R.drawable.china);
        put(Language.Tamil,R.drawable.unitednations);
        put(Language.Hungarian,R.drawable.hungary);
        put(Language.Dutch,R.drawable.netherlands);

        put(Language.Persian,R.drawable.unitednations);
        put(Language.Slovak,R.drawable.unitednations);
        put(Language.Estonian,R.drawable.estonia);
        put(Language.Latvian,R.drawable.latvia);
        put(Language.Central_Khmer,R.drawable.unitednations);
    }};

    @Override
    public void drawLogo(ImageView view) {
        Glide
                .with(view.getContext())
                .load(logos.get(this))
                .into(view);
    }

    private boolean isHighLighted;
    @Override
    public boolean getIsHighLighted() {
        return isHighLighted;
    }

    @Override
    public void setIsHighLighted(boolean isHighLighted) {
        this.isHighLighted=isHighLighted;
    }
    @Override
    public void toggleHighLight() {
        isHighLighted = !isHighLighted;
    }

//    @Bindable
    public int getHighlightedFilterVisibility(){
        if(isHighLighted)
            return View.VISIBLE;
        else
            return View.GONE;
    }
}

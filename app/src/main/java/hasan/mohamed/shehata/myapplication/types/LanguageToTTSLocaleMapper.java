package hasan.mohamed.shehata.myapplication.types;

import java.util.Locale;

import hasan.mohamed.shehata.myapplication.models.Language;

public class LanguageToTTSLocaleMapper {
    public static Locale map(Language language){
        switch(language){
            case Arabic:{
                return Locale.forLanguageTag(Language.Arabic.symbol);
            }
            case English:{
                return  Locale.ENGLISH;
            }
            case French:{
                return  Locale.FRENCH;
            }
            case German:{
                return  Locale.GERMANY;
            }
            case Italian:{
                return  Locale.ITALIAN;
            }
            case Chinese:{
                return  Locale.CHINESE;
            }
            case Japanese:{
                return  Locale.JAPANESE;
            }
            case Korean:{
                return  Locale.KOREAN;
            }
            default:{
                return  Locale.US;
            }
        }
    }
}

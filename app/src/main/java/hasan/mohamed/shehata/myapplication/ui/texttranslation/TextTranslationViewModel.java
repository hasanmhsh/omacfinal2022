package hasan.mohamed.shehata.myapplication.ui.texttranslation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hasan.mohamed.shehata.myapplication.models.Language;

public class TextTranslationViewModel extends ViewModel {
    private TextTranslationViewModel textTranslationViewModel;
    private MutableLiveData<String> mText;
    private MutableLiveData<Language> fromLanguage;
    private MutableLiveData<Language> toLanguage;

    public TextTranslationViewModel() {
        mText = new MutableLiveData<>();
        fromLanguage = new MutableLiveData<>();
        toLanguage = new MutableLiveData<>();
        fromLanguage.setValue(null);
        toLanguage.setValue(null);
        mText.setValue("This is text translation fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Language> getFromLanguage() {
        return fromLanguage;
    }

    public LiveData<Language> getToLanguage() {
        return toLanguage;
    }

    public void setFromLanguage(Language language){
        this.fromLanguage.setValue(language);
    }

    public void setToLanguage(Language language){
        this.toLanguage.setValue(language);
    }
}
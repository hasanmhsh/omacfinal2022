package hasan.mohamed.shehata.myapplication.ui.translationfragment;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.templates.GeneralRecyclerViewAdapter;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.TranslatorCapabilities;
import hasan.mohamed.shehata.myapplication.views.TranslationItemView;

public class TranslationViewModel extends ViewModel {
    private TranslationViewModel translationViewModel;
    private MutableLiveData<List<ListItemBindableItemContentProvider>> lngList;
    public TranslationViewModel() {
        lngList = new MutableLiveData<>();
        lngList.setValue(null);
    }


    public MutableLiveData<List<ListItemBindableItemContentProvider>> getLngList() {
        return lngList;
    }

    public void setLngList(MutableLiveData<List<ListItemBindableItemContentProvider>> lngList) {
        this.lngList = lngList;
    }
}
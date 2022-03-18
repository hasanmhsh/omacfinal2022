package hasan.mohamed.shehata.myapplication.models;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.Bindable;

import java.util.List;

import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;
import hasan.mohamed.shehata.myapplication.types.ListItemContentProviderComparer;
import hasan.mohamed.shehata.myapplication.types.TranslationItemType;
import hasan.mohamed.shehata.myapplication.types.TranslatorCapabilities;

public interface ListItemBindableItemContentProvider extends ListItemContentProviderComparer {
    public String getPrimaryText();
    public String getSecondaryText();
    public long getID();
    public void setIsGroupAdmin(boolean isGroupAdmin);
    public boolean getIsGroupAdmin();
    public int getIsAdminCheckBoxVisibility();
    public void drawLogo(ImageView view);
    public boolean getIsHighLighted();
    public int getHighlightedFilterVisibility();
    public void setIsHighLighted(boolean isHighLighted);
    public void setOnListItemCallbacks(ListItemCallbacks callbacks);
    public void disposeResources();
    public static void getNewItem(Context context, List<ListItemBindableItemContentProvider> dataSet, TranslatorCapabilities capabilities, ListItemCallbacks listItemCallbacks,Language targetLanguage) {
        if(dataSet.get(0) instanceof TranslationItem){
            if(dataSet == null || dataSet.size() < 1 || dataSet.get(0)==null || ((TranslationItem) dataSet.get(0)).getSourceLanguage()==null){
//                throw new Exception("Dataset is empty");
                return;
            }
            new TranslationItem(context ,((TranslationItem)dataSet.get(0)).getSourceLanguage(),  ((TranslationItem)dataSet.get(0)), targetLanguage,TranslationItemType.Target, capabilities, listItemCallbacks);
        }
    }

    void toggleHighLight();
}

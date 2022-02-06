package hasan.mohamed.shehata.myapplication.types;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;

public interface ListItemCallbacks {
    public void delete(ListItemBindableItemContentProvider provider);
    public void add(ListItemBindableItemContentProvider item);
    public void setNewDataSet(List<ListItemBindableItemContentProvider> newDataSet);
    public void refreshDataSet();
    public void updateTranslationDataSet(String text);
    public void sourceLanguageSelected(Language sourceLanguage);
}

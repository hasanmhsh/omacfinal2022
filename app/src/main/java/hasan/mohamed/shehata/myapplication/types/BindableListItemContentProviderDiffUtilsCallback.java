package hasan.mohamed.shehata.myapplication.types;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;

public class BindableListItemContentProviderDiffUtilsCallback extends DiffUtil.Callback{

    List<ListItemBindableItemContentProvider> oldList;
    List<ListItemBindableItemContentProvider> newList;

    public BindableListItemContentProviderDiffUtilsCallback(List<ListItemBindableItemContentProvider> newList, List<ListItemBindableItemContentProvider> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        if(oldList == null)
            return 0;
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        if(newList == null)
            return 0;
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).isEqualTo(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
package hasan.mohamed.shehata.myapplication.types;

import java.io.Serializable;

import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;

public interface ListItemContentProviderComparer extends Serializable {
  public boolean isEqualTo(ListItemBindableItemContentProvider item);
}

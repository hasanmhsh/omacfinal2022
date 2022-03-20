package hasan.mohamed.shehata.myapplication.types;

import java.io.Serializable;
import java.util.List;

import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.TranslationItem;
import hasan.mohamed.shehata.myapplication.models.User;

public abstract class ResultReceiver implements Serializable {
    public abstract void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider);
    public abstract void receiveMultipleChoices(List<ListItemBindableItemContentProvider> list);
    public abstract void deleteItem(ListItemBindableItemContentProvider item);
    public abstract User getBuddy();
    public abstract Group getGroup();
    public abstract SpeakerProvider provideSpeaker();
    public abstract boolean isReadOnly();
}

package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.TranslationItem;
import hasan.mohamed.shehata.myapplication.models.User;

public abstract class ResultReceiver  {
    public abstract void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider);
    public abstract void deleteItem(ListItemBindableItemContentProvider item);
    public abstract User getBuddy();
    public abstract SpeakerProvider provideSpeaker();
}

package hasan.mohamed.shehata.myapplication.models;

public interface BindableItem {
    public void bind(ListItemBindableItemContentProvider bindableItemContentProvider);
    public void bind(DownloadWindowContent downloadWindowContent);
    public void bind(User user);
    public void bind(Group group);
    public void bind(Message msg);
    public void close();
}

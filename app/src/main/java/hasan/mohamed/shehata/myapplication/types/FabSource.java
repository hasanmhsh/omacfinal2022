package hasan.mohamed.shehata.myapplication.types;

public interface FabSource {
    public void setFabAction(Runnable runnable);
    public void setFabActionType(FabActionType type);
    public void refreshFab();
    public void disableFab();
}

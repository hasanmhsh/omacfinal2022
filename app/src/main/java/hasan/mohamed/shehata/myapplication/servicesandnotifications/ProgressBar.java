package hasan.mohamed.shehata.myapplication.servicesandnotifications;

public interface ProgressBar {
    public void setProgress(long downLength, long totalLength);
    public void fill();
    public void close();
}

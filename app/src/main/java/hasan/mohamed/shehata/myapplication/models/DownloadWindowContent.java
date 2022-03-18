package hasan.mohamed.shehata.myapplication.models;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import hasan.mohamed.shehata.myapplication.Utils;

public class DownloadWindowContent extends BaseObservable {
    private String fileName;
    private long downloadedSize;
    private long totalSize;
    private Context context;

    public DownloadWindowContent(String fileName, Context context) {
        this.fileName = fileName;
        this.context = context;
    }

    @Bindable
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Bindable
    public String getDownloadedSizeStrMB() {
        double d = downloadedSize;
        d /= 1024.0D * 1024D;
        return String.valueOf(String.format("%.1f", d));
    }

    @Bindable
    public String getTotalSizeStrMB() {
        double d = totalSize;
        d /= 1024.0D * 1024D;
        return String.valueOf(String.format("%.1f", d));
    }

    @Bindable
    public long getDownloadedSize() {
        return downloadedSize;
    }

    @Bindable
    public long getTotalSize() {
        return totalSize;
    }

    public void setProgress(long downloadedSize, long totalSize) {
        this.downloadedSize = downloadedSize;
        this.totalSize = totalSize;
    }

    @Bindable
    public int getProgressPercent(){
        return Utils.getDownloadedPercent(downloadedSize , totalSize);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Bindable
    public String getDownloadSpeedKBPS(){
        return String.valueOf(Utils.getDownLinkSpeedKBps(context));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String getDownloadSpeedMBPS(){
        double d = Utils.getDownLinkSpeedKBps(context);
        d /= 1000.0D;
        return String.valueOf(String.format("%.2f", d));
    }
}

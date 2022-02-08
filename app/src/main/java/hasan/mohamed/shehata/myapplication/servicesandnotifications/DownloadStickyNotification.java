package hasan.mohamed.shehata.myapplication.servicesandnotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;

public class DownloadStickyNotification implements ProgressBar {
    private static final String CHANNEL_ID = "hasan.mohamed.shehata.myapplication" + ".";
    private static final int DOWNLOAD_NOTIFICATION_PENDING_INTENT_ID = 233845;
    private static int instances_count = 0;

    public static ProgressBar make(Context context, Canceler cancelHandler, String fileName){
        instances_count++;
        DownloadStickyNotification downloadStickyNotification = new DownloadStickyNotification(context, cancelHandler, fileName);
        return downloadStickyNotification;
    }

    private Context context;
    private Canceler cancelHandler;
    private String fileName;
    private NotificationCompat.Builder builder;
    private PendingIntent cancelIntent;
    private NotificationManagerCompat notificationManager;
    private Object object1ForHash = new Object();
    private Object object2ForHash = new Object();
    private Object object3ForHash = new Object();
    private int runningHashCode;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.channel_name);
            String description = context.getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private DownloadStickyNotification(Context context, Canceler cancelHandler, String fileName) {
        this.context = context;
        this.cancelHandler = cancelHandler;
        this.fileName = fileName;
        initialize();
    }

    private void initialize() {
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(fileName + context.getResources().getString(R.string.model_download))
                .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
                .setSubText(context.getResources().getString(R.string.download_in_progress))
                .setSettingsText("jkdbjhvkd")
                .setSmallIcon(R.drawable.ic_baseline_language_24)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(getIntentToSendFromNotificationToComponent(context))
                .setOngoing(true);
//                .addAction(R.drawable.ic_baseline_cloud_download_24, "cancel", getIntentToCancelNotification(context,hashCode()));
        builder.setAutoCancel(false);


// Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        runningHashCode = hashCode();
        notificationManager.notify(runningHashCode, builder.build());

// Do the job here that tracks the progress.
// Usually, this should be in a 
// worker thread 
// To show progress, update PROGRESS_CURRENT and update the notification with:
// builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
// notificationManager.notify(notificationId, builder.build());

// When done, update the notification one more time to remove the progress bar
//        builder.setContentText("Download complete")
//                .setProgress(0,0,false);
//        notificationManager.notify(hashCode(), builder.build());

    }

    private static PendingIntent getIntentToSendFromNotificationToComponent(Context context){
        // Will start activity if not started
        // Will trigger onIntent if activity is started
        Intent intent = new Intent(context, TranslationMainActivity.class);
        return PendingIntent.getActivity(
                context,
                DOWNLOAD_NOTIFICATION_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static PendingIntent getIntentToCancelNotification(Context context , int hashcode){
        // Will start activity if not started
        // Will trigger onIntent if activity is started
        Intent intent = new Intent(context, MyModelDownloadIntentService.class);
        intent.setAction(MyModelDownloadIntentService.ACTION_NOTIFICATION_CANCEL_PRESSED);
        intent.putExtra(MyModelDownloadIntentService.NOTIFICATION_CODE_PARAM, hashcode);
        return PendingIntent.getService(
                context,
                DOWNLOAD_NOTIFICATION_PENDING_INTENT_ID+hashcode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
    }

    @Override
    public void setProgress(long downLength, long totalLength){
//        builder.setContentText("Download complete");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setProgress(100, Utils.getDownloadedPercent(downLength, totalLength),false);
        notificationManager.notify(hashCode(), builder.build());
    }


    @Override
    public void fill() {

    }

    @Override
    public void close() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(runningHashCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadStickyNotification that = (DownloadStickyNotification) o;
        return Objects.equals(context, that.context) && Objects.equals(cancelHandler, that.cancelHandler) && Objects.equals(fileName, that.fileName) && Objects.equals(builder, that.builder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object1ForHash, object2ForHash, object3ForHash) + (instances_count * 31);
    }
}

package hasan.mohamed.shehata.myapplication.servicesandnotifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

import hasan.mohamed.shehata.myapplication.models.Language;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyModelDownloadIntentService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "hasan.mohamed.shehata.myapplication.languages.action.FOO";
    private static final String ACTION_BAZ = "hasan.mohamed.shehata.myapplication.languages.action.BAZ";
    private static final String ACTION_TRANSLATION_MODEL_DOWNLOAD = "hasan.mohamed.shehata.myapplication.languages.action.TanslationModel";
    public static final String ACTION_NOTIFICATION_CANCEL_PRESSED = "hasan.mohamed.shehata.myapplication.languages.action.ACTION_NOTIFICATION_CANCEL_PRESSED";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "hasan.mohamed.shehata.myapplication.languages.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "hasan.mohamed.shehata.myapplication.languages.extra.PARAM2";
    private static final String SOURCE_LANGUAGE_EXTRA_PARAM = "hasan.mohamed.shehata.myapplication.languages.extra.TranslationModelDownloadSourceLanguage";
    private static final String TARGET_LANGUAGE_EXTRA_PARAM = "hasan.mohamed.shehata.myapplication.languages.extra.TranslationModelDownloadTargetLanguage";
    public static final String NOTIFICATION_CODE_PARAM = "hasan.mohamed.shehata.myapplication.notification.extra.NOTIFICATION_CODE_PARAM";



    public MyModelDownloadIntentService() {
        super("MyModelDownloadIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyModelDownloadIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyModelDownloadIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            } else if (ACTION_TRANSLATION_MODEL_DOWNLOAD.equals(action)) {
                final Language sorceLanguage = (Language) intent.getSerializableExtra(SOURCE_LANGUAGE_EXTRA_PARAM);
                final Language targetLanguage = (Language) intent.getSerializableExtra(TARGET_LANGUAGE_EXTRA_PARAM);
                handleActionDownloadTranslationModel(sorceLanguage, targetLanguage);
            } else if (ACTION_NOTIFICATION_CANCEL_PRESSED.equals(action)) {
                final int notificationHashCode = intent.getIntExtra(NOTIFICATION_CODE_PARAM,0);
                handleActionCancelNotification(notificationHashCode);
            }


        }
    }

    private void handleActionCancelNotification(int notificationHashCode) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(notificationHashCode);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownloadTranslationModel(Language sorceLanguage, Language targetLanguage) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
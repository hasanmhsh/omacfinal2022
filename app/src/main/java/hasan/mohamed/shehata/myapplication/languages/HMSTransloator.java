package hasan.mohamed.shehata.myapplication.languages;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadListener;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslateSetting;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslator;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslatorModel;

import java.io.Closeable;
import java.io.IOException;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.TranslationItem;
import hasan.mohamed.shehata.myapplication.servicesandnotifications.Canceler;
import hasan.mohamed.shehata.myapplication.servicesandnotifications.DownloadStickyNotification;
import hasan.mohamed.shehata.myapplication.servicesandnotifications.ProgressBar;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.types.DownloadCallbacks;
import hasan.mohamed.shehata.myapplication.types.NewMessagesConsumer;

public class HMSTransloator {
    private Language sourceLanguage;
    private Language targetLanguage;
    private Context context;
    private MLLocalTranslator mlLocalTranslator;
    private BindableItem downloadWindow;
    private DownloadWindowContent downloadWindowContent;
    private TextView sourceTextV;
    private TextView targetTextV;
    private TranslationItem translationItem;
    private String initialText;
    private Closeable progressWindow;
    private DownloadCallbacks downloadCallbacks;
    private boolean isRefreshFab;
    private ProgressBar notificationProgress;

    public HMSTransloator(Context context, Language sourceLanguage, Language targetLanguage, TextView sourceTextV , TextView targetTextV , TranslationItem item , String initialText, DownloadCallbacks downloadCallbacks, boolean isRefreshFab) {
        this.context = context;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.sourceTextV = sourceTextV;
        this.targetTextV = targetTextV;
        this.translationItem = item;
        this.initialText = initialText;
        this.downloadCallbacks = downloadCallbacks;
        autoDownloadTranslationModel();
        this.isRefreshFab = isRefreshFab;

    }

    private void autoDownloadTranslationModel(){
        // Create an offline translator.
        if(Utils.getIsToUseCloudTranslation())
            return;

        if(progressWindow == null)
            progressWindow = GeneralPopupWindow.makeProgressWindow(context,"",isRefreshFab);

        if(notificationProgress == null)
            notificationProgress = DownloadStickyNotification.make(context, new Canceler() {
                @Override
                public void onDownloadCanceled() {

                }
            },sourceLanguage.getLanguageName() + " to " + targetLanguage.getLanguageName());

        MLLocalTranslateSetting setting = new MLLocalTranslateSetting.Factory()
                // Set the source language code, which complies with the ISO 639-1 standard. This parameter is mandatory. If this parameter is not set, an error may occur.
                .setSourceLangCode(sourceLanguage.symbol)
                // Set the target language code, which complies with the ISO 639-1 standard. This parameter is mandatory. If this parameter is not set, an error may occur.
                .setTargetLangCode(targetLanguage.symbol)
                .create();
        mlLocalTranslator = MLTranslatorFactory.getInstance().getLocalTranslator(setting);



//        // Query the languages supported by on-device translation.
//
//        // Sample code for calling the asynchronous method:
//
//        MLTranslateLanguage.getLocalAllLanguages().addOnSuccessListener(
//                new OnSuccessListener<Set<String>>() {
//                    @Override
//                    public void onSuccess(Set<String> result) {
//                        // Languages supported by on-device translation are successfully obtained.
//                    }
//                });
//        //      ^^^^^^^^^^^
//        //           OR
//        //      VVVVVVVVVVV
//
//        // Sample code for calling the synchronous method:
//
//        Set<String> result = MLTranslateLanguage.syncGetLocalAllLanguages();

        // Set the model download policy.
        MLModelDownloadStrategy downloadStrategy = new MLModelDownloadStrategy.Factory()
//                .needWifi() // It is recommended that you download the package in a Wi-Fi environment.
                .create();
        // Create a download progress listener.
        MLModelDownloadListener modelDownloadListener = new MLModelDownloadListener() {
            @Override
            public void onProcess(final long alreadyDownLength, final long totalLength) {
                if (downloadWindowContent == null) {
                    downloadWindowContent = new DownloadWindowContent(sourceLanguage.getLanguageName() + " to " + targetLanguage.getLanguageName() +" ", context);
                }
                if (downloadWindow == null) {
                    downloadWindow = GeneralPopupWindow.makeDownloadWindow(context, context.getResources().getString(R.string.download), isRefreshFab);
                }
                if (downloadWindowContent != null) {
                    downloadWindowContent.setProgress(alreadyDownLength, totalLength);
                    if (downloadWindow != null)
                        downloadWindow.bind(downloadWindowContent);
                }
                if(notificationProgress != null)
                    notificationProgress.setProgress(alreadyDownLength, totalLength);
            }
        };
        Task<Void> task = mlLocalTranslator.preparedModel(downloadStrategy, modelDownloadListener).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess (Void aVoid){
                        // Called when the model package is successfully downloaded.
                        closeProgress();
                        if(downloadWindow != null)
                            downloadWindow.close();
//                        Toast.makeText(context,"Model downloaded successfully",Toast.LENGTH_LONG).show();
                        if(sourceTextV != null && targetTextV != null) {
                            translateAsyncThenDisplayTheResult(sourceTextV, targetTextV);
                        }
                        if(translationItem != null){
                            translateAsync(translationItem, initialText);
                        }

                        if(downloadCallbacks != null)
                            downloadCallbacks.downloadCompleted();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure (Exception e){
                // Called when the model package fails to be downloaded.
                closeProgress();
                if(downloadWindow != null)
                    downloadWindow.close();
//                Toast.makeText(context,"Model download failed",Toast.LENGTH_LONG).show();
            }
        });






    }

    private void closeProgress(){
        if(progressWindow != null) {
            try {
                progressWindow.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeNotification();
    }

    private void manualDownloadOfflineTranslationModel(){
        /*
        4.Download the offline model required for local offline translation in either of the following ways:
         1. Use MLLocalModelManager to manually download the required model package;
          2. Use preparedModel to automatically download the required model package.
        You can select a mode as required.
         */
        // Method 1:
// After the download is successful, translate text in the onSuccess callback.
// Obtain the model manager.
        MLLocalModelManager manager = MLLocalModelManager.getInstance();
        MLLocalTranslatorModel model = new MLLocalTranslatorModel.Factory(sourceLanguage.symbol).create();
// Set the model download policy.
        MLModelDownloadStrategy downloadStrategy1 = new MLModelDownloadStrategy.Factory()
                .needWifi() // It is recommended that you download the package in a Wi-Fi environment.
                .create();
// Create a download progress listener.
        MLModelDownloadListener modelDownloadListener1 = new MLModelDownloadListener() {
            @Override
            public void onProcess(long alreadyDownLength, long totalLength) {
                //run on ui any view update
            }
        };
// Download the model. After the model is downloaded, translate text in the onSuccess callback.
        manager.downloadModel(model, downloadStrategy1, modelDownloadListener1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Called when the model package is successfully downloaded.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Called when the model package fails to be downloaded.
            }
        });
//// Method 2:
//// Set the model download policy.
//        MLModelDownloadStrategy downloadStrategy2 = new MLModelDownloadStrategy.Factory()
//                .needWifi() // It is recommended that you download the package in a Wi-Fi environment.
//                .create();
//// Create a download progress listener.
//        MLModelDownloadListener modelDownloadListener2 = new MLModelDownloadListener() {
//            @Override
//            public void onProcess(long alreadyDownLength, long totalLength) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Display the download progress or perform other operations.
//                    }
//                });
//            }
//        };
//        mlLocalTranslator.preparedModel(downloadStrategy2, modelDownloadListener2).
//                addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess (Void aVoid){
//                        // Called when the model package is successfully downloaded.
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure (Exception e){
//                // Called when the model package fails to be downloaded.
//            }
//        });
    }

    public void release(){
        if(mlLocalTranslator != null) {
            mlLocalTranslator.stop();
            mlLocalTranslator = null;
        }
        closeNotification();
    }

    private void closeNotification(){
        try{
            if(notificationProgress != null){
                notificationProgress.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void translateAsync(final TranslationItem item , final String sourceText){
        if(Utils.getIsToUseCloudTranslation())
            return;
        if(mlLocalTranslator == null)
            autoDownloadTranslationModel();
        mlLocalTranslator.asyncTranslate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        item.setText(s);
                    }
                });
            }
        });
    }

    public void translateMessageAsync(final Message msg, NewMessagesConsumer consumer){
        if(Utils.getIsToUseCloudTranslation())
            return;
        if(mlLocalTranslator == null)
            autoDownloadTranslationModel();
        mlLocalTranslator.asyncTranslate(msg.getMessagetext()).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setMessagetranslatedtext(s);
                        consumer.sendAndSaveThisMessage(msg);
                    }
                });
            }
        });
    }

    private void translateAsyncThenDisplayTheResult(final TextView source, final TextView target){
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mlLocalTranslator.asyncTranslate(source.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        target.post(new Runnable() {
                            @Override
                            public void run() {
                                target.setText(s);
                            }
                        });
                    }
                });
            }
        });
    }

}

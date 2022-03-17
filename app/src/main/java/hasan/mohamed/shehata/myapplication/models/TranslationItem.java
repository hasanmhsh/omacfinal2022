package hasan.mohamed.shehata.myapplication.models;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Ignore;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hasan.mohamed.shehata.myapplication.BR;
import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.languages.ASR_Enhanced;
import hasan.mohamed.shehata.myapplication.languages.HMSTransloator;
import hasan.mohamed.shehata.myapplication.languages.TTS;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.types.ActionResultCallback;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestProvider;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;
import hasan.mohamed.shehata.myapplication.types.TextReceiver;
import hasan.mohamed.shehata.myapplication.types.TranslationItemType;
import hasan.mohamed.shehata.myapplication.types.TranslatorCapabilities;
import hasan.mohamed.shehata.myapplication.types.UpdatableItem;

public class TranslationItem extends BaseObservable implements ListItemBindableItemContentProvider, UpdatableItem , TextReceiver {
    private Context context;
    private Language sourceLanguage;
    private Language targetLanguage;
    private TranslationItemType translationItemType;
    private TranslatorCapabilities translatorCapabilities;
    private String text = "";
    private HMSTransloator hmsTransloator;
    private ListItemCallbacks listItemCallbacks;
    private TranslationItem sourceItem;
    private TranslationItem thiz = this;
    private TTS tts;
    private Closeable progressWindow;
    private ASR_Enhanced asrEnhanced;
    private void openProgressWindow(String title){
        closeProgressWindow();
        progressWindow = GeneralPopupWindow.makeProgressWindow(context, title,true);
    }
    private void closeProgressWindow(){
        if(progressWindow != null){
            try{
                progressWindow.close();
            }
            catch(Exception e){
            }
        }
    }


    // Hypothis is the text which is recognized from speech



    private ResultReceiver newelyCreatedResultReceiver = new ResultReceiver() {
        @Override
        public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
            ((FabSource)context).refreshFab();
            if(bindableItemContentProvider != null) {
                targetLanguage = (Language)bindableItemContentProvider;
                makeTTS();
                makeASR();
                listItemCallbacks.add(thiz);
                listItemCallbacks.refreshDataSet();
                initTranslation();
            }
        }

        @Override
        public void receiveMultipleChoices(List<ListItemBindableItemContentProvider> list) {

        }

        @Override
        public void deleteItem(ListItemBindableItemContentProvider item) {

        }

        @Override
        public User getBuddy() {
            return null;
        }

        @Override
        public Group getGroup() {
            return null;
        }

        @Override
        public SpeakerProvider provideSpeaker() {
            return null;
        }
    };

    private void makeASR() {
        asrEnhanced = new ASR_Enhanced((Activity)context,
                (PermissionRequestProvider)context,
                sourceLanguage,
                null,
        R.drawable.ic_baseline_mic_24,
        R.drawable.ic_baseline_record_voice_over_24,
        null,
        null);
    }

    public void bindASRViews(ImageButton recordZButton, TextView resultTextView){
        if(asrEnhanced != null){
            asrEnhanced.changeRecordButton(recordZButton);
            asrEnhanced.changeRecognizedTextConsumer(resultTextView);
        }
    }

    private ResultReceiver changeSourceLanguageResultReceiver = new ResultReceiver() {
        @Override
        public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
            ((FabSource)context).refreshFab();
            if(bindableItemContentProvider != null) {
                disposeResources();
                Language newSourceLanguage = (Language)bindableItemContentProvider;
                listItemCallbacks.sourceLanguageSelected(newSourceLanguage);
                TranslationItem newSource = new TranslationItem(
                        context,
                        newSourceLanguage,
                        null,
                        null,
                        TranslationItemType.Source,
                        translatorCapabilities,

                        listItemCallbacks
                );
                List<ListItemBindableItemContentProvider> newDataSet = new ArrayList<ListItemBindableItemContentProvider>();
                newDataSet.add(newSource);
                listItemCallbacks.setNewDataSet(newDataSet);
            }
        }

        @Override
        public void receiveMultipleChoices(List<ListItemBindableItemContentProvider> list) {

        }

        @Override
        public void deleteItem(ListItemBindableItemContentProvider item) {

        }

        @Override
        public User getBuddy() {
            return null;
        }

        @Override
        public Group getGroup() {
            return null;
        }

        @Override
        public SpeakerProvider provideSpeaker() {
            return null;
        }
    };


    public TranslationItem(Context context, Language sourceLanguage, TranslationItem sourceItem, Language targetLanguage , TranslationItemType translationItemType, TranslatorCapabilities translatorCapabilities, ListItemCallbacks listItemCallbacks){
        this.context = context;
        this.sourceLanguage = sourceLanguage;
        this.translationItemType = translationItemType;
        this.translatorCapabilities = translatorCapabilities;
        this.sourceItem = sourceItem;
        this.listItemCallbacks = listItemCallbacks;
        switch(translationItemType){
            case Source:{
                if(sourceLanguage != null) {
                    makeTTS();
                    makeASR();
                }
                break;
            }
            case Target:{
                if(targetLanguage == null)
                    GeneralPopupWindow.makeSelectionWindow(context, context.getResources().getString(R.string.select_language), Arrays.asList(Language.values()), newelyCreatedResultReceiver , true,false);
                else
                    newelyCreatedResultReceiver.receiveResult(targetLanguage);
                break;
            }
        }

    }

    public void say(){
        if(tts != null)
            tts.speak(text);
    }

    public void changeSourceLanguage(){// Ok
        if(translationItemType == TranslationItemType.Source){
            GeneralPopupWindow.makeSelectionWindow(context, context.getResources().getString(R.string.select_language), Arrays.asList(Language.values()), changeSourceLanguageResultReceiver, true , false);
        }
    }

    private void initTranslation() {
        if(translationItemType == TranslationItemType.Target){
            hmsTransloator = new HMSTransloator(context, sourceLanguage, targetLanguage, null, null, this, sourceItem.getText(),null,true);
        }
    }
    public Language getTargetLanguage() {
        return targetLanguage;
    }

    private void translate(String string){ //Ok
        // DONE Translate string here if type is target
        if(translationItemType == TranslationItemType.Target){
            hmsTransloator.translateAsync(this, string);
        }
    }

    private void makeTTS(){
        if(tts != null)
            tts.release();
        tts = new TTS(context, getLanguage());
    }


    public void deleteItem(){//ok
        listItemCallbacks.delete(this);
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String value) {
        // Avoids infinite loops.
        if(this.text == null){
            if(value != null){
                this.text = value;
                if(translationItemType == TranslationItemType.Source){
                    listItemCallbacks.updateTranslationDataSet(text);
                }
                notifyPropertyChanged(BR.text);
            }
        }
        else if (!text.equals(value)) {
            text = value;
            if(translationItemType == TranslationItemType.Source){
                listItemCallbacks.updateTranslationDataSet(text);
            }

            // React to the change.
//            saveData();

            // Notify observers of a new value.
            notifyPropertyChanged(BR.text);
        }


    }

    @Bindable
    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    @Bindable
    public Language getLanguage() {
        switch(translationItemType){
            case Source:{
                return sourceLanguage;
            }
            case Target:
            default:{
                return targetLanguage;
            }
        }
    }

    @Bindable
    public TranslationItemType getTranslationItemType() {
        return translationItemType;
    }

    @Bindable
    public TranslatorCapabilities getTranslatorCapabilities() {
        return translatorCapabilities;
    }

    @Bindable
    public String getLanguageTitle() {
        switch(translationItemType){
            case Source:{
                return context.getResources().getString(R.string.translate_from);
            }
            case Target:
            default:{
                return context.getResources().getString(R.string.translate_to);
            }
        }
    }

    @Bindable
    public boolean getIsEditable(){
        switch(translationItemType){
            case Source:{
                return true;
            }
            case Target:
            default:{
                return false;
            }
        }
    }

    @Bindable
    public int getCloseButtonVisibility(){
        switch(translationItemType){
            case Source:{
                return View.GONE;
            }
            case Target:
            default:{
                return View.VISIBLE;
            }
        }
    }

    @Bindable
    public int getSelectLanguageButtonVisibility(){
        switch(translationItemType){
            case Source:{
                return View.VISIBLE;
            }
            case Target:
            default:{
                return View.GONE;
            }
        }
    }

    // TTS :Text to speach
    @Bindable
    public int getTTSButtonVisibility(){
        switch(translatorCapabilities){
            case TextAndTTSAndASR:
            case TextAndTTS:{
                return View.VISIBLE;
            }
            case TextAndASR:
            case Text:
            default:{
                return View.GONE;
            }
        }
    }

    // ASR : Automatic speech recognition
    @Bindable
    public int getASRButtonVisibility(){
        switch(translatorCapabilities){
            case TextAndTTSAndASR:
            case TextAndASR:
            {
                return View.VISIBLE;
            }
            case TextAndTTS:
            case Text:
            default:{
                return View.GONE;
            }
        }
    }

    @Override
    public String getPrimaryText() {
        return null;
    }

    @Override
    public String getSecondaryText() {
        return null;
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public void drawLogo(ImageView view) {

    }

    @Override
    public void setOnListItemCallbacks(ListItemCallbacks callbacks) {
        this.listItemCallbacks = listItemCallbacks;
    }

    @Override
    public void disposeResources() {
        if(hmsTransloator != null)
            hmsTransloator.release();
        if(tts != null)
            tts.release();
        if(asrEnhanced !=null) {
            asrEnhanced.release();
        }
    }
    

    @Override
    public void update(String text) {
        translate(text);
    }

    @Override
    public void receiveText(String text) {
        setText(text);
    }

    @Override
    public boolean isEqualTo(ListItemBindableItemContentProvider item) {
        return false;
    }

    @Ignore
    private transient boolean isHighLighted;
    @Override
    public boolean getIsHighLighted() {
        return isHighLighted;
    }
    @Override
    public void toggleHighLight() {
        isHighLighted = !isHighLighted;
    }
    @Override
    public void setIsHighLighted(boolean isHighLighted) {
        this.isHighLighted=isHighLighted;
    }
}

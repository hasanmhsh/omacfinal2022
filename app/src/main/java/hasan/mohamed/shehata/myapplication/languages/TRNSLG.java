package hasan.mohamed.shehata.myapplication.languages;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.ConnectionError;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.ResultFetchError;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.general.JavaScriptObjectFactory;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.result.TTSRSP;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.types.TranslationReadyHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TRNSLG {
    private static final String url = "https://translation.googleapis.com/language/translate/v2";
    private static final String token_query_param_key = "key";
    private static final String MIME_T = "application/json; charset=utf-8";

    public static void translate(final Message msg, Language source, Language target, final TranslationReadyHandler handler){
        final RequestData data = new RequestData(msg.getMessagetext(),source,target);
        new Thread(new Runnable() {
            Response result = null;
            String formattedPayload  = "No translation";
            @Override
            public void run() {
                try {
                    String text = msg.getMessagetext();
                    OkHttpClient connection = new OkHttpClient();
                    RequestBody formatedOverload = RequestBody.create(MediaType.parse(MIME_T),
                            JavaScriptObjectFactory.setData(data));
                    Request payload = new Request.Builder()
                            .url(url+"?key="+Utils.getGoogleKey())
//                            .addHeader(token_query_param_key, Utils.getGoogleKey())
                            .addHeader("Content-Type", MIME_T)
                            .post(formatedOverload)
                            .build();
                    result =  connection.newCall(payload).execute();
                    formattedPayload = Objects.requireNonNull(result.body()).string();
                    if(result == null || result.code() != 200){
                        throw new ResultFetchError(formattedPayload);
                    }
                    String tText = JavaScriptObjectFactory.toObjectHTMLEscapingIsDisabled(formattedPayload, ResponseData.class).getTranslatedText();
                    tText = tText.replace("&#39;" , "\'");
                    tText = tText.replace("&#34;" , "\"");
                    final String translatedText = tText;
                    msg.setMessagetranslatedtext(translatedText);
                    Utils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.translationDone(msg);
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }



    static class RequestData{

        @SerializedName("q")
        private String textToBeTranslated;

        @SerializedName("source")
        private String sourceLanguage;

        @SerializedName("target")
        private String targetLanguage;

        public RequestData(){
        }

        public RequestData (String text, Language source, Language target){

            textToBeTranslated= text;
            sourceLanguage = source.symbol;
            targetLanguage = target.symbol;

        }

        public String getTextToBeTranslated() {
            return textToBeTranslated;
        }

        public String getSourceLanguage() {
            return sourceLanguage;
        }

        public String getTargetLanguage() {
            return targetLanguage;
        }
    }

    static class ResponseData{
        private Map<String, List<Map<String,String>>> data;

        public Map<String, List<Map<String, String>>> getData() {
            return data;
        }

        public void setData(Map<String, List<Map<String, String>>> data) {
            this.data = data;
        }

        public String getTranslatedText(){
            try{
                return data.get("translations").get(0).get("translatedText");
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}

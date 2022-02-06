package hasan.mohamed.shehata.myapplication.templates;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.TranslationItem;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.BindableListItemContentProviderDiffUtilsCallback;
import hasan.mohamed.shehata.myapplication.types.NewMessagesConsumer;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;
import hasan.mohamed.shehata.myapplication.types.UserListConsumer;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.types.TranslationItemType;
import hasan.mohamed.shehata.myapplication.types.TranslatorCapabilities;
import hasan.mohamed.shehata.myapplication.types.UpdatableItem;
import hasan.mohamed.shehata.myapplication.views.MessageView;
import hasan.mohamed.shehata.myapplication.views.TranslationItemView;
import hasan.mohamed.shehata.myapplication.views.UserItemView;

public class GeneralRecyclerViewAdapter<T extends BindableItem> extends RecyclerView.Adapter<GeneralRecyclerViewItemViewHolder> implements ListItemCallbacks, Serializable, UserListConsumer, NewMessagesConsumer {
    private Context context;
    private List<ListItemBindableItemContentProvider> dataset;
    private ResultReceiver selectionReceiver;
    private Class<T> itemViewClass;
    private FabActionType fabActionType;
    private TranslatorCapabilities translatorCapabilities;
    private List<ListItemBindableItemContentProvider> languagesOfItems;
    private AsyncPinger pinger;
    private SpeakerProvider speakerProvider;
    private RecyclerView recyclerView;
    private User me;
    private long myUserId;

    /**************************************
     * @param context
     * @param dataset1
     * @param selectionReceiver is used by item view to call select method to inform fragment that an item has been selected
     * @param itemViewClass it is just ItemView.class  and item view must have 2 arguments , first is context and second is selectionReceiver
     **************************************/

    public GeneralRecyclerViewAdapter(Context context, List<ListItemBindableItemContentProvider> dataset1, ResultReceiver selectionReceiver, Class<T> itemViewClass, FabActionType fabActionType, TranslatorCapabilities capabilities, User buddy, SpeakerProvider speakerProvider, RecyclerView recyclerView, User me) {
        this.fabActionType = fabActionType;
        this.recyclerView = recyclerView;
        this.context = context;
        this.me = me;
        this.dataset = dataset1;
        this.selectionReceiver = selectionReceiver;
        this.itemViewClass = itemViewClass;
        this.speakerProvider = speakerProvider;
        this.translatorCapabilities = capabilities;
        myUserId = Utils.getUserID(context);
        long userID = Utils.getUserID(context);
        if ((Class) itemViewClass == UserItemView.class){
            this.dataset = dataset1;
            pinger = ((AsyncPingerProvider)context).getCurrentPinger();
            if(pinger != null)
                pinger.addUsersConsumer(this);

        }
        else if(((Class)itemViewClass) == MessageView.class){
            this.selectionReceiver = new ResultReceiver() {
                @Override
                public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {

                }

                @Override
                public void deleteItem(ListItemBindableItemContentProvider item) {
                    dataset.remove(item);
                    notifyDataSetChanged();
                }

                @Override
                public User getBuddy() {
                    return buddy;
                }

                @Override
                public SpeakerProvider provideSpeaker() {
                    return speakerProvider;
                }
            };

            ((AsyncPingerProvider)context).getCurrentPinger().setFastRate();

            ((AsyncPingerProvider)context).getCurrentPinger().addNewMessagesConsumer(buddy.getUserid(), this);
        }
        else {
            if (dataset1 == null || dataset1.size() == 0 || dataset1.get(0) == null) {

                if ((Class) itemViewClass == TranslationItemView.class) {
                    this.languagesOfItems = new ArrayList<>();
                    this.dataset = new ArrayList<>();
                    TranslationItem sourceTrI = new TranslationItem(context, null, null, null, TranslationItemType.Source, translatorCapabilities, this);
                    this.dataset.add(sourceTrI);
                    languagesOfItems.add(sourceTrI.getLanguage());
                    update(this.dataset);
                }


            } else {
                if ((Class) itemViewClass == TranslationItemView.class) {
                    this.languagesOfItems = new ArrayList<>();
                    this.dataset = new ArrayList<>();
                    this.dataset.add(new TranslationItem(context, (Language) dataset1.get(0), null, null, TranslationItemType.Source, translatorCapabilities, this));
                    languagesOfItems.add(dataset1.get(0));
                    for (int i = 1; i < dataset1.size(); i++) {
                        try {
                            addNewItem((Language) dataset1.get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    update(this.dataset);
                }
            }


        }
        try {
            if (fabActionType == FabActionType.AddNewItem) {
                ((FabSource) context).setFabActionType(FabActionType.AddNewItem);
                ((FabSource) context).setFabAction(new Runnable() {
                    @Override
                    public void run() {
                        addNewItem(null);
                    }
                });
            } else {
                ((FabSource) context).setFabActionType(FabActionType.None);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addNewItem(Language targetLanguage) {
        ListItemBindableItemContentProvider.getNewItem(context, dataset, translatorCapabilities,this, targetLanguage);
    }

    @NonNull
    @Override
    public GeneralRecyclerViewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) getInstanceOfBindableItem(itemViewClass);
        if (view != null) {
            GeneralRecyclerViewItemViewHolder generalRecyclerViewItemViewHolder = new GeneralRecyclerViewItemViewHolder(view);
            return generalRecyclerViewItemViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralRecyclerViewItemViewHolder holder, int position) {
        if (holder != null) {
            BindableItem bindableItem = null;
            try {
                bindableItem = (BindableItem) holder.getItemView();
                bindableItem.bind(dataset.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (dataset != null) {
            return dataset.size();
        }
        else
            return 0;
    }

    public List<ListItemBindableItemContentProvider> getDataset() {
        return this.dataset;
    }

    public void update(List<ListItemBindableItemContentProvider> bindableItemContentProviders) {
        this.dataset = bindableItemContentProviders;
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private T getInstanceOfBindableItem(Class<T> classT) {
        try {
            Constructor<?>[] constructors = classT.getConstructors();
            return classT.getDeclaredConstructor(Context.class, ResultReceiver.class).newInstance(context, selectionReceiver);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(ListItemBindableItemContentProvider provider) {
        int index = dataset.indexOf(provider);
        languagesOfItems.remove(index);
        dataset.remove(provider);
        update(dataset);
    }

    @Override
    public void add(ListItemBindableItemContentProvider item) {
        dataset.add(item);
        languagesOfItems.add(((TranslationItem)item).getLanguage());
    }

    @Override
    public void setNewDataSet(List<ListItemBindableItemContentProvider> newDataSet) {
        for(ListItemBindableItemContentProvider item : dataset){
            item.disposeResources();
        }
        dataset = newDataSet;
        languagesOfItems = new ArrayList<>();
        for(ListItemBindableItemContentProvider item : dataset){
            languagesOfItems.add(((TranslationItem)item).getLanguage());
        }
        update(dataset);
    }

    @Override
    public void refreshDataSet() {
        update(dataset);
    }

    @Override
    public void updateTranslationDataSet(String text) {
        if(dataset != null){
            for(Object o : dataset){
                ((UpdatableItem)o).update(text);
            }
        }
    }

    @Override
    public void sourceLanguageSelected(Language sourceLanguage) {
        languagesOfItems.remove(0);
        languagesOfItems.add(0,sourceLanguage);
    }


    public void release(){
        if(dataset!=null){
            for (ListItemBindableItemContentProvider item : dataset)
                item.disposeResources();
        }

    }

    public List<ListItemBindableItemContentProvider> getTranslatorItemsLanguages(){
//        List<ListItemBindableItemContentProvider> languages = new ArrayList<>();
//        try {
//            for(int i = 0 ; i< dataset.size() ; i ++){
//                languages.add(((TranslationItem)dataset.get(0)).getLanguage());
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return languages;
        return languagesOfItems;
    }


    @Override
    public void getUsersList(List<User> users, Fragment owner) {
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        List<ListItemBindableItemContentProvider> oldDataSet = dataset;
        List<ListItemBindableItemContentProvider> newDataSet = new ArrayList<>(users);
        dataset = newDataSet;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BindableListItemContentProviderDiffUtilsCallback(newDataSet, oldDataSet));
        diffResult.dispatchUpdatesTo(this);
//        if(dataset == null && users == null)
//            return;
//        else if(dataset != null && users != null) {
//            if (dataset.size() != users.size()) {
//                dataset = new ArrayList<ListItemBindableItemContentProvider>(users);
//                notifyDataSetChanged();
//            }
//        }
//        else if(dataset == null && users != null){
//            dataset = new ArrayList<ListItemBindableItemContentProvider>(users);
//            notifyDataSetChanged();
//        }
//        else if(dataset != null && users == null){
//            dataset = null;
//            notifyDataSetChanged();
//        }
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

    }

    private Parcelable recyclerViewState;;
    @Override
    public void newMessage(long senderUserId, Message message) {
        // Save state
        if(!checkIfControlMessage(message)) {

            if (senderUserId == me.getUserid() || speakerProvider.isForCall()) {
                takeNewMessage(senderUserId, message);
                recyclerView.scrollToPosition(dataset.size() - 1);
            } else {
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                takeNewMessage(senderUserId, message);

                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

            }

            notifyItemInserted(dataset.size() - 1);
            if(recyclerView != null) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(dataset.size() - 1);
                    }
                },1300);
            }
            if (speakerProvider.isForCall() && message.getSenderid() != myUserId) {
                speakerProvider.speak(message);
            }
        }

    }

    private boolean checkIfControlMessage(Message message) {
        String control = message.getMessagemoshakkaltext();
        if(control != null){
            if(control.equals("Calling") ||control.equals("Accept") || control.equals("Busy") || control.equals("Terminate"))
                return true;
        }
        return false;
    }

    private void takeNewMessage(long useid, Message message){
        dataset.add(message);
        message.setIsToShowTranslatedText(true);
        List<ListItemBindableItemContentProvider> newList = new ArrayList<>(dataset);
        newList.add(message);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BindableListItemContentProviderDiffUtilsCallback(this.dataset, newList));
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void sendAndSaveThisMessage(Message message) {
        // Done : send and save this message

    }
}

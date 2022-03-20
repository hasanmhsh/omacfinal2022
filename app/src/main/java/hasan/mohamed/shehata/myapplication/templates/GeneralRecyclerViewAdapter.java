package hasan.mohamed.shehata.myapplication.templates;

import android.content.Context;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.MessageStatus;
import hasan.mohamed.shehata.myapplication.models.TranslationItem;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.Callable;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.BindableListItemContentProviderDiffUtilsCallback;
import hasan.mohamed.shehata.myapplication.types.ImageReady;
import hasan.mohamed.shehata.myapplication.types.NewGroupsMessagesConsumer;
import hasan.mohamed.shehata.myapplication.types.NewMessagesConsumer;
import hasan.mohamed.shehata.myapplication.types.SearchCallbacks;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;
import hasan.mohamed.shehata.myapplication.types.UserListConsumer;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.types.TranslationItemType;
import hasan.mohamed.shehata.myapplication.types.TranslatorCapabilities;
import hasan.mohamed.shehata.myapplication.types.UpdatableItem;
import hasan.mohamed.shehata.myapplication.types.UsersViewType;
import hasan.mohamed.shehata.myapplication.views.DualTextRecyclerViewItemView;
import hasan.mohamed.shehata.myapplication.views.MessageView;
import hasan.mohamed.shehata.myapplication.views.TranslationItemView;
import hasan.mohamed.shehata.myapplication.views.UserItemView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeneralRecyclerViewAdapter<T extends BindableItem> extends RecyclerView.Adapter<GeneralRecyclerViewItemViewHolder> implements ListItemCallbacks, Serializable, UserListConsumer, NewMessagesConsumer, NewGroupsMessagesConsumer {
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
    private boolean isGroupChatMessageView = false;
    private Group chatGroup;
    private GeneralRecyclerViewAdapter<T> thiz;
    private UsersViewType usersViewType;
    private boolean isMultipleChoicesItems;
    private List<ListItemBindableItemContentProvider> initialDataSet;
    private SearchCallbacks searchCallbacks;
    private String searchQuery = "";
    private boolean isReadOnly = false;
    interface GenericListCallable{
        public void call(List<ListItemBindableItemContentProvider> list);
    }

    /**************************************
     * @param context
     * @param dataset1
     * @param selectionReceiver is used by item view to call select method to inform fragment that an item has been selected
     * @param itemViewClass it is just ItemView.class  and item view must have 2 arguments , first is context and second is selectionReceiver
     * @param isViewForGroupChat
     * @param chatGroup
     * @param usersViewType
     **************************************/

    public GeneralRecyclerViewAdapter(Context context, List<ListItemBindableItemContentProvider> dataset1, ResultReceiver selectionReceiver, Class<T> itemViewClass, FabActionType fabActionType, TranslatorCapabilities capabilities, User buddy, SpeakerProvider speakerProvider, RecyclerView recyclerView, User me, boolean isViewForGroupChat, Group chatGroup, UsersViewType usersViewType, boolean isMultiChoices, SearchCallbacks searchCallbacks   , boolean isReadOnly  ) {
        this.fabActionType = fabActionType;
        this.recyclerView = recyclerView;
        this.context = context;
        this.me = me;
        this.initialDataSet = dataset1;
        this.dataset = dataset1;
        thiz = this;
        this.selectionReceiver = selectionReceiver;
        this.itemViewClass = itemViewClass;
        this.speakerProvider = speakerProvider;
        this.translatorCapabilities = capabilities;
        myUserId = Utils.getUserID(context);
        long userID = Utils.getUserID(context);
        this.isGroupChatMessageView = isViewForGroupChat;
        this.chatGroup = chatGroup;
        this.usersViewType = usersViewType;
        this.isMultipleChoicesItems = isMultiChoices;
        this.searchCallbacks = searchCallbacks;
        this.isReadOnly = isReadOnly;
        if(searchCallbacks != null){
            searchCallbacks.setSearchable(new SearchCallbacks.Searchable() {
                @Override
                public void find(String query) {
                    searchQuery = query;
                    List<ListItemBindableItemContentProvider> newDataSet = new ArrayList<>();
                    if(initialDataSet != null){
                        synchronized (initialDataSet) {
                            for (ListItemBindableItemContentProvider item : initialDataSet) {
                                if (item.getPrimaryText().toUpperCase().contains(query.toUpperCase())) {
                                    newDataSet.add(item);
                                    update(newDataSet);
                                }
                            }
                        }
                    }
                    else{
                        if(dataset != null){
                            synchronized (dataset) {
                                for (ListItemBindableItemContentProvider item : dataset) {
                                    if (item.getPrimaryText().toUpperCase().contains(query.toUpperCase())) {
                                        newDataSet.add(item);
                                        update(newDataSet);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        if ((Class) itemViewClass == UserItemView.class){
//            this.dataset = dataset1;

            GenericListCallable then = new GenericListCallable() {
                @Override
                public void call(List<ListItemBindableItemContentProvider> list) {
                    dataset = list;
                    pinger = ((AsyncPingerProvider)context).getCurrentPinger();
                    if(pinger != null){
                        if(usersViewType == UsersViewType.groups)
                            pinger.addGroupsConsumer(thiz);
                        else
                            pinger.addUsersConsumer(thiz);
                    }

                }
            };
            filterUsersListDataSet(dataset1, then);


        }
        else if(((Class)itemViewClass) == MessageView.class){
            this.selectionReceiver = new ResultReceiver() {
                @Override
                public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {

                }

                @Override
                public void receiveMultipleChoices(List<ListItemBindableItemContentProvider> list) {

                }

                @Override
                public void deleteItem(ListItemBindableItemContentProvider item) {
                    dataset.remove(item);
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                    notifyDataSetChanged();

                    Utils.runOnUIThreadPostDelayedSpeceific(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        }
                    },100);



                }

                @Override
                public User getBuddy() {
                    return buddy;
                }

                @Override
                public Group getGroup() {
                    return chatGroup;
                }

                @Override
                public SpeakerProvider provideSpeaker() {
                    return speakerProvider;
                }

                @Override
                public boolean isReadOnly() {
                    return isReadOnly;
                }
            };

            ((AsyncPingerProvider)context).getCurrentPinger().setFastRate();

            if(isViewForGroupChat)
                ((AsyncPingerProvider)context).getCurrentPinger().addNewGroupMessagesConsumer(chatGroup.getGroupid(),this);
            else
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

    private void filterUsersListDataSet(List<ListItemBindableItemContentProvider> dataset1, final GenericListCallable then) {

        if(dataset1!=null) {
//            List<String> contacts = Utils.getPhoneNumberList(context);
            List<ListItemBindableItemContentProvider> filteredList = new ArrayList<>();


            int idCurrentDestenation = 0;
            if(context != null)
                idCurrentDestenation = ((TranslationMainActivity)context).getCurrentDestenationId();
            switch (idCurrentDestenation) {
//                case R.id.nav_contacts: {
//                    APIClient.getAPIInterface(context).getRegisteredContactsUsers(contacts).enqueue(new Callback<List<User>>() {
//                        @Override
//                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                            if(response.isSuccessful()){
//                                if(then != null)
//                                    then.call(new ArrayList<ListItemBindableItemContentProvider>(response.body()));
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<List<User>> call, Throwable t) {
//                            call.cancel();
//                        }
//                    });
//                }
//                break;
                case R.id.nav_calls:{
                    List<ListItemBindableItemContentProvider> onlineUsers = new ArrayList<>();
                    for(ListItemBindableItemContentProvider item : dataset1){
                        if(item != null){
                            if(((User)item).getIsOnline())
                                onlineUsers.add(item);
                        }
                    }
                    if(then != null)
                        then.call(onlineUsers);

                }
                break;
                default:
                case R.id.nav_users: {
                    if(then != null)
                        then.call(dataset1);
                }
            }


        }
        else if(dataset1!=null){
            if(then != null)
                then.call(dataset1);
        }
        else {
            if(then != null)
                then.call(null);
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
                ((View) holder.getItemView()).setEnabled(!isReadOnly);
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
            if(itemViewClass == DualTextRecyclerViewItemView.class || itemViewClass == UserItemView.class)
                return classT.getDeclaredConstructor(Context.class, ResultReceiver.class,Boolean.class).newInstance(context, selectionReceiver,isMultipleChoicesItems);
            else
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



    private GenericListCallable atUserListConsumed = new GenericListCallable() {
        @Override
        public void call(List<ListItemBindableItemContentProvider> list) {
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            List<ListItemBindableItemContentProvider> oldDataSet = dataset;
            List<ListItemBindableItemContentProvider> newDataSet = list;
            dataset = newDataSet;
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BindableListItemContentProviderDiffUtilsCallback(newDataSet, oldDataSet));
            diffResult.dispatchUpdatesTo(thiz);
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
    };

    @Override
    public void getUsersList(List<User> users, Fragment owner) {
        String query = searchQuery;
        if(initialDataSet != null) {
            synchronized (initialDataSet) {
                initialDataSet = new ArrayList<>(users);
            }
        }
        else{
            initialDataSet = new ArrayList<>(users);
        }
        List<ListItemBindableItemContentProvider> newFilteredDataSet = new ArrayList<>();
        for(User user : users){
            if(user.getUsername().toLowerCase().contains(query.toLowerCase())){
                newFilteredDataSet.add(user);
            }
        }
        if(context!=null) {
            if(((TranslationMainActivity)context).getCurrentDestenationId() != R.id.nav_groups)
                filterUsersListDataSet(newFilteredDataSet, atUserListConsumed);
        }


    }

    @Override
    public void getGroupList(List<Group> groups, Fragment fragment) {
        String query = searchQuery;
        if(initialDataSet != null) {
            synchronized (initialDataSet) {
                initialDataSet = new ArrayList<>(groups);
            }
        }
        else{
            initialDataSet = new ArrayList<>(groups);
        }
        List<ListItemBindableItemContentProvider> newFilteredDataSet = new ArrayList<>();
        for(Group group : groups){
            if(group.getName().toLowerCase().contains(query.toLowerCase())){
                newFilteredDataSet.add(group);
            }
        }
        if(context!=null) {
            if(((TranslationMainActivity)context).getCurrentDestenationId() == R.id.nav_groups)
                filterUsersListDataSet(newFilteredDataSet, atUserListConsumed);
        }

    }

    private Parcelable recyclerViewState;;
    @Override
    public void newMessage(long senderUserId, Message message) {
        // Save state
        if(dataset != null) {
            synchronized (dataset) {
                boolean isDeleteMessage = false;
                if (message.getControlText() != null)
                    isDeleteMessage = message.getControlText().equals(Utils.MESSAGE_DELETE_COMMAND);
                int deletedItemPosition = 0;
                if (isDeleteMessage) {
                    for (int i = 0; i < dataset.size(); i++) {
                        if (((Message) dataset.get(i)).getMessageid() == message.getControlnumber()) {
                            message = (Message) dataset.get(i);
                            message.setControlnumber(message.getMessageid());
                            deletedItemPosition = i;
                        }
                    }
                }

                if (!checkIfControlMessage(message) || isDeleteMessage) {

                    if (senderUserId == me.getUserid() || speakerProvider.isForCall() || isDeleteMessage) {
                        takeNewMessage(senderUserId, message, isDeleteMessage);
                        recyclerView.scrollToPosition(dataset.size() - 1);
                    } else {
                        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                        takeNewMessage(senderUserId, message, isDeleteMessage);

                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                    }

                    if (isDeleteMessage)
                        notifyItemRemoved(deletedItemPosition);
                    else
                        notifyItemInserted(dataset.size() - 1);
                    if (recyclerView != null && !isDeleteMessage) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.scrollToPosition(dataset.size() - 1);
                            }
                        }, 500);
                    } else if (recyclerView != null && isDeleteMessage) {
                        int position = deletedItemPosition;
                        if (position != 0) {
                            if (position >= dataset.size())
                                position--;
                        }
                        if (position < dataset.size() && position >= 0) {
                            final int position1 = position;

                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.scrollToPosition(position1);
                                }
                            }, 500);
                        }
                    }
                    if ((speakerProvider.isForCall() || Utils.isContinuousSpeaking) && message.getSenderid() != myUserId) {
                        speakerProvider.speak(message);
                    }
                }
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

    private void takeNewMessage(long useid, Message message, boolean isDeleteMessage){
        if(dataset != null) {
            synchronized (dataset) {
                if (isDeleteMessage)
                    dataset.remove(message);
                else
                    dataset.add(message);
                message.setIsToShowTranslatedText(true);
                List<ListItemBindableItemContentProvider> newList = new ArrayList<>(dataset);
                if (isDeleteMessage)
                    newList.remove(message);
                else
                    newList.add(message);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BindableListItemContentProviderDiffUtilsCallback(this.dataset, newList));
                diffResult.dispatchUpdatesTo(this);
            }
        }
    }

    @Override
    public void sendAndSaveThisMessage(Message message) {
        // Done : send and save this message

    }

    @Override
    public void deleteMessage(final long messageid) {
//        Utils.runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                if (thiz != null && dataset != null) {
//                    for (ListItemBindableItemContentProvider item : dataset) {
//                        if (((Message) item).getControlnumber() == messageid) {
//                            List<ListItemBindableItemContentProvider> newList = new ArrayList<>(dataset);
//                            newList.remove(item);
//                            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BindableListItemContentProviderDiffUtilsCallback(dataset, newList));
//                            diffResult.dispatchUpdatesTo(thiz);
//                        }
//                    }
//                }
//            }
//        });

    }

    @Override
    public void unreadMessages(List<Message> unreadMessages) {
        if(unreadMessages != null &&dataset != null && itemViewClass == MessageView.class) {
            synchronized (dataset) {
                for (ListItemBindableItemContentProvider item : dataset) {
                    if (item != null && item instanceof Message) {
                        if (unreadMessages.size() == 0) {
                            final Message read = (Message) item;
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    read.setContext(context);
                                    read.setMessagestatus(MessageStatus.read);
                                }
                            });
                            continue;
                        }
                        for (Message urm : unreadMessages) {
                            Message rm = (Message) item;
                            if (rm.getMessageid() == urm.getMessageid()) {
                                final Message read = rm;
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        read.setContext(context);
                                        read.setMessagestatus(MessageStatus.delivered);
                                    }
                                });
                            } else {
                                final Message read = rm;
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        read.setContext(context);
                                        read.setMessagestatus(MessageStatus.read);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public long getGroupId() {
        if(chatGroup != null)
            return chatGroup.getGroupid();
        else
            return 0;
    }




    //Call backs for popup selection window
//    private EditText searchET;

    public List<ListItemBindableItemContentProvider> getSelection(){
        List<ListItemBindableItemContentProvider> selection = new ArrayList<>();
        if(dataset != null) {
            for (ListItemBindableItemContentProvider item : dataset) {
                if(item.getIsHighLighted()){
                    selection.add(item);
                }
            }
        }
        return selection;
    }



    private ImageReady groupImageUpdater = null;
    public void setGroupImageUpdater(ImageReady imageUpdater){
        groupImageUpdater = imageUpdater;
    }

    @Override
    public void updateGroupImage() {
        if(groupImageUpdater != null && isGroupChatMessageView && chatGroup!=null&& chatGroup.getGroupid()!= 0){
            TranslationMainActivity activity = (TranslationMainActivity) context;
            if(activity!=null){
                AsyncPinger pinger = activity.getCurrentPinger();
                if(pinger != null){
                    pinger.registerImageReadyListenerOrGetImageIfExistForGroups(chatGroup.getGroupid(),groupImageUpdater);
                }
            }
        }
    }

}

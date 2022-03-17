package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.ui.messages.MessageFragment;

public interface MessageFragmentProvider {
    public MessageFragment provideMessageFragment(User buddy, boolean isForCall);
    public MessageFragment provideGroupMessageFragment(Group group);
    public void endCallFragment();
}

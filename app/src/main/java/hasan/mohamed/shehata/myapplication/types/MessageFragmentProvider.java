package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.ui.messages.MessageFragment;

public interface MessageFragmentProvider {
    public MessageFragment provideMessageFragment(User buddy, boolean isForCall);
    public void endCallFragment();
}

package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.async.AsyncPinger;

public interface AsyncPingerProvider {
  public AsyncPinger getCurrentPinger();
  public void createPingerIfNotCreated();
  public void registerUserConsumerAfterCreatingPinger(UserListConsumer userListConsumer);
}

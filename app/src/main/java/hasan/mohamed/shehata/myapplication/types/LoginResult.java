package hasan.mohamed.shehata.myapplication.types;

import com.google.gson.annotations.SerializedName;

import hasan.mohamed.shehata.myapplication.models.User;

public class LoginResult {
  @SerializedName("success")
  private boolean isLoginSuccessfully;

  @SerializedName("user")
  private User user;

  public boolean isLoginSuccessfully() {
    return isLoginSuccessfully;
  }

  public User getUser() {
    return user;
  }
}

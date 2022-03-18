package hasan.mohamed.shehata.myapplication.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import hasan.mohamed.shehata.myapplication.models.User;

public class LoginResult {
  @JsonIgnore
  @SerializedName("success")
  private boolean success;

//  @JsonProperty("user")
@JsonIgnore
  @SerializedName("user")
  private User user;

  @JsonIgnore
//  @JsonProperty("success")
  public boolean isSuccess() {
    return success;
  }


  @JsonIgnore
//  @JsonProperty("user")
  public User getUser() {
    return user;
  }
}

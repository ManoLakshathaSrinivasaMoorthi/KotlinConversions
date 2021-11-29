
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable


class AppConfig : Serializable {
    //  @Override
    //  public JsonMap set(String fieldName, Object value) {
    //    return (JsonMap) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public JsonMap clone() {
    //    return (JsonMap) super.clone();
    //  }
    @SerializedName("logoutAppTimerinMilli")
    @Expose
    var logoutAppTimerinMilli: String? = null

    @SerializedName("logoutServerTimerinMilli")
    @Expose
    var logoutServerTimerinMilli: String? = null
}
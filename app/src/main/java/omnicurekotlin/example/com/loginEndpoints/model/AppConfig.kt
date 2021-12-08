
package omnicurekotlin.example.com.loginEndpoints.model

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

    var logoutAppTimerinMilli: String? = null


    var logoutServerTimerinMilli: String? = null

    @JvmName("getLogoutAppTimerinMilli1")
    fun getLogoutAppTimerinMilli(): String? {
        return logoutAppTimerinMilli
    }

    @JvmName("setLogoutAppTimerinMilli1")
    fun setLogoutAppTimerinMilli(logoutAppTimerinMilli: String?) {
        this.logoutAppTimerinMilli = logoutAppTimerinMilli
    }

    @JvmName("getLogoutServerTimerinMilli1")
    fun getLogoutServerTimerinMilli(): String? {
        return logoutServerTimerinMilli
    }

    @JvmName("setLogoutServerTimerinMilli1")
    fun setLogoutServerTimerinMilli(logoutServerTimerinMilli: String?) {
        this.logoutServerTimerinMilli = logoutServerTimerinMilli
    }
}
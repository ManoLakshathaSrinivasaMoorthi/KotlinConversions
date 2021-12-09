
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable

class ChangePasswordRequest : Serializable {


    var email: String? = null
    var oldPassword: String? = null
    var newPassword: String? = null
    var confirmPassword: String? = null

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    @JvmName("setEmail1")
    fun setEmail(email: String?) {
        this.email = email
    }

    @JvmName("getOldPassword1")
    fun getOldPassword(): String? {
        return oldPassword
    }

    @JvmName("setOldPassword1")
    fun setOldPassword(oldPassword: String?) {
        this.oldPassword = oldPassword
    }

    @JvmName("getNewPassword1")
    fun getNewPassword(): String? {
        return newPassword
    }

    @JvmName("setNewPassword1")
    fun setNewPassword(newPassword: String?) {
        this.newPassword = newPassword
    }

    @JvmName("getConfirmPassword1")
    fun getConfirmPassword(): String? {
        return confirmPassword
    }

    @JvmName("setConfirmPassword1")
    fun setConfirmPassword(confirmPassword: String?) {
        this.confirmPassword = confirmPassword
    }


}

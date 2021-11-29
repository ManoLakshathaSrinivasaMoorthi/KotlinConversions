package omnicurekotlin.example.com.providerEndpoints.model;

import java.io.Serializable;

//public class ChatReceiverModelClass extends com.google.api.client.json.GenericJson {
public class ChatReceiverModelClass implements Serializable {

    //com.google.api.client.util.Key
    private String receiverId;
    //com.google.api.client.util.Key
    private String providerName;
    //com.google.api.client.util.Key
    private String providerType;
    //com.google.api.client.util.Key
    private String profilePicUrl;
    //com.google.api.client.util.Key
    private String status;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.example.kotlinomnicure.media

import java.lang.Exception

class RtcTokenBuilder {
    private val TAG = "RTctOKEN"

    enum class Role(private val initValue: Int) {
        /**
         * DEPRECATED. Role_Attendee has the same privileges as Role_Publisher.
         */
        Role_Attendee(0),

        /**
         * RECOMMENDED. Use this role for a voice/video call or a live broadcast, if your scenario does not require authentication for [Hosting-in](https://docs.agora.io/en/Agora%20Platform/terms?platform=All%20Platforms#hosting-in).
         */
        Role_Publisher(1),

        /**
         * Only use this role if your scenario require authentication for [Hosting-in](https://docs.agora.io/en/Agora%20Platform/terms?platform=All%20Platforms#hosting-in).
         *
         * @note In order for this role to take effect, please contact our support team to enable authentication for Hosting-in for you. Otherwise, Role_Subscriber still has the same privileges as Role_Publisher.
         */
        Role_Subscriber(2),

        /**
         * DEPRECATED. Role_Attendee has the same privileges as Role_Publisher.
         */
        Role_Admin(101);
    }

    /**
     * Builds an RTC token using an int uid.
     *
     * @param appId The App ID issued to you by Agora.
     * @param appCertificate Certificate of the application that you registered in
     * the Agora Dashboard.
     * @param channelName The unique channel name for the AgoraRTC session in the string format. The string length must be less than 64 bytes. Supported character scopes are:
     *
     *  *  The 26 lowercase English letters: a to z.
     *  *  The 26 uppercase English letters: A to Z.
     *  *  The 10 digits: 0 to 9.
     *  *  The space.
     *  *  "!", "#", "$", "%", "&", "(", ")", "+", "-", ":", ";", "<", "=", ".", ">", "?", "@", "[", "]", "^", "_", " {", "}", "|", "~", ",".
     *
     * @param uid  User ID. A 32-bit unsigned integer with a value ranging from
     * 1 to (2^32-1).
     * @param role The user role.
     *
     *  *  Role_Publisher = 1: RECOMMENDED. Use this role for a voice/video call or a live broadcast.
     *  *  Role_Subscriber = 2: ONLY use this role if your live-broadcast scenario requires authentication for [Hosting-in](https://docs.agora.io/en/Agora%20Platform/terms?platform=All%20Platforms#hosting-in). In order for this role to take effect, please contact our support team to enable authentication for Hosting-in for you. Otherwise, Role_Subscriber still has the same privileges as Role_Publisher.
     *
     * @param privilegeTs Represented by the number of seconds elapsed since 1/1/1970.
     * If, for example, you want to access the Agora Service within 10 minutes
     * after the token is generated, set expireTimestamp as the current time stamp
     * + 600 (seconds).
     */
    fun buildTokenWithUid(
        appId: String?, appCertificate: String?,
        channelName: String?, uid: Int, role: Role, privilegeTs: Int
    ): String? {
        val account = if (uid == 0) "" else uid.toString()
        return buildTokenWithUserAccount(
            appId, appCertificate, channelName,
            account, role, privilegeTs
        )
    }

    /**
     * Builds an RTC token using a string userAccount.
     *
     * @param appId The App ID issued to you by Agora.
     * @param appCertificate Certificate of the application that you registered in
     * the Agora Dashboard.
     * @param channelName The unique channel name for the AgoraRTC session in the string format. The string length must be less than 64 bytes. Supported character scopes are:
     *
     *  *  The 26 lowercase English letters: a to z.
     *  *  The 26 uppercase English letters: A to Z.
     *  *  The 10 digits: 0 to 9.
     *  *  The space.
     *  *  "!", "#", "$", "%", "&", "(", ")", "+", "-", ":", ";", "<", "=", ".", ">", "?", "@", "[", "]", "^", "_", " {", "}", "|", "~", ",".
     *
     * @param account  The user account.
     * @param role The user role.
     *
     *  *  Role_Publisher = 1: RECOMMENDED. Use this role for a voice/video call or a live broadcast.
     *  *  Role_Subscriber = 2: ONLY use this role if your live-broadcast scenario requires authentication for [Hosting-in](https://docs.agora.io/en/Agora%20Platform/terms?platform=All%20Platforms#hosting-in). In order for this role to take effect, please contact our support team to enable authentication for Hosting-in for you. Otherwise, Role_Subscriber still has the same privileges as Role_Publisher.
     *
     * @param privilegeTs represented by the number of seconds elapsed since 1/1/1970.
     * If, for example, you want to access the Agora Service within 10 minutes
     * after the token is generated, set expireTimestamp as the current time stamp
     * + 600 (seconds).
     */
    fun buildTokenWithUserAccount(
        appId: String?, appCertificate: String?,
        channelName: String?, account: String?, role: Role, privilegeTs: Int
    ): String? {

        // Assign appropriate access privileges to each role.
        val builder = AccessToken(appId, appCertificate, channelName, account)
        builder.addPrivilege(AccessToken.Privileges.kJoinChannel, privilegeTs)
        if (role == Role.Role_Publisher || role == Role.Role_Subscriber || role == Role.Role_Admin) {
            builder.addPrivilege(AccessToken.Privileges.kPublishAudioStream, privilegeTs)
            builder.addPrivilege(AccessToken.Privileges.kPublishVideoStream, privilegeTs)
            builder.addPrivilege(AccessToken.Privileges.kPublishDataStream, privilegeTs)
        }
        return try {
            builder.build()
        } catch (e: Exception) {
            //			Log.e(TAG, "Exception:", e.getCause());
            ""
        }
    }
}

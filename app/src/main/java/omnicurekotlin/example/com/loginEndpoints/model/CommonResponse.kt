/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2018-10-08 17:45:39 UTC)
 * on 2020-07-20 at 06:48:21 UTC
 * Modify at your own risk.
 */
package omnicurekotlin.example.com.loginEndpoints.model


import java.io.Serializable


class CommonResponse : Serializable {

    var activeList: List<Patient>? = null
    var dischargedCount: Int? = null
    var idToken: String? = null
    var refreshToken: String? = null
    var dischargedList: List<Patient>? = null
    var errorId: Int? = null
    var errorMessage: String? = null
    var feedbackForm: String? = null
    var tutorial_url: String? = null
    var id: Long? = null
    var provider: Provider? = null
    var pendingList: List<Patient>? = null
    var providerList: List<Provider>? = null
    var room: Room? = null
    var status: Boolean? = null
    var email: String? = null
    var encryptionKey: String? = null
    var aesEncryptionKey: String? = null
    var agoraAppId: String? = null
    var agoraAppCertificate: String? = null
    var unit: Unit? = null
    var ward: Ward? = null



    fun setActiveList(activeList: List<Patient>?): CommonResponse {
        this.activeList = activeList
        return this
    }


    fun setDischargedCount(dischargedCount: Int?): CommonResponse {
        this.dischargedCount = dischargedCount
        return this
    }


    fun setDischargedList(dischargedList: List<Patient>?): CommonResponse {
        this.dischargedList = dischargedList
        return this
    }

    fun setErrorId(errorId: Int?): CommonResponse {
        this.errorId = errorId
        return this
    }


    fun setErrorMessage(errorMessage: String?): CommonResponse {
        this.errorMessage = errorMessage
        return this
    }


    fun setId(id: Long?): CommonResponse {
        this.id = id
        return this
    }


    fun setPendingList(pendingList: List<Patient>?): CommonResponse {
        this.pendingList = pendingList
        return this
    }


    fun setProvider(provider: Provider?): CommonResponse {
        this.provider = provider
        return this
    }


    fun setProviderList(providerList: List<Provider>?): CommonResponse {
        this.providerList = providerList
        return this
    }

    fun setRoom(room: Room?): CommonResponse {
        this.room = room
        return this
    }

    fun setStatus(status: Boolean?): CommonResponse {
        this.status = status
        return this
    }

    fun setUnit(unit: Unit?): CommonResponse {
        this.unit = unit
        return this
    }


    fun setWard(ward: Ward?): CommonResponse {
        this.ward = ward
        return this
    }
}
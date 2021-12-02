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

class Room : Serializable {

    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var joiningTime: Long? = null
    var name: String? = null
    var totalNumberOfBeds: Int? = null
    var unitId: Long? = null
    var unitName: String? = null
    var ward: String? = null

    private var wardId: Long? = null


    fun setHospital(hospital: String?): Room {
        this.hospital = hospital
        return this
    }

    /**
     * @param hospitalId hospitalId or `null` for none
     */
    fun setHospitalId(hospitalId: Long?): Room {
        this.hospitalId = hospitalId
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): Room {
        this.id = id
        return this
    }

    /**
     * @param joiningTime joiningTime or `null` for none
     */
    fun setJoiningTime(joiningTime: Long?): Room {
        this.joiningTime = joiningTime
        return this
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): Room {
        this.name = name
        return this
    }

    /**
     * @param totalNumberOfBeds totalNumberOfBeds or `null` for none
     */
    fun setTotalNumberOfBeds(totalNumberOfBeds: Int?): Room {
        this.totalNumberOfBeds = totalNumberOfBeds
        return this
    }

    /**
     * @param unitId unitId or `null` for none
     */
    fun setUnitId(unitId: Long?): Room {
        this.unitId = unitId
        return this
    }

    /**
     * @param unitName unitName or `null` for none
     */
    fun setUnitName(unitName: String?): Room {
        this.unitName = unitName
        return this
    }

    /**
     * @param ward ward or `null` for none
     */
    fun setWard(ward: String?): Room {
        this.ward = ward
        return this
    }

    /**
     * @param wardId wardId or `null` for none
     */
    fun setWardId(wardId: Long?): Room {
        this.wardId = wardId
        return this
    } //  @Override
    //  public Room set(String fieldName, Object value) {
    //    return (Room) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public Room clone() {
    //    return (Room) super.clone();
    //  }
}
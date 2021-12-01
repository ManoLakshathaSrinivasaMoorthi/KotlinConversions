/**
 * Copyright Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kotlinomnicure.model


class HandOffList  {
    var id: String? = null
    var messageId: String? = null
    var message: String? = null
    var type //text,image,video,audio,system
            : String? = null
    var subType //completion, summary
            : String? = null
    var text: String? = null
    var title: String? = null
    var name: String? = null
    var role: String? = null
    var isUrgent = false
    var senderId: String? = null
    var photoUrl: String? = null
    var imageUrl: String? = null
    var thumbUrl: String? = null
    var videoUrl: String? = null
    var filename: String? = null
    var senderName: String? = null
    var receiverList: List<ChatMessageStatusModel>? = null
    var time: Long? = null
    var patientId: Long? = null
    var consultId: Long? = null
    var accepterName: String? = null
    var status //Sent, Delivered, Read
            : String? = null
}
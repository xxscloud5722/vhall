@file:Suppress("UNCHECKED_CAST", "unused")

package com.xxscloud.sdk.vhall

import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class VHall constructor(
        private val token: VHallToken
) {
    fun create(subject: String, startTime: Date, introduction: String = "", topics: String = ""): HashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["subject"] = subject
        request["start_time"] = startTime.time
        request["introduction"] = introduction
        request["topics"] = topics
        request["player"] = 2
        request["is_new_version"] = 1
        val result = HttpClient.post(token, "/webinar/create", request)
        return if ((result["code"] ?: "").toString() == "200") {
            val value = HashMap<String, Any>()
            value["id"] = result["data"].toString()
            value["url"] = "https://live.vhall.com/room/watch/" + result["data"]
            value
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }


    fun setCover(id: String, file: File): Boolean {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["image"] = file
        val result = HttpClient.post(token, "/webinar/activeimage", request)
        return if ((result["code"] ?: "").toString() == "200") {
            true
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }

    fun setDocument(id: String, file: File): Boolean {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["resfile"] = file
        val result = HttpClient.post(token, "/webinar/doc", request)
        return if ((result["code"] ?: "").toString() == "200") {
            true
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }

    fun remove(id: String): Boolean {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        val result = HttpClient.post(token, "/webinar/delete", request)
        return if ((result["code"] ?: "").toString() == "200") {
            true
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }

    fun getPushAddress(id: String): String {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        val result = HttpClient.post(token, "/webinar/get-stream-push-address", request)
        return if ((result["code"] ?: "").toString() == "200") {
            ((result["data"] as Map<*, *>)["RTMP_URL"] ?: "").toString()
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }

    fun getApplyList(id: String, index: Int = 1, size: Int = 1000): Map<String, Any> {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["pos"] = index
        request["limit"] = size
        val result = HttpClient.post(token, "/report/form", request)
        return if ((result["code"] ?: "").toString() == "200") {
            result["data"] as Map<String, Any>
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }
}
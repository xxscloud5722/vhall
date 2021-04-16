@file:Suppress("UNCHECKED_CAST", "unused")

package com.xxscloud.sdk.vhall

import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


/**
 * 微吼直播.
 * 文档地址: https://saas-doc.vhall.com/docs/show/947.
 * @property token VHallToken 微吼令牌.
 * @constructor 微吼SDK.
 */
class VHall constructor(
    private val token: VHallToken
) {

    /**
     * 获取直播列表.
     * @param position Long 直播列表条数.
     * @return LinkedHashMap<String, Any> 直播列表.
     */
    fun getList(position: Long = 0): LinkedHashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["pos"] = position
        request["limit"] = 16
        val result = HttpClient.post(token, "/v3/webinars/webinar/get-list", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return result["data"] as LinkedHashMap<String, Any>
    }

    /**
     * 创建直播.
     * @param subject String 标题.
     * @param startTime Date 开始时间.
     * @param type Int  直播类型 (1 音频 2 视频 3 互动).
     * @param introduction String 简介.
     * @param cover String 封面地址.
     * @return HashMap<String, Any> 直播间信息.
     */
    fun create(subject: String, startTime: Date, type: Int, introduction: String = "", cover: String = ""): HashMap<String, Any> {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val request = HashMap<String, Any>()
        request["subject"] = subject
        request["start_time"] = simpleDateFormat.format(startTime)
        request["introduction"] = introduction
        request["webinar_type"] = type
        if (cover.isNotEmpty()) {
            request["img_url"] = cover
        }

        val result = HttpClient.post(token, "/v3/webinars/webinar/create", request)
        return if ((result["code"] ?: "").toString() == "200") {
            val value = HashMap<String, Any>()
            value["id"] = (result["data"] as LinkedHashMap<String, Any>)["webinar_id"].toString()
            value["url"] = "https://live.vhall.com/v3/lives/watch/" + value["id"]
            value
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }

    /**
     * 修改直播信息.
     * @param id String 直播ID.
     * @param subject String 标题.
     * @param startTime Date 开始时间.
     * @param introduction String 简介.
     * @param cover String 封面地址.
     * @return HashMap<String, Any> 直播间信息.
     */
    fun update(id: String, subject: String, startTime: Date, introduction: String = "", cover: String = ""): Boolean {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["subject"] = subject
        request["start_time"] = simpleDateFormat.format(startTime)
        request["introduction"] = introduction
        if (cover.isNotEmpty()) {
            request["img_url"] = cover
        }

        val result = HttpClient.post(token, "/v3/webinars/webinar/edit", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return true
    }

    /**
     * 获取直播详情.
     * @param id String 直播ID.
     * @return LinkedHashMap<String, Any> 直播信息.
     */
    fun getDetail(id: String): LinkedHashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        val result = HttpClient.post(token, "/v3/webinars/webinar/info", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return result["data"] as LinkedHashMap<String, Any>
    }

    /**
     * 删除直播.
     * @param id String 直播ID.
     * @return Boolean 是否成功.
     */
    fun remove(id: String): Boolean {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        val result = HttpClient.post(token, "/v3/webinars/webinar/delete", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return true
    }

    /**
     * 获取推流地址
     * @param id String 直播ID.
     * @return String 地址.
     */
    fun getPushAddress(id: String): String {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        val result = HttpClient.post(token, "/v3/webinars/live/get-stream-push-address", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return (result["data"] as LinkedHashMap<String, Any>)["stream_address"].toString()
    }

    /**
     * 开始直播.
     * @param id String 直播ID.
     * @return Boolean 是否成功.
     */
    fun startLive(id: String): Boolean {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        val result = HttpClient.post(token, "/v3/webinars/live/start", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return true
    }

    /**
     * 结束直播.
     * @param id String 直播ID.
     * @return Boolean 是否成功.
     */
    fun endLive(id: String): Boolean {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        val result = HttpClient.post(token, "/v3/webinars/live/end", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return true
    }

    // ----

    /**
     * 获取角色信息.
     * @param id String 直播ID.
     * @param type Int 1-主持人，2-嘉宾，3-助理.
     * @return LinkedHashMap<String, Any> 角色信息.
     */
    fun getRoleInfo(id: String, type: Int): LinkedHashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["type"] = type
        val result = HttpClient.post(token, "/v3/webinars/live/get-role-url", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return result["data"] as LinkedHashMap<String, Any>
    }

    /**
     * 设置角色状态.
     * @param id String 直播ID.
     * @param status Int 是否开启 1 开启 0 关闭
     */
    fun setRoleStatus(id: String, status: Int): LinkedHashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["is_privilege"] = status
        val result = HttpClient.post(token, "/v3/webinars/privilege/open-privilege", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return result["data"] as LinkedHashMap<String, Any>
    }


    // -----

    /**
     * 获取回执信息.
     * @return LinkedHashMap<String, Any> 回执数据.
     */
    fun getCallbackInfo(): LinkedHashMap<String, Any> {
        val request = HashMap<String, Any>()
        val result = HttpClient.post(token, "/v3/users/callback-lists/get-info", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return result["data"] as LinkedHashMap<String, Any>
    }


    /**
     * 创建或者修改回执事件.
     * @param callbackUrl String 回执地址.
     * @param eventName String
     *   1、活动状态，开启后，直播开始或结束时进行通知.
     *   3、视频转码，开启后，JSSDK上传视频并转码成功进行通知.
     *   4、生成回放，开启后，直播结束并生成回放成功进行通知.
     *   5、回放分辨率，开启后，回放转码成功后支持获取不同分辨率.
     *   6、回放下载，开启后，回放下载成功进行通知.
     *   7、文档转码，开启后，文档上传并转码成功进行通知.
     *   8、裁剪回放，开启后，裁剪视频成功后进行通知.
     *   9、回放重制，开启后，裁剪视频成功后进行通知.
     * 多个之间用英文逗号隔开.
     * @param flag Boolean true 创建 false 修改.
     * @return Boolean 是否成功.
     */
    fun saveCallback(callbackUrl: String, eventName: String, flag: Boolean = true): Boolean {
        val request = HashMap<String, Any>()
        request["secret_key"] = token.secretKey
        request["callback_url"] = callbackUrl
        request["callback_event"] = eventName
        val result = HttpClient.post(token, if (flag) "/v3/users/callback-lists/create" else "/v3/users/callback-lists/edit", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return true
    }


    // -----

    /**
     * 获取用户观看流水.
     * @param id String 活动ID.
     * @param type Int 类型 1为直播，2为回放.
     * @param position Long 获取条目节点位置.
     */
    fun getReportWatchFlowList(id: String, type: Int, position: Long = 0): LinkedHashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["type"] = type
        request["pos"] = position
        request["limit"] = 300
        val result = HttpClient.post(token, "/v3/data-center/report/track", request)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return result["data"] as LinkedHashMap<String, Any>
    }

    // -----

    /**
     * 上传文件.
     * @param url String 文件地址.
     * @return String 微吼地址.
     */
    fun upload(url: String): String {
        val response = HttpClient.get(url) ?: throw IOException("read data is null")
        return upload(response, "png")
    }

    /**
     * 上传文件.
     * @param file File 文件.
     * @return String 微吼地址.
     */
    fun upload(file: ByteArray, type: String): String {
        val request = HashMap<String, Any>()
        request["resfile"] = file
        request["type"] = "image"
        val result = HttpClient.post(token, "/v3/commons/upload/index", request, type)
        if ((result["code"] ?: "").toString() != "200") {
            throw IOException((result["msg"] ?: "").toString())
        }
        return (result["data"] as LinkedHashMap<String, Any>)["domain_url"].toString()
    }

    // -----

    fun generateWatchUrl(id: String, userId: String = "", nickName: String = "默认"): String {
        val email = URLEncoder.encode("$userId@vhall.com", StandardCharsets.UTF_8.name())
        return "https://live.vhall.com/webinar/inituser/${id}?email=${email}&name=${URLEncoder.encode(nickName, StandardCharsets.UTF_8.name())}"
    }
}
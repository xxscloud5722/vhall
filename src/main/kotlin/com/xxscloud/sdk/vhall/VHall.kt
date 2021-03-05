@file:Suppress("UNCHECKED_CAST", "unused")

package com.xxscloud.sdk.vhall

import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class VHall constructor(
        private val token: VHallToken
) {

    /**
     * 创建直播.
     * @param subject String 标题.
     * @param startTime Date 开始时间.
     * @param introduction String 简介.
     * @param interact Int 直播类型.
     * @param topics String 话题.
     * @return HashMap<String, Any> 直播间信息.
     */
    fun create(subject: String, startTime: Date, introduction: String = "", interact: Int = 0, topics: String = ""): HashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["subject"] = subject
        request["start_time"] = startTime.time / 1000
        request["introduction"] = introduction
        request["topics"] = topics
        request["player"] = 2
        request["is_interact"] = interact
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


    /**
     * 设置封面.
     * @param id String 直播ID.
     * @param file File 文件.
     * @return Boolean 是否成功.
     */
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

    /**
     * 设置文档.
     * @param id String 直播ID.
     * @param file File 文件.
     * @return Boolean 是否成功.
     */
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

    /**
     * 删除直播.
     * @param id String 直播ID.
     * @return Boolean 是否成功.
     */
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

    /**
     * 获取推流地址
     * @param id String 直播ID.
     * @return String 地址.
     */
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

    /**
     * 获取报名列表.
     * @param id String 直播ID.
     * @param index Int 索引.
     * @param size Int 每页大小.
     * @return Map<String, Any>  报名数据.
     */
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

    /**
     * 获取观看流水.
     * @param id String 直播ID.
     * @param type Int 索引.
     * @param range Int 范围.
     * @param index Int 索引.
     * @param size Int 每个大小.
     * @return Map<String, Any> 报名数据.
     */
    fun getTrackList(id: String, type: Int, range: Int, index: Int = 1, size: Int = 1000): Map<String, Any> {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["type"] = type
        request["pos"] = index
        request["limit"] = size
        request["range"] = range
        val result = HttpClient.post(token, "/report/track", request)
        return if ((result["code"] ?: "").toString() == "200") {
            result["data"] as Map<String, Any>
        } else {
            throw  IOException((result["msg"] ?: "").toString())
        }
    }


    // ----

    /**
     * 添加用户.
     * @param id String 用户ID.
     * @param password String 密码.
     * @param email String 游戏地址.
     * @param nickName String 游戏地址.
     * @param avatarUrl String 游戏地址.
     * @return Boolean
     */
    fun addUser(id: String, password: String, email: String, nickName: String, avatarUrl: String): String {
        val request = HashMap<String, Any>()
        request["third_user_id"] = id
        request["pass"] = password
        request["email"] = email
        val result = HttpClient.post(token, "/user/register", request)
        return if ((result["code"] ?: "").toString() == "200") {
            (result["data"] as HashMap<String, Any>)["user_id"].toString()
        } else {
            ""
        }
    }

    /**
     *
     * @param id String
     * @param type Int 1助手 2嘉宾
     * @param password String
     * @param nickName String
     * @param avatarUrl String
     * @return Boolean
     */
    fun addRole(id: String, type: Int, password: String, nickName: String, avatarUrl: String): Boolean {
        val request = HashMap<String, Any>()
        request["webinar_id"] = id
        request["role_name"] = type
        request["users"] = this.addUser("v$id" + UUID.randomUUID().toString().substring(0, 4), password, "", nickName, avatarUrl)
        val result = HttpClient.post(token, "/guest/add-authorization", request)
        return (result["code"] ?: "").toString() == "200"
    }

}
@file:Suppress("UNCHECKED_CAST")

package com.xxscloud.sdk.vhall

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


object HttpClient {
    private val CLIENT = OkHttpClient.Builder().build()
    private val JSON_MAPPER: ObjectMapper = ObjectMapper()
    private const val BASE_URL = "https://saas-open.vhall.com"
    private val log = LoggerFactory.getLogger(HttpClient::class.java)


    fun post(token: VHallToken, url: String, request: HashMap<String, Any>, fileType: String = "png"): HashMap<String, Any> {
        val timestamp = System.currentTimeMillis().toString()
        val parameter = TreeMap<String, Any>()
        parameter["app_key"] = token.appKey
        parameter["sign_type"] = "0"
        parameter["signed_at"] = timestamp
        request.forEach { (k, v) ->
            parameter[k] = v
        }
        parameter["sign"] = getSign(token.secretKey, parameter)

        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
        parameter.forEach { (k, v) ->
            if (v is ByteArray) {
                body.addFormDataPart(k, UUID.randomUUID().toString() + "." + fileType, v.toRequestBody("application/octet-stream".toMediaType()))
            } else {
                body.addFormDataPart(k, v.toString())
            }
        }
        val requestUrl = BASE_URL + url
        parameter.remove("resfile")
        log.debug("[微吼] 地址: $requestUrl 请求参数: ${JSON_MAPPER.writeValueAsString(parameter)}")
        val response = CLIENT.newCall(Request.Builder().url(requestUrl).post(body.build()).build()).execute()
        val responseJson = response.body?.string() ?: throw IOException("response is null")
        log.debug("[微吼] 响应参数: $responseJson")
        return JSON_MAPPER.readValue(responseJson, HashMap::class.java) as HashMap<String, Any>
    }

    private fun getSign(secretKey: String, parameter: TreeMap<String, Any>): String {
        val value = StringBuilder(secretKey)
        parameter.forEach { (k, v) ->
            if (v !is ByteArray) {
                value.append(k).append(v)
            }
        }
        value.append(secretKey)
        return DigestUtils.md5Hex(value.toString())
    }

    fun get(url: String): ByteArray? {
        val response = CLIENT.newCall(Request.Builder().url(url).get().build()).execute()
        return response.body?.bytes()
    }
}
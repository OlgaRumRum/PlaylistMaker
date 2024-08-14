package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackRequest
import java.io.IOException

class RetrofitNetworkClient(private val trackService: TrackApi) : NetworkClient {


    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackRequest) {
                val resp = trackService.search(dto.expression).execute()
                val body = resp.body() ?: Response()
                body.apply { resultCode = resp.code() }
            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: IOException) {
            Response().apply {
                resultCode = 500
            }
        }
    }

}
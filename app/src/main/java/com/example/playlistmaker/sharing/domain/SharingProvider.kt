package com.example.playlistmaker.sharing.domain


interface SharingProvider {
    fun getShareAppLink(): String

    fun getSupportEmailData(): EmailData

    fun getTermsLink(): String
}
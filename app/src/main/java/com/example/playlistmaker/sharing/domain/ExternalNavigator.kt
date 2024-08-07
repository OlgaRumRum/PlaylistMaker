package com.example.playlistmaker.sharing.domain

interface ExternalNavigator {
    fun shareLink(shareText: String)
    fun openLink(termsLink: String)
    fun openEmail(supportEmailData: EmailData)
}
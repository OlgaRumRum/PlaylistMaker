package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.App
import com.example.playlistmaker.R

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return App.getStringFromResources(R.string.message_share)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            App.getStringFromResources(R.string.email_address),
            App.getStringFromResources(R.string.subject_line),
            App.getStringFromResources(R.string.context_of_the_message)
        )
    }

    private fun getTermsLink(): String {
        return App.getStringFromResources(R.string.user_agreement_button)
    }
}
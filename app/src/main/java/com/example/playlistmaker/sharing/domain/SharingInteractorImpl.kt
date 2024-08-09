package com.example.playlistmaker.sharing.domain

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingProvider: SharingProvider
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
        return sharingProvider.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return sharingProvider.getSupportEmailData()
    }

    private fun getTermsLink(): String {
        return sharingProvider.getTermsLink()
    }
}
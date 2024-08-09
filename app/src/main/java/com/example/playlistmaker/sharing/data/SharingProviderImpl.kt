package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.SharingProvider

class SharingProviderImpl(private val context: Context) : SharingProvider {
    override fun getShareAppLink(): String {
        return context.getString(R.string.message_share)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.email_address),
            context.getString(R.string.subject_line),
            context.getString(R.string.context_of_the_message)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.user_agreement_button)
    }
}
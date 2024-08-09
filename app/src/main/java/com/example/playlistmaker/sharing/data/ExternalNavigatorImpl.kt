package com.example.playlistmaker.sharing.data

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(private val application: Application) : ExternalNavigator {

    override fun shareLink(shareText: String) {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            application.startActivity(
                Intent.createChooser(this, null).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun openLink(termsLink: String) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(termsLink)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(browserIntent)
    }

    override fun openEmail(email: EmailData) {
        val supportIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            putExtra(Intent.EXTRA_SUBJECT, email.subject)
            putExtra(Intent.EXTRA_TEXT, email.text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        application.startActivity(supportIntent)
    }
}


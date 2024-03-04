package com.kamelia.hedera.mail

import com.kamelia.hedera.core.MailSendingException
import com.kamelia.hedera.core.PasswordResetMessagingException
import com.kamelia.hedera.plugins.toHTML
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.Environment.mailFrom
import com.kamelia.hedera.util.Environment.mailFromName
import com.kamelia.hedera.util.Environment.mailHost
import com.kamelia.hedera.util.Environment.mailPassword
import com.kamelia.hedera.util.Environment.mailPort
import com.kamelia.hedera.util.Environment.mailUseAuth
import com.kamelia.hedera.util.Environment.mailUseTLS
import com.kamelia.hedera.util.Environment.mailUsername
import com.kamelia.hedera.util.I18N
import io.ktor.server.freemarker.*
import jakarta.mail.Authenticator
import jakarta.mail.MessagingException
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MailService {

    private val props = Properties().apply {
        set("mail.smtp.host", mailHost)
        set("mail.smtp.port", mailPort)
        set("mail.smtp.auth", mailUseAuth)
        set("mail.smtp.starttls.enable", mailUseTLS)
    }
    private val session= Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(mailUsername, mailPassword)
        }
    })

    private suspend fun sendMail(
        to: String,
        subject: String,
        text: String,
        html: String? = null,
    ) = withContext(Dispatchers.IO) {
        try {
            val message = MimeMessage(session).apply {
                setFrom("$mailFromName <$mailFrom>")
                setRecipients(MimeMessage.RecipientType.TO, to)
                setSubject(subject)
                val bodyPart = MimeBodyPart()
                bodyPart.setContent(html ?: text, "text/html; charset=utf-8")
                bodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable")
                val multipart = MimeMultipart()
                multipart.addBodyPart(bodyPart)
                setContent(multipart)
            }
            Transport.send(message)
        } catch (e: MessagingException) {
            e.printStackTrace()
            throw MailSendingException()
        }
    }

    suspend fun sendResetPasswordMail(
        user: User,
        token: String,
    ) {
        val locale = Locale.Builder().setLanguageTag(user.settings.preferredLocale.name).build()


        with(I18N.withNewCurrentLocale(locale).section("mails.reset_password")) {
            val subject = t("subject")
            val html = FreeMarkerContent("mails/ResetPasswordRequest.ftl", mapOf(
                "URL" to Environment.URL,
                "subject" to t("subject"),
                "greetings" to t("greetings", mapOf("username" to user.username)),
                "paragraph1" to t("paragraph1"),
                "paragraph2" to t("paragraph2"),
                "paragraph3" to t("paragraph3"),
                "paragraph4" to t("paragraph4"),
                "button" to t("button"),
                "salutations" to t("salutations"),
                "footer1" to t("footer1", mapOf("email" to user.email)),
                "footer2" to t("footer2"),
                "token" to token,
            )).toHTML()

            try {
                sendMail(user.email, subject, html)
            } catch (e: MailSendingException) {
                throw PasswordResetMessagingException()
            }
        }
    }
}

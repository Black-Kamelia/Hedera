package com.kamelia.hedera.mail

import com.kamelia.hedera.core.HederaException
import com.kamelia.hedera.core.MailSendingException
import com.kamelia.hedera.core.PasswordResetMessagingException
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.util.Environment.mailFrom
import com.kamelia.hedera.util.Environment.mailFromName
import com.kamelia.hedera.util.Environment.mailHost
import com.kamelia.hedera.util.Environment.mailPassword
import com.kamelia.hedera.util.Environment.mailPort
import com.kamelia.hedera.util.Environment.mailUseAuth
import com.kamelia.hedera.util.Environment.mailUseTLS
import com.kamelia.hedera.util.Environment.mailUsername
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
        this["mail.smtp.host"] = mailHost
        this["mail.smtp.port"] = mailPort
        this["mail.smtp.auth"] = mailUseAuth
        this["mail.smtp.starttls.enable"] = mailUseTLS
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
        val subject = "Reset your password"
        val html = """
            <html>
                <body>
                    <h1>Reset your password</h1>
                    <p>Hello ${user.username},</p>
                    <p>
                        You have requested to reset your password.
                        Please click on the following link to reset your password:
                    </p>
                    <p><a href="http://localhost:3000/reset-password?token=$token">Reset password</a></p>
                    <p>Or</p>
                    <p>Input this token: <strong><pre>$token</pre></strong></p>
                    <p>If you did not request to reset your password, please ignore this email.</p>
                </body>
            </html>
        """.trimIndent()

        try {
            sendMail(user.email, subject, html)
        } catch (e: MailSendingException) {
            throw PasswordResetMessagingException()
        }
    }

}
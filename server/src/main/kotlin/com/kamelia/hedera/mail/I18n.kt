package com.kamelia.hedera.mail

import com.kamelia.hedera.rest.setting.Locale

val RESET_PASSWORD_VALUES = mapOf(
    Locale.fr to mapOf(
        "subject" to "Réinitialiser votre mot de passe",
        "greetings" to "Bonjour {username},",
        "paragraph1" to "Vous avez récemment demandé à réinitialiser le mot de passe de votre compte Hedera. Si ce n'était pas vous, merci d'ignorer ce message.",
        "paragraph2" to "Vous pouvez réinitialiser votre mot de passe en cliquant sur le bouton ci-dessous :",
        "paragraph3" to "Ou, vous pouvez copier et coller la clé de réinitialisation suivante dans le formulaire de réinitialisation du mot de passe :",
        "paragraph4" to "La clé de réinitialisation est valide pendant 10 minutes seulement.",
        "button" to "Réinitialiser le mot de passe",
        "salutations" to "Cordialement,",
        "footer1" to "Ce message a été envoyé à {email}. Si vous pensez avoir reçu cet message par erreur, veuillez l'ignorer et le supprimer.",
        "footer2" to "Ce message a été envoyé automatiquement, merci de ne pas répondre à ce message.",
    ),
    Locale.en to mapOf(
        "subject" to "Reset your password",
        "greetings" to "Hello {username},",
        "paragraph1" to "You recently requested to reset your Hedera account password. If this wasn't you, please ignore this message.",
        "paragraph2" to "You can reset your password by clicking the button below:",
        "paragraph3" to "Or, you can copy and paste the following reset key into the password reset form:",
        "paragraph4" to "The reset key is valid for 10 minutes only.",
        "button" to "Reset password",
        "salutations" to "Regards,",
        "footer1" to "This message was sent to {email}. If you think you received this message by mistake, please ignore and delete it.",
        "footer2" to "This message was sent automatically, please do not reply to this message.",
    ),
)

package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import javax.mail.internet.MimeMessage


@Service
class MailClient @Autowired constructor(val emailSender: JavaMailSender,
                                        val templateEngine: TemplateEngine) {

    private val LOG = LoggerFactory.getLogger(MailClient::class.java)

    fun sendWelcome(editedUser: User) {
        // TODO email sending must be done in asynch
        // if there is a switch in enabled from false -> true
        val messagePreparator = { mimeMessage : MimeMessage ->
            val messageHelper = MimeMessageHelper(mimeMessage)
            messageHelper.setTo(editedUser.login)
            messageHelper.setSubject("Welcome in Kilim")
            val content = formatWelcomeEmail()
            messageHelper.setText(content, true)
        }

        try {
            emailSender.send(messagePreparator)
        } catch (mex: MailException) {
            LOG.warn("Can't send email to new customer ", editedUser.login)
        }
    }

    private fun formatWelcomeEmail(): String {
        val context = Context()
        context.setVariable("moquette_link", "https://broker.moquette.io")
        return templateEngine.process("mailTemplate", context)
    }

}
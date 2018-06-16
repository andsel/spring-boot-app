package io.moquette.kilim.infrastructure


import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import io.moquette.kilim.model.User
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException
import javax.mail.MessagingException


@RunWith(SpringRunner::class)
@SpringBootTest
class MailClientTest {

    private var smtpServer: GreenMail? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        smtpServer = GreenMail(ServerSetup(2525, null, "smtp"))
        smtpServer!!.start()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        smtpServer!!.stop()
    }

    @Autowired
    private val mailClient: MailClient? = null

    @Test
    @Throws(Exception::class)
    fun shouldSendWelcomeMail() {
        //given
        val recipient = User(123, "john.doe@gmail.com", "password", "ROLE_USER", true, false)

        //when
        mailClient!!.sendWelcome(recipient)
        //then
        val welcomeMessage = "<span>Your registration request has been accepted, thanks a lot for your interest into " +
                "the Moquette MQTT Saas</span>"
        assertReceivedMessageContains(welcomeMessage)
    }

    @Throws(IOException::class, MessagingException::class)
    private fun assertReceivedMessageContains(expected: String) {
        val receivedMessages = smtpServer!!.getReceivedMessages()
        assertEquals(1, receivedMessages.size)
        val content = receivedMessages[0].content as String
        assertTrue(content.contains(expected))
    }
}
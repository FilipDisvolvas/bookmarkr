package net.sakrak.reactivebookmarks.services

import com.mongodb.reactivestreams.client.MongoClient
import net.sakrak.reactivebookmarks.dto.LoginUserDTO
import net.sakrak.reactivebookmarks.exceptions.ActiveUserNotFoundException
import net.sakrak.reactivebookmarks.exceptions.EmailAdressAlreadyExistsException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono


@DataMongoTest(includeFilters = [ComponentScan.Filter(Service::class), ComponentScan.Filter(Repository::class)])
class LoginUserServiceImplIT(@Autowired private val userService: LoginUserService) {
    val logger = LoggerFactory.getLogger(LoginUserServiceImplIT::class.java)

    @BeforeEach
    fun before(@Autowired mongoClient: MongoClient, @Value("\${spring.data.mongodb.database}") dbName: String) {
        mongoClient.getDatabase(dbName).drop().toMono().block()
    }

    @Test
    fun testChangeEmail() {
        val dto = LoginUserDTO(email = "test@invalid", pwhash = "12345", pwsalt = "sdfklh", isActive = true, verificationToken = "sdflkhsdflh")
        val changedEmail = "barfoo@invalid"

        val savedDto = userService.create(dto).block()
        val loadedUser = userService.findActiveById(savedDto!!.id!!).block()

        assertThat(loadedUser!!.email, equalTo(dto.email))

        loadedUser.email = changedEmail
        userService.update(loadedUser.id!!, loadedUser).block()

        val reloadedUser = userService.findActiveById(savedDto!!.id!!).block()
        assertThat(reloadedUser!!.email, equalTo(changedEmail))
    }

    @Test
    fun testFindActivated() {
        val verificationToken = "sdflkhsdflh"
        val dto = LoginUserDTO(email = "test@invalid", pwhash = "12345", pwsalt = "sdfklh", isActive = false, verificationToken = verificationToken)
/*
        val activatedUser = userService
            .create(dto)
            .publish {
                val foo = it
                userService.activate(dto.email, verificationToken) }
            .publish { userService.findActiveByEmail(dto.email) }
            .block()
 */
        userService.create(dto).block()
        userService.activate(dto.email, verificationToken).block()
        val activatedUser = userService.findActiveByEmail(dto.email).block()

        assertThat(activatedUser!!.email, equalTo(dto.email))
    }

    @Test
    fun testFindInactive() {
        val verificationToken = "sdflkhsdflh"
        val dto = LoginUserDTO(email = "test@invalid", pwhash = "12345", pwsalt = "sdfklh", isActive = false, verificationToken = verificationToken)

        assertThrows<ActiveUserNotFoundException> {
            userService
                .create(dto)
                .flatMap {
                    userService.findActiveByEmail(dto.email) }
                .block()
        }
    }

    @Test
    fun testDoubledRegistration() {
        val verificationToken = "sdflkhsdflh"
        val dto = LoginUserDTO(email = "test@invalid", pwhash = "12345", pwsalt = "sdfklh", isActive = false, verificationToken = verificationToken)
        val dto2 = LoginUserDTO(email = "test@invalid", pwhash = "sdfsdfsdf", pwsalt = "sdfklh", isActive = false, verificationToken = verificationToken)

        assertThrows<EmailAdressAlreadyExistsException> {
            logger.debug("{}", userService.create(dto).block())
            logger.debug("{}", userService.create(dto2).block())
        }
    }

}

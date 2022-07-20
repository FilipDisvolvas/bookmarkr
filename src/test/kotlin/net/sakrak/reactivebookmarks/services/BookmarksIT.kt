package net.sakrak.reactivebookmarks.services

import com.mongodb.reactivestreams.client.MongoClient
import net.sakrak.reactivebookmarks.dto.BookmarkDTO
import net.sakrak.reactivebookmarks.dto.BookmarkFolderDTO
import net.sakrak.reactivebookmarks.dto.LoginUserDTO
import net.sakrak.reactivebookmarks.exceptions.BookmarkFolderNotFoundException
import net.sakrak.reactivebookmarks.exceptions.BookmarkNotFoundException
import net.sakrak.reactivebookmarks.repositories.BookmarkFolderRepository
import net.sakrak.reactivebookmarks.repositories.BookmarkRepository
import net.sakrak.reactivebookmarks.repositories.LoginUserRepository
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono

@DataMongoTest(includeFilters = [ComponentScan.Filter(Service::class), ComponentScan.Filter(Repository::class)])
class BookmarksIT(
    /*
        FIXME: Beim Anfordern der Service-Instanzen fliegt eine ParameterResolutionException.
     */
    @Autowired private val userService: LoginUserService,
    @Autowired private val bookmarkFolderService: BookmarkFolderService,
    @Autowired private val bookmarkService: BookmarkService
) {
    private var loginUserDto: LoginUserDTO? = null
    private var testData: TestData? = null
    private var savedRootFolder: BookmarkFolderDTO? = null

    private val logger = LoggerFactory.getLogger(BookmarksIT::class.java)

    @BeforeEach
    fun before(@Autowired mongoClient: MongoClient, @Value("\${spring.data.mongodb.database}") dbName: String) {
        mongoClient.getDatabase(dbName).drop().toMono().block()

        loginUserDto = userService.create(
            LoginUserDTO(
                email = "test@invalid",
                pwhash = "12345",
                pwsalt = "sdfklh",
                isActive = true,
                verificationToken = "sdflkhsdflh"
            )
        ).block()

        testData = TestData(loginUserId = loginUserDto!!.id!!)

        savedRootFolder = bookmarkFolderService
            .createRecursive(loginUserDto!!, testData!!.rootFolder)
            .block()
    }

    @Test
    fun testEntitiesHasIds() {
        val savedSubfolder = savedRootFolder!!.children.first()

        assertThat(savedRootFolder!!.id, `is`(notNullValue()))
        assertThat(savedSubfolder.id, `is`(notNullValue()))
        assertThat(savedSubfolder.bookmarks.first().id, `is`(notNullValue()))
    }

    @Test
    fun testHasNoSideEffects() {
        val unsavedRootFolder = testData!!.rootFolder
        val unsavedSubfolder = unsavedRootFolder.children.first()

        // Keine Nebeneffekte! -- Das als Parameter verwendete Objekt wurde nicht verändert!
        assertThat(unsavedRootFolder.id, `is`(nullValue()))
        assertThat(unsavedSubfolder.id, `is`(nullValue()))
        assertThat(unsavedSubfolder.bookmarks.first().id, `is`(nullValue()))
    }

    @Test
    fun testUpdateFolder() {
        val modifiedFolder = savedRootFolder!!.copy(
            title = "sdflkhdsflkhsdf", description = "sadfoiar9awlkssd")

        bookmarkFolderService.update(loginUserDto!!, modifiedFolder).block()
        val savedModifiedFolder = bookmarkFolderService.findFolderById(loginUserDto!!, modifiedFolder.id!!).block()

        assertThat(savedModifiedFolder!!.title, equalTo(modifiedFolder.title))
        assertThat(savedModifiedFolder.description, equalTo(modifiedFolder.description))
    }

    @Test
    fun testUpdateFolderFails() {
        val modifiedFolder = savedRootFolder!!.copy(
            title = "sdflkhdsflkhsdf", description = "sadfoiar9awlkssd")

        assertThrows<BookmarkFolderNotFoundException> {
            bookmarkFolderService.update(loginUserDto!!.copy(id = "invalid"), modifiedFolder).block()
        }
    }


    @Test
    fun testReReadSorted() {
        // Und jetzt nochmal aus der Datenbank auslesen, was wir eben abgespeichert haben.
        val foundRootFolders = bookmarkFolderService
            .findRootFolders(loginUserDto!!)
            .collectList()
            .block()
        val foundSubfolder = foundRootFolders!!.first().children.first()

        assertThat(foundRootFolders.size, equalTo(1))
        assertThat(foundRootFolders.first().id, equalTo(savedRootFolder!!.id))

        //FIXME
        //assertThat(foundRootFolders!!.first().children.first().id, equalTo(rootFolder!!.children.first().id))
        assertThat(foundSubfolder.bookmarks.first().id, equalTo(savedRootFolder!!.children.first().bookmarks.first().id))

        val foundSubFolder: BookmarkFolderDTO? = bookmarkFolderService
            .findFolderById(loginUserDto!!, foundSubfolder.id!!)
            .block()

        assertThat(foundSubFolder!!.id, equalTo(foundSubfolder.id!!))
    }

    @Test
    fun testUpdateBookmark() {
        val touchedSubFolder = savedRootFolder!!.children.first()
        val touchedBookmark: BookmarkDTO = touchedSubFolder.bookmarks.first().copy(title = "sdsdfsdf")

        bookmarkService
            .update(loginUserDto!!.id!!, touchedSubFolder.id!!, touchedBookmark)
            .block()

        val foundSubFolder: BookmarkFolderDTO? = bookmarkFolderService
            .findFolderById(loginUserDto!!, touchedSubFolder.id!!)
            .block()
        val foundBookmark = foundSubFolder!!
            .bookmarks
            .filter { it.id!! == touchedBookmark.id }
            .firstOrNull()

        assertThat(foundBookmark!!.title, equalTo(touchedBookmark.title))
    }

    @Test
    fun testDeleteBookmark() {
        val touchedSubFolder = savedRootFolder!!.children.first()
        val deletionCandidate: BookmarkDTO = touchedSubFolder.bookmarks.first()

        bookmarkService
            .delete(loginUserDto!!.id!!, touchedSubFolder.id!!, deletionCandidate.id!!)
            .block()

        val foundSubFolder: BookmarkFolderDTO? = bookmarkFolderService.findFolderById(loginUserDto!!, touchedSubFolder.id!!).block()
        val foundBookmark: BookmarkDTO? = foundSubFolder!!.bookmarks.filter { it.id!! == deletionCandidate.id!! }.firstOrNull()

        assertThat(foundBookmark, `is`(nullValue()))
    }

    @Test
    fun testDeleteNonExistingBookmark() {
        val touchedSubFolder = savedRootFolder!!.children.first()

        assertThrows<BookmarkNotFoundException> {
            bookmarkService
                .delete(loginUserDto!!.id!!, touchedSubFolder.id!!, "invalid id")
                .block()
        }
    }

    @Test
    fun testInsertNewBookmark() {
        val newBookmark = BookmarkDTO(url = "http://www.example.com/", title = "Example", description = "", loginUserId = loginUserDto!!.id!!);

        bookmarkService
            .create(loginUserDto!!.id!!, savedRootFolder!!.id!!, newBookmark)
            .block()

        val foundRootFolders = bookmarkFolderService
            .findFolderById(loginUserDto!!, savedRootFolder!!.id!!)
            .block()

        assertThat(savedRootFolder!!.bookmarks.size, equalTo(0))
        assertThat(foundRootFolders!!.bookmarks.size, equalTo(1))
    }

    @Test
    fun testInsertNewBookmarkFailing() {
        val newBookmark = BookmarkDTO(url = "http://www.example.com/", title = "Example", description = "", loginUserId = loginUserDto!!.id!!);

        assertThrows<BookmarkFolderNotFoundException> {
            bookmarkService
                .create("inexistent", savedRootFolder!!.id!!, newBookmark)
                .block()
        }
    }


    data class TestData(val loginUserId: String) {
        val springInitializr = BookmarkDTO(
            loginUserId = loginUserId,
            url = "https://start.spring.io/",
            title = "Spring initializr",
            description = ""
        )
        val tutorialReactiveREST = BookmarkDTO(
            loginUserId = loginUserId,
            url = "https://spring.io/guides/gs/reactive-rest-service/",
            title = "Getting Started | Building a Reactive RESTful Web Service",
            description = ""
        )
        val tutorialMockMvc = BookmarkDTO(
            loginUserId = loginUserId,
            url = "https://spring.io/guides/gs/testing-web/",
            title = "Getting Started | Testing the Web Layer",
            description = ""
        )

        val springFolder = BookmarkFolderDTO(
            title = "Spring Framework",
            description = "API, Tutorials, Cheat Sheets",
            loginUserId = loginUserId,
            bookmarks = listOf(tutorialReactiveREST, springInitializr, tutorialMockMvc)
        )

        val rootFolder = BookmarkFolderDTO(
            title = "Java World",
            description = "Alles rund um Java-Entwicklung",
            loginUserId = loginUserId,
            children = listOf(springFolder)
        )
    }
}
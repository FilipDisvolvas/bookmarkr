package net.sakrak.reactivebookmarks.domain

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class UrlPartsTest {
    @Test
    fun testTwoUrlParts() {
        val urlParts = UrlParts.parseUrlParts("http://spring.io")
        assertThat(urlParts.hostName, equalTo("io.spring"))
        assertThat(urlParts.domainName, equalTo("io.spring"))
    }

    @Test
    fun testThreeUrlParts() {
        val urlParts = UrlParts.parseUrlParts("http://start.spring.io")
        assertThat(urlParts.hostName, equalTo("io.spring.start"))
        assertThat(urlParts.domainName, equalTo("io.spring"))
    }
}
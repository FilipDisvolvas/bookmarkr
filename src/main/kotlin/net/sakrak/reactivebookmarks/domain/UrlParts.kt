package net.sakrak.reactivebookmarks.domain

import java.net.URL

data class UrlParts(val hostName: String, val domainName: String) {
    companion object {
        fun parseUrlParts(url: String): UrlParts {
            val parsedUrl = URL(url)

            val hostNameParts = parsedUrl.host.split('.').reversed()

            val hostName = hostNameParts.joinToString(".")

            val domainName = hostNameParts.subList(0, 2).joinToString(".")

            return UrlParts(hostName, domainName)
        }
    }
}
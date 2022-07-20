package net.sakrak.reactivebookmarks.exceptions

import java.lang.RuntimeException

class BookmarkNotFoundException(override val message: String?) : RuntimeException() {
}
package net.sakrak.reactivebookmarks.exceptions

import java.lang.RuntimeException

class BookmarkFolderNotFoundException(override val message: String?) : RuntimeException() {
}
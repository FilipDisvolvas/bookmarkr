package net.sakrak.reactivebookmarks.services

import net.sakrak.reactivebookmarks.converters.BookmarkFolderConverter.bookmarkFolderToDTO
import net.sakrak.reactivebookmarks.converters.BookmarkFolderConverter.dtoToBookmarkFolder
import net.sakrak.reactivebookmarks.domain.Bookmark
import net.sakrak.reactivebookmarks.domain.BookmarkFolder
import net.sakrak.reactivebookmarks.dto.BookmarkFolderDTO
import net.sakrak.reactivebookmarks.dto.LoginUserDTO
import net.sakrak.reactivebookmarks.exceptions.BookmarkFolderNotFoundException
import net.sakrak.reactivebookmarks.repositories.BookmarkFolderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URL

@Service
class BookmarkFolderServiceImpl(
    @Autowired private val bookmarkFolderRepository: BookmarkFolderRepository
) : BookmarkFolderService {
    override fun findRootFolders(loginUserDTO: LoginUserDTO): Flux<BookmarkFolderDTO> {
        // TODO: Nur die Subfolder und die Bookmarks der Root Folder lesen -- nicht die Bookmarks der Subfolder: https://stackoverflow.com/questions/19389415/return-document-without-subdocuments-in-mongodb
        return sortBookmarksAndFindChildren(
            loginUserDTO,
            bookmarkFolderRepository.findByLoginUserIdAndParentIdIsNull(loginUserDTO.id!!)
        )
    }

    override fun findFolderById(loginUserDTO: LoginUserDTO, folderId: String): Mono<BookmarkFolderDTO> {
        return sortBookmarksAndFindChildren(
            loginUserDTO,
            bookmarkFolderRepository.findByLoginUserIdAndId(loginUserDTO.id!!, folderId)
        ).collectList().map { it.first() }
    }

    private fun sortBookmarksAndFindChildren(
        loginUserDTO: LoginUserDTO,
        bookmarkFolderFlux: Flux<BookmarkFolder>
    ): Flux<BookmarkFolderDTO> {
        return bookmarkFolderFlux
            .map {
                bookmarkFolderToDTO(it.copy(bookmarks = sortedBookmarks(it.bookmarks)))
            }
            .flatMap { folder ->
                bookmarkFolderRepository
                    .findByLoginUserIdAndParentId(loginUserDTO.id!!, folder.id!!)
                    .collectList()
                    .map {
                        val subFolders = it.map { bookmarkFolderToDTO(it) }
                        folder.copy(children = subFolders)
                    }
            }
    }

    override fun create(loginUserDTO: LoginUserDTO, bookmarkFolderDTO: BookmarkFolderDTO): Mono<BookmarkFolderDTO> {
        val folder = dtoToBookmarkFolder(bookmarkFolderDTO.copy(loginUserId = loginUserDTO.id!!))
        return bookmarkFolderRepository.save(folder).map { bookmarkFolderToDTO(it) }
    }

    override fun createRecursive(
        loginUserDTO: LoginUserDTO,
        bookmarkFolderDTO: BookmarkFolderDTO
    ): Mono<BookmarkFolderDTO> {
        val dtoWithBookmarkIds: BookmarkFolderDTO = bookmarkFolderDTO.copy(bookmarks = bookmarkFolderDTO.bookmarks.map { BookmarkService.bookmarkWithId(it) })

        return bookmarkFolderRepository
            .save(dtoToBookmarkFolder(dtoWithBookmarkIds))
            .flatMap { savedFolder ->
                val monos = bookmarkFolderDTO
                    .children
                    .map { it.copy(parentId = savedFolder.id) }
                    .map { createRecursive(loginUserDTO, it) }
                Flux.concat(monos).collectList().map {
                    bookmarkFolderToDTO(savedFolder).copy(children = it)
                }
            }
    }

    override fun update(loginUserDTO: LoginUserDTO, bookmarkFolderDTO: BookmarkFolderDTO): Mono<BookmarkFolderDTO> {
        return bookmarkFolderRepository
            .existsByLoginUserIdAndId(loginUserDTO.id!!, bookmarkFolderDTO.id!!)
            .flatMap { folderBelongsToUser ->
                if (folderBelongsToUser) {
                    val folder = dtoToBookmarkFolder(bookmarkFolderDTO.copy(loginUserId = loginUserDTO.id!!))
                    bookmarkFolderRepository.save(folder).map { bookmarkFolderDTO }
                } else {
                    Mono.defer { throw BookmarkFolderNotFoundException("${bookmarkFolderDTO.id} gehört nicht ${loginUserDTO.id}") }
                }
            }
    }

    override fun sortedBookmarks(bookmarks: List<Bookmark>): List<Bookmark> {
        val foo = bookmarks
            .map { Pair(URL(it.url), it) }
            .sortedWith { o1, o2 ->
                val hostName1 = o1.first.host
                val hostName2 = o2.first.host
                val sComp = hostName1.compareTo(hostName2)

                if (sComp != 0) {
                    sComp
                } else {
                    o1.second.title.compareTo(o2.second.title)
                }
            }
            .map { it.second }
        return foo
    }
}

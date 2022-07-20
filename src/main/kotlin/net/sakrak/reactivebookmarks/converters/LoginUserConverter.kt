package net.sakrak.reactivebookmarks.converters

import net.sakrak.reactivebookmarks.domain.LoginUser
import net.sakrak.reactivebookmarks.dto.LoginUserDTO

object LoginUserConverter {
    fun loginUserToDTO(loginUser: LoginUser) = LoginUserDTO(
        id = loginUser.id, email = loginUser.email, pwhash = loginUser.pwhash,
        pwsalt = loginUser.pwsalt, isActive = loginUser.isActive,
        verificationToken = loginUser.verificationToken, nextLoginAfter = loginUser.nextLoginAfter
    )

    fun dtoToLoginUser(loginUserDTO: LoginUserDTO) = LoginUser(
        id = loginUserDTO.id, email = loginUserDTO.email, pwhash = loginUserDTO.pwhash,
        pwsalt = loginUserDTO.pwsalt, isActive = loginUserDTO.isActive,
        verificationToken = loginUserDTO.verificationToken, nextLoginAfter = loginUserDTO.nextLoginAfter
    )
}

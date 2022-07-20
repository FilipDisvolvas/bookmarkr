package net.sakrak.reactivebookmarks.services

import net.sakrak.reactivebookmarks.dto.LoginUserDTO
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface LoginUserService  {
    fun findActiveById(id: String): Mono<LoginUserDTO>
    fun findActiveByEmail(email: String): Mono<LoginUserDTO>
    fun findByEmail(email: String): Mono<LoginUserDTO>
    fun activate(email: String, verificationToken: String): Mono<LoginUserDTO>
    fun create(dto: LoginUserDTO): Mono<LoginUserDTO>
    fun update(id: String, dto: LoginUserDTO): Mono<LoginUserDTO>
}
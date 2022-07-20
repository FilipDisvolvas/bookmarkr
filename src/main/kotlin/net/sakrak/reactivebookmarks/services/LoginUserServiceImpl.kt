package net.sakrak.reactivebookmarks.services

import net.sakrak.reactivebookmarks.converters.LoginUserConverter.dtoToLoginUser
import net.sakrak.reactivebookmarks.converters.LoginUserConverter.loginUserToDTO
import net.sakrak.reactivebookmarks.domain.LoginUser
import net.sakrak.reactivebookmarks.dto.LoginUserDTO
import net.sakrak.reactivebookmarks.exceptions.ActiveUserNotFoundException
import net.sakrak.reactivebookmarks.exceptions.EmailAdressAlreadyExistsException
import net.sakrak.reactivebookmarks.repositories.LoginUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class LoginUserServiceImpl(@Autowired private val loginUserRepository: LoginUserRepository) : LoginUserService {
    override fun findActiveById(id: String): Mono<LoginUserDTO> {
        return loginUserRepository.findByIdAndIsActiveIsTrue(id).map { loginUserToDTO(it) }
    }

    override fun findActiveByEmail(email: String): Mono<LoginUserDTO> {
        return loginUserRepository
            .findByEmailAndIsActiveIsTrue(email)
            .switchIfEmpty {
                Mono.defer { throw ActiveUserNotFoundException() }
            }
            .map { loginUserToDTO(it) }
    }

    override fun findByEmail(email: String): Mono<LoginUserDTO> {
        return loginUserRepository
            .findByEmail(email)
            .switchIfEmpty {
                Mono.defer { throw ActiveUserNotFoundException() }
            }
            .map { loginUserToDTO(it) }
    }



    override fun activate(email: String, verificationToken: String): Mono<LoginUserDTO> {
        return loginUserRepository
            .findByEmailAndIsActiveIsFalseAndVerificationToken(email, verificationToken)
            .flatMap {
                it.isActive = true
                loginUserRepository.save(it)
            }.map { loginUserToDTO(it) }
    }

    override fun create(dto: LoginUserDTO): Mono<LoginUserDTO> {
        val user = dtoToLoginUser(dto)

        /*
            Nicht die beste Lösung!

            Wenn ein Benutzer sich mit der selben E-Mail-Adresse nochmal anmelden
            möchte, dan würde MongoDB diesen in der Datenbank finden und das Passwort
            vom bestehenden Record und noch weitere Dinge ändern. Dies ist so, weil
            der LoginUser anhand der E-Mail-Adresse datenbankseitig als einzigartig
            indiziert wird und aus dem save() aus Versehen ein update() werden würde.
            Also prüfen wir zunächst, ob der Benutzer mit dieser E-Mail-Adresse
            bereits in der Datenbank hinterlegt ist. Wenn dem so ist, dann schmeißen
            wir eine Exception. Ein möglicher Angriffsvektor ist, dass die Anmeldung
            mit der selben E-Mail-Adresse concurrently stattfindet. Hierfür habe ich
            allerdings auch keine zufriedenstellende Lösung, die ohne Blocking auskommen
            würde.
         */

        return loginUserRepository
            .findByEmail(dto.email)
            .mapNotNull {
                throw EmailAdressAlreadyExistsException()

                /*
                    Kotlin ausgetrickst: Ich brauche den Rückgabe-Typ nochmal in switchIfEmpty.
                    Allerdings ändert sich der Typ auf "Nothing", wenn ich nur eine Exception
                    schmeiße. Also gebe ich genau das Objekt als Rückgabewert zurück, obwohl
                    dies eigentlich dead code ist und vermutlich auch vom Compiler wegoptimiert
                    wird. Allerdings beschwert er sich auch nicht, dass dies ein Fehler sein könnte...
                 */
                it
            }
            .switchIfEmpty {
                loginUserRepository.save(user)
            }.map {
                loginUserToDTO(it)
            }
    }

    override fun update(id: String, dto: LoginUserDTO): Mono<LoginUserDTO> {
        return loginUserRepository.findById(id).map {
            it.email = dto.email
            it.pwhash = dto.pwhash
            it
        }.flatMap {
            val foo: Mono<LoginUser> = loginUserRepository.save(it)
            foo
        }.map { loginUserToDTO(it) }
    }
}

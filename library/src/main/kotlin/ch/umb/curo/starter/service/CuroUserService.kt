package ch.umb.curo.starter.service

import ch.umb.curo.starter.models.response.CuroUser
import ch.umb.curo.starter.models.response.CuroUserResponse

interface CuroUserService {

    fun getUsers(emailLike: String,
                 lastnameLike: String,
                 firstnameLike: String,
                 memberOfGroup: ArrayList<String>,
                 attributes: ArrayList<String>): CuroUserResponse

    fun getUser(id: String): CuroUser
    fun getCurrentUser(): CuroUser

}

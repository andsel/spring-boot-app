package io.moquette.kilim.model

interface IAdminNotificator {

    fun newUserRegistered(user: User)
}
package com.dpworld.copilotworld.panel

import com.intellij.util.messages.Topic

interface AvatarUserDetailsNotifier {
    fun userDetailsObtained(userDetails: AvatarUserDetails?)

    companion object {
        @JvmStatic
        val AVATAR_USER_DETAILS_TOPIC =
            Topic.create("avatarUserDetails", AvatarUserDetailsNotifier::class.java)
    }
}
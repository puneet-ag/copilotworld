package com.dpworld.copilotworld.panel.converted

import com.intellij.util.messages.Topic

interface VisionUserDetailsNotifier {
    fun userDetailsObtained(userDetails: VisionUserDetails?)

    companion object {
        @JvmStatic
        val VISION_USER_DETAILS_TOPIC =
            Topic.create("visionUserDetails", VisionUserDetailsNotifier::class.java)
    }
}
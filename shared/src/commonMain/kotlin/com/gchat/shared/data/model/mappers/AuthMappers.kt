package com.gchat.shared.data.model.mappers

import com.gchat.shared.data.model.auth.AuthResponse
import com.gchat.shared.domain.model.auth.AuthResultDetails

fun AuthResponse.toAuthResultDetails(): AuthResultDetails {
    return AuthResultDetails(
        token = this.token
    )
}

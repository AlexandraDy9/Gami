package com.degree.gami.model.social

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class SocialAuthRequest(
        @NotEmpty
        var token: String? = null,
        @NotNull
        var network: SocialNetwork? = null
)
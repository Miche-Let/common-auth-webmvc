package com.michelet.common.auth.webmvc.internal;

import java.util.Set;

public record InternalTokenClaims(
        String issuer,
        Set<String> audience
) {
}

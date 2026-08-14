package vn.gaspro.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import vn.gaspro.api.repository.InvalidatedTokenRepository;

@Component
@RequiredArgsConstructor
public class CustomJwtValidator implements OAuth2TokenValidator<Jwt> {

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String jti = token.getId();
        
        // Nếu token ID nằm trong bảng invalidated_token -> token đã bị logout
        if (jti != null && invalidatedTokenRepository.existsById(jti)) {
            OAuth2Error error = new OAuth2Error("TOKEN_BLACKLISTED", "Token has been logged out", null);
            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
    }
}

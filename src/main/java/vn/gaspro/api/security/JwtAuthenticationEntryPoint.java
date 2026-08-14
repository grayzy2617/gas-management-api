package vn.gaspro.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.enums.ErrorCode;

import java.io.IOException;
import java.util.Collection;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        // Xử lý lỗi ném ra từ Nimbus JwtDecoder
        if (authException instanceof OAuth2AuthenticationException oauth2Exception) {
            OAuth2Error error = oauth2Exception.getError();
            if (error != null) {
                log.error("OAuth2 Error Code: {}", error.getErrorCode());
                // Các lỗi mặc định của Spring OAuth2 hoặc do custom validator ném ra
                switch (error.getErrorCode()) {
                    case "invalid_token":
                        // JwtTimestampValidator mặc định trả về invalid_token kèm mô tả có chứa "expired"
                        if (error.getDescription() != null && error.getDescription().toLowerCase().contains("expired")) {
                            errorCode = ErrorCode.TOKEN_EXPIRED;
                        } else {
                            errorCode = ErrorCode.INVALID_TOKEN;
                        }
                        break;
                    case "TOKEN_BLACKLISTED": // Mã lỗi do CustomJwtValidator của chúng ta ném ra
                        errorCode = ErrorCode.TOKEN_BLACKLISTED;
                        break;
                }
            }
        } else if (authException.getCause() instanceof JwtValidationException jwtValidationException) {
            // Lấy danh sách các lỗi trong JwtValidationException
            Collection<OAuth2Error> errors = jwtValidationException.getErrors();
            for (OAuth2Error error : errors) {
                if ("TOKEN_BLACKLISTED".equals(error.getErrorCode())) {
                    errorCode = ErrorCode.TOKEN_BLACKLISTED;
                    break;
                } else if (error.getDescription() != null && error.getDescription().toLowerCase().contains("expired")) {
                    errorCode = ErrorCode.TOKEN_EXPIRED;
                    break;
                } else {
                    errorCode = ErrorCode.INVALID_TOKEN;
                }
            }
        }

        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}

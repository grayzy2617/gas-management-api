package vn.gaspro.api.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.gaspro.api.dto.request.LoginRequest;
import vn.gaspro.api.dto.request.RefreshTokenRequest;
import vn.gaspro.api.dto.request.RegisterRequest;
import vn.gaspro.api.dto.response.AuthResponse;
import vn.gaspro.api.dto.response.UserResponse;
import vn.gaspro.api.entity.InvalidatedToken;
import vn.gaspro.api.entity.Role;
import vn.gaspro.api.entity.User;
import vn.gaspro.api.enums.ErrorCode;
import vn.gaspro.api.enums.RoleCode;
import vn.gaspro.api.exception.AppException;
import vn.gaspro.api.mapper.UserMapper;
import vn.gaspro.api.repository.InvalidatedTokenRepository;
import vn.gaspro.api.repository.RoleRepository;
import vn.gaspro.api.repository.UserRepository;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        RoleCode requestedRoleCode = request.getRoleCode() != null ? request.getRoleCode() : RoleCode.CUSTOMER;
        
        if (requestedRoleCode == RoleCode.ADMIN || requestedRoleCode == RoleCode.OPERATOR) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Role role = roleRepository.findByCode(requestedRoleCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = User.builder()
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new AppException(ErrorCode.WRONG_PASSWORD));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        String accessToken = generateToken(user, VALID_DURATION);
        String refreshToken = generateToken(user, REFRESHABLE_DURATION);

        return AuthResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .roleCode(user.getRole().getCode())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
            log.info("Token {} has been invalidated", jti);

        } catch (ParseException e) {
            log.error("Error while parsing token for logout", e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(request.getRefreshToken());
            
            // Verify signature
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            // Verify expiration
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiryTime.before(new Date())) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }

            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            
            // Check blacklist
            if (invalidatedTokenRepository.existsById(jti)) {
                throw new AppException(ErrorCode.TOKEN_BLACKLISTED);
            }

            // Lấy user ra
            String phone = signedJWT.getJWTClaimsSet().getSubject();
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Vô hiệu hóa (Blacklist) Refresh Token cũ này
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

            // Sinh cặp token mới
            String accessToken = generateToken(user, VALID_DURATION);
            String newRefreshToken = generateToken(user, REFRESHABLE_DURATION);

            return AuthResponse.builder()
                    .userId(user.getId())
                    .phone(user.getPhone())
                    .roleCode(user.getRole().getCode())
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .build();

        } catch (ParseException | JOSEException e) {
            log.error("Error while verifying refresh token", e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public UserResponse getMyProfile(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    private String generateToken(User user, long expirationMillis) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getPhone())
                .issuer("gaspro.vn")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(expirationMillis, ChronoUnit.MILLIS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getRole().getCode().name())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
}

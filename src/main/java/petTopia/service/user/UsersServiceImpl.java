package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import petTopia.dto.user.response.BindAccountResponse;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.repository.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UsersServiceImpl implements UsersService {
    private final UserRepository usersRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public BindAccountResponse bindOAuth2Account(Integer userId, User.Provider provider) {
        log.info("執行 OAuth2 帳號綁定 - 用戶ID: {}, 提供者: {}", userId, provider);

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用戶不存在"));

        user.setProvider(provider);     // 更新用戶的 OAuth2 提供者資訊
        user.setEmailVerified(true);    // OAuth2 登入的郵箱已驗證

        User updatedUser = usersRepository.save(user);

        String token = jwtUtil.generateToken(
                updatedUser.getEmail(),
                updatedUser.getId(),
                updatedUser.getUserRole().toString()
        );

        log.info("OAuth2 帳號綁定成功 - 用戶ID: {}", userId);

        return BindAccountResponse.builder()
                .success(true)
                .message("帳號綁定成功")
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getUserRole().toString())
                .provider(user.getProvider().toString())
                .build();
    }
} 
package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import petTopia.model.user.User;
import petTopia.repository.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UsersServiceImpl implements UsersService {
    private final UserRepository usersRepository;

    @Override
    public User findById(Integer id) {
        return usersRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void bindOAuth2Account(Integer userId, User.Provider provider) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用戶不存在"));

        // 更新用戶的 OAuth2 提供者資訊
        user.setProvider(provider);
        user.setEmailVerified(true);  // OAuth2 登入的郵箱已驗證

        usersRepository.save(user);
    }
} 
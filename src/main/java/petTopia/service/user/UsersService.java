package petTopia.service.user;

import petTopia.dto.user.response.BindAccountResponse;
import petTopia.model.user.User;

public interface UsersService {
    BindAccountResponse bindOAuth2Account(Integer userId, User.Provider provider);
}
package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import petTopia.service.user.MemberLoginService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/oauth2")
public class OAuth2RedirectController {
    private final MemberLoginService memberLoginService;

    @GetMapping("/callback")
    public RedirectView oauth2Callback(@RequestParam String email) {
        return memberLoginService.oauth2Callback(email);
    }
} 
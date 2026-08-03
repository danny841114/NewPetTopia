package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.user.request.*;
import petTopia.dto.user.response.*;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.repository.user.UserRepository;
import petTopia.model.user.Admin;
import petTopia.repository.user.AdminRepository;
import petTopia.model.user.Member;
import petTopia.repository.user.MemberRepository;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 管理員登入
    public LoginResponse adminLogin(LoginRequest request) throws AuthenticationException {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email or password is null");
        }

        // 查找管理員帳號
        User adminUser = userRepository.findByEmailAndUserRole(request.getEmail(), User.UserRole.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Admin User not found"));

        // 驗證密碼
        boolean isPasswordValid = passwordEncoder.matches(request.getPassword(), adminUser.getPassword());
        if (!isPasswordValid) throw new AuthenticationException("Password not match");

        // 生成 JWT
        String token = jwtUtil.generateToken(request.getEmail(), adminUser.getId(), "ADMIN");

        return LoginResponse.builder()
                .message("登入成功")
                .token(token)
                .adminId(adminUser.getId())
                .role("ADMIN")
                .isAuthenticated(true)
                .build();
    }

    // 停用/啟用用戶
    @Transactional
    public void toggleUserStatus(Integer userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setEmailVerified(isActive);  // 使用 emailVerified 作為啟用狀態
        userRepository.save(user);
    }

    // 獲取所有會員（支援分頁、搜尋和篩選）
    public MemberPageResponse getAllMembersWithFilters(MemberSearchRequest request) {
        // 獲取所有會員
        List<User> allMembers = userRepository.findByUserRole(User.UserRole.MEMBER);

        // 根據條件過濾
        List<User> filteredMembers = allMembers.stream()
                .filter(member -> checkKeyword(member, request.getKeyword()))
                .filter(member -> checkStatus(member, request.getStatus()))
                .filter(member -> checkEmail(member, request.getEmail()))
                .collect(Collectors.toList());

        // 獲取會員詳細資訊
        List<UserDetail> memberDetails = filteredMembers.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());

        // 計算分頁
        int totalElements = memberDetails.size();
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());
        int startIndex = request.getPage() * request.getSize();
        int endIndex = Math.min(startIndex + request.getSize(), totalElements);

        List<UserDetail> pageContent = memberDetails.subList(startIndex, endIndex);

        return MemberPageResponse.builder()
                .content(pageContent)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(request.getPage())
                .build();
    }

    // 新增會員
    @Transactional
    public User createMember(CreateMemberRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email or password is null");
        }

        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("該電子郵件已被使用");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserRole(User.UserRole.MEMBER);
        user.setEmailVerified(true);
        user.setProvider(User.Provider.LOCAL);
        user.setLocalEnabled(true);

        User savedUser = userRepository.save(user);

        Member member = new Member();
        member.setUser(savedUser);
        member.setStatus(true);
        member.setUpdatedDate(LocalDateTime.now());
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        if (request.getBirthdate() != null) member.setBirthdate(LocalDate.parse(request.getBirthdate()));
        if (request.getGender() != null) member.setGender(request.getGender());

        memberRepository.save(member);

        return savedUser;
    }

    // 刪除會員
    @Transactional
    public void deleteMember(Integer memberId) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("找不到該會員"));

        if (user.getUserRole() != User.UserRole.MEMBER) {
            throw new RuntimeException("該用戶不是會員");
        }

        // 刪除會員記錄
        memberRepository.deleteByUserId(memberId);
        // 刪除用戶記錄
        userRepository.delete(user);
    }

    // 批量更新會員狀態
    @Transactional
    public void batchUpdateMemberStatus(BatchUpdateMembersRequest request) {
        boolean isActive = "activate".equals(request.getAction());

        List<Integer> memberIds = request.getMemberIds();
        for (Integer memberId : memberIds) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("找不到會員 ID: " + memberId));

            if (user.getUserRole() != User.UserRole.MEMBER) {
                throw new RuntimeException("用戶 ID: " + memberId + " 不是會員");
            }

            user.setEmailVerified(isActive);
        }

        // TODO: Error may happen
        userRepository.saveAll(userRepository.findAllById(memberIds));
    }

    @Transactional
    public void createSuperAdmin(String saEmail) throws BadRequestException {
        Optional<User> userOptional = userRepository.findByEmailAndUserRole(saEmail, User.UserRole.ADMIN);
        if (userOptional.isPresent() && userOptional.get().getIsSuperAdmin()) {
            throw new BadRequestException("Super admin is existing");
        }

        User superAdmin = new User();
        superAdmin.setEmail(saEmail);
        superAdmin.setPassword(passwordEncoder.encode("test123"));
        superAdmin.setUserRole(User.UserRole.ADMIN);
        superAdmin.setEmailVerified(true);
        superAdmin.setIsSuperAdmin(true);
        superAdmin.setAdminLevel(1);
        superAdmin.setProvider("LOCAL");
        superAdmin.setLocalEnabled(true);
        superAdmin.setUserRole(User.UserRole.ADMIN);
        superAdmin.setIsSuperAdmin(true);
        superAdmin.setAdminLevel(1);

        User savedUserAdmin = userRepository.save(superAdmin);

        Admin adminRecord = new Admin();
        adminRecord.setUsers(savedUserAdmin);
        adminRecord.setName("Super Admin");
        adminRecord.setRole(Admin.AdminRole.SA);
        adminRecord.setRegistrationDate(LocalDateTime.now());

        adminRepository.save(adminRecord);
    }

    public Dashboard getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<User> members = userRepository.findByUserRole(User.UserRole.MEMBER);
        List<User> vendors = userRepository.findByUserRole(User.UserRole.VENDOR);

        Dashboard.AdminInfo adminInfo = Dashboard.AdminInfo.builder()
                .id(userDetails.getUsername())
                .email(userDetails.getUsername())
                .role("ADMIN")
                .build();

        return Dashboard.builder()
                .members(members)
                .vendors(vendors)
                .adminInfo(adminInfo)
                .build();
    }

    public Map<String, Object> verifyLoginStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());

        if (isAuthenticated) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 檢查是否為管理員
            boolean isAdminRole = userDetails.getAuthorities()
                    .stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (isAdminRole) {
                return Map.of(
                        "isLoggedIn", true,
                        "adminId", userDetails.getUsername(),
                        "email", userDetails.getUsername(),
                        "role", "ADMIN"
                );
            }
        }

        return Map.of("isLoggedIn", false);
    }

    public AdminResponse getCurrentAdminInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UsernameNotFoundException("Authentication failed");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User adminUser = userRepository.findByEmailAndUserRole(email, User.UserRole.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Admin adminRecord = adminRepository.findById(adminUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));

        return AdminResponse.fromEntity(adminUser, adminRecord);
    }

    public MemberResponse getMemberById(Integer memberId) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getUserRole() != User.UserRole.MEMBER) {
            throw new IllegalArgumentException("Role of this user is not MEMBER");
        }

        Member member = memberRepository.findByUserId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        return MemberResponse.fromEntity(user, member);
    }

    @Transactional
    public void updateMember(Integer memberId, UpdateMemberRequest request) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getUserRole() != User.UserRole.MEMBER) {
            throw new IllegalArgumentException("Role of this user is not MEMBER");
        }

        Member member = memberRepository.findByUserId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        if (request.getName() != null) member.setName(request.getName());
        if (request.getPhone() != null) member.setPhone(request.getPhone());
        if (request.getAddress() != null) member.setAddress(request.getAddress());
        if (request.getEmailVerified() != null) user.setEmailVerified(request.getEmailVerified());

        if (request.getBirthdate() != null && !request.getBirthdate().trim().isEmpty()) {
            String birthdateStr = request.getBirthdate();
            try {
                LocalDate birthdate = LocalDate.parse(birthdateStr);
                member.setBirthdate(birthdate);
            } catch (DateTimeParseException e) {
                log.error("Birthdate string is invalid");
            }
        }

        memberRepository.save(member);
        userRepository.save(user);
    }

    private boolean checkKeyword(User member, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            String searchStr = keyword.toLowerCase();
            return String.valueOf(member.getId()).contains(searchStr)
                    || member.getEmail().toLowerCase().contains(searchStr);
        }
        return true;
    }

    private boolean checkStatus(User member, String status) {
        if (status != null && !status.isEmpty()) {
            return member.isEmailVerified() == "active".equals(status);
        }
        return true;
    }

    private boolean checkEmail(User member, String email) {
        if (email != null && !email.isEmpty()) {
            return member.getEmail().toLowerCase().contains(email.toLowerCase());
        }
        return true;
    }

    private UserDetail fromEntity(User user) {
        Member member = memberRepository.findByUserId(user.getId()).orElse(null);

        return UserDetail.builder()
                .id(user.getId())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .name(member != null ? member.getName() : "")
                .phone(member != null ? member.getPhone() : "")
                .updatedDate(member != null ? member.getUpdatedDate() : null)
                .build();
    }
}
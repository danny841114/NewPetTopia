package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.user.request.BatchUpdateMembersRequest;
import petTopia.dto.user.request.CreateMemberRequest;
import petTopia.dto.user.request.LoginRequest;
import petTopia.dto.user.request.MemberSearchRequest;
import petTopia.dto.user.response.MemberPageResponse;
import petTopia.dto.user.response.UserDetail;
import petTopia.model.user.User;
import petTopia.repository.user.UserRepository;
import petTopia.model.user.Admin;
import petTopia.repository.user.AdminRepository;
import petTopia.model.user.Member;
import petTopia.repository.user.MemberRepository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 管理員登入
    public User adminLogin(LoginRequest request) {
        // 查找管理員帳號
        User admin = userRepository.findByEmailAndUserRole(request.getEmail(), User.UserRole.ADMIN);

        // 驗證密碼
        if (admin != null && passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return admin;
        }

        return null;
    }

    // 獲取所有會員
    public List<User> getAllMembers() {
        return userRepository.findByUserRole(User.UserRole.MEMBER);
    }

    // 獲取所有商家
    public List<User> getAllVendors() {
        return userRepository.findByUserRole(User.UserRole.VENDOR);
    }

    // 停用/啟用用戶
    @Transactional
    public void toggleUserStatus(Integer userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用戶不存在"));
        user.setEmailVerified(isActive);  // 使用 emailVerified 作為啟用狀態
        userRepository.save(user);
    }

    // 創建管理員帳號
    @Transactional
    public User createAdmin(User admin, boolean isSuperAdmin) {
        // 設置用戶角色和權限
        admin.setUserRole(User.UserRole.ADMIN);
        admin.setIsSuperAdmin(isSuperAdmin);
        admin.setAdminLevel(isSuperAdmin ? 1 : 0);

        // 保存用戶記錄
        User savedUser = userRepository.save(admin);

        // 創建並保存管理員記錄
        Admin adminRecord = new Admin();
        adminRecord.setUsers(savedUser);
        adminRecord.setName(isSuperAdmin ? "Super Admin" : "Admin");
        adminRecord.setRole(isSuperAdmin ? Admin.AdminRole.SA : Admin.AdminRole.ADMIN);
        adminRecord.setRegistrationDate(LocalDateTime.now());

        adminRepository.save(adminRecord);

        return savedUser;
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

        userRepository.saveAll(userRepository.findAllById(memberIds));
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
package petTopia.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringHelper {
    /**
     * 檢查名稱是否為郵箱格式
     */
    public static boolean isEmailFormat(String name, String email) {
        if (name == null || email == null) return false;

        // 最基本的判斷：名稱與郵箱完全相同
        if (name.equalsIgnoreCase(email)) return true;

        // 更進階的判斷：名稱是否符合郵箱格式 (包含 @ 和 .)
        return name.matches("^[^@]+@[^@]+\\.[^@]+$");
    }
}

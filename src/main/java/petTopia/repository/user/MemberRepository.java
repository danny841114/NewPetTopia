package petTopia.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petTopia.model.user.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUserId(Integer userId);

    // clear persistence context automatically after executing SQL
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Member m WHERE m.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}

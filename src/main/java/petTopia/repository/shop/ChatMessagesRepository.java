package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petTopia.model.shop.ChatMessages;

public interface ChatMessagesRepository extends JpaRepository<ChatMessages, Integer> {
    // TODO: 會漏掉單向對話
    List<ChatMessages> findBySenderIdAndReceiverIdOrderByIdAsc(Integer senderId, Integer receiverId);

    @Query(value = """
                SELECT sender_id FROM chat_messages WHERE receiver_id = :userId
                UNION
                SELECT receiver_id FROM chat_messages WHERE sender_id = :userId
            """, nativeQuery = true)
    List<Integer> findDistinctChatUserIds(@Param("senderId") Integer senderId);
}

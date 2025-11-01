package ben.back_end.repository;

import ben.back_end.entity.Ranks;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RankRepository extends JpaRepository<Ranks, Integer> {

    @Query(value = """
        SELECT r.*
        FROM ranks r
        JOIN sub_events se ON r.sub_eventId = se.sub_eventId
        WHERE se.sub_eventName = :sub_eventName AND r.type = :type
        ORDER BY pos ASC
        """, nativeQuery = true)
    List<Ranks> findBySubEventName(@Param("sub_eventName") String sub_eventName,
                                   @Param("type") String type);

    @Query(value = """
        SELECT u.userName, t.teamName, SUM(r.pts) AS pts
        FROM ranks r
        JOIN users u ON r.userId = u.userId
        JOIN teams t ON r.teamId = t.teamId
        JOIN sub_events se ON r.sub_eventId = se.sub_eventId
        JOIN events e ON se.eventId = e.eventId
        WHERE e.eventName = :eventName
        GROUP BY u.userName, t.teamName
        ORDER BY pts DESC
        """, nativeQuery = true)
    List<Object[]> findUserTotalRankByEventName(@Param("eventName") String eventName);


    @Query(value = """
        SELECT t.teamName, SUM(r.pts) AS pts
        FROM ranks r
        JOIN teams t ON r.teamId = t.teamId
        JOIN sub_events se ON r.sub_eventId = se.sub_eventId
        JOIN events e ON se.eventId = e.eventId
        WHERE e.eventName = :eventName
        GROUP BY t.teamName
        ORDER BY pts DESC
        """, nativeQuery = true)
    List<Object[]> findTeamTotalRankByEventName(@Param("eventName") String eventName);


    @Query(value = "SELECT type FROM events WHERE eventName = :eventName", nativeQuery = true)
    String findEventTypeByName(@Param("eventName") String eventName);
}

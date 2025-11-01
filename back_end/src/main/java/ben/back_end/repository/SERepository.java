package ben.back_end.repository;

import ben.back_end.entity.Sub_Events;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SERepository extends JpaRepository<Sub_Events, Integer> {
    @Query(value = "SELECT * FROM sub_events where sub_eventName = :sub_eventName", nativeQuery = true)
    Sub_Events findBySEName(@Param("sub_eventName") String sub_eventName);

    @Query(value = "SELECT * FROM sub_events where eventId = :eventId", nativeQuery = true)
    List<Sub_Events> findByEventId(@Param("eventId") int eventId);

    @Query(value = """
            SELECT eventName
            FROM sub_events
            LEFT JOIN events ON sub_events.eventId = events.eventId
            WHERE (sub_events.sub_eventName = :sub_eventName)
            """, nativeQuery = true)
    String findEventNameBySEName(String sub_eventName);
}

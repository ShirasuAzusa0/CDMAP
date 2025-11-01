package ben.back_end.repository;

import ben.back_end.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Events, Long> {
    @Query(value = """
            SELECT events.*
            FROM events
            LEFT JOIN categories ON events.catId = categories.catId
            WHERE (categories.catName = :catName OR :catName IS NULL OR :catName = '')
            """, nativeQuery = true)
    List<Events> findByCatName(String catName);

    @Query(value = """
            SELECT events.*
            FROM events
            WHERE events.eventName = :eventName
            """, nativeQuery = true)
    Events getEventByEventName(String eventName);
}

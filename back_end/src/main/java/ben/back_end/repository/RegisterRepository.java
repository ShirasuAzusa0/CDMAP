package ben.back_end.repository;

import ben.back_end.entity.Registers;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegisterRepository extends JpaRepository<Registers, Integer> {
    @Query(value = """
            SELECT registers.*
            FROM registers
            LEFT JOIN users ON registers.userId = users.userId
            LEFT JOIN teams ON registers.teamId = teams.teamId
            LEFT JOIN sub_events ON registers.sub_eventId = sub_events.sub_eventId
            LEFT JOIN cars ON registers.carId = cars.carId
            WHERE registers.status = :status
            """, nativeQuery = true)
    List<Registers> findByStatus(@Param("status") String status);
}

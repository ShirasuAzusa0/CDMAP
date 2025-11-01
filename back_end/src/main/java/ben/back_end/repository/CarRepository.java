package ben.back_end.repository;

import ben.back_end.entity.Cars;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarRepository extends JpaRepository<Cars, Integer> {
    @Query(value = "SELECT * FROM cars where carName = :carName", nativeQuery = true)
    Cars findByCarName(@Param("carName") String carName);

    @Query(value = """
            SELECT cars.carName
            FROM cars
            WHERE cars.eventId = :eventId
            """, nativeQuery = true)
    List<String> findCatNamesByEventId(@Param("eventId") int eventId);
}

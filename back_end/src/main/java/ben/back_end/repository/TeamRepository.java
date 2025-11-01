package ben.back_end.repository;

import ben.back_end.entity.Teams;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Teams, Integer> {
    @Query(value = "SELECT * FROM teams where teamName = :teamName", nativeQuery = true)
    Teams findByTeamName(@Param("teamName") String teamName);
}

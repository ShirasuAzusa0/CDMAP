package ben.back_end.repository;

import ben.back_end.entity.Rewards_Punishments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RPRepository extends JpaRepository<Rewards_Punishments, Integer> {
}

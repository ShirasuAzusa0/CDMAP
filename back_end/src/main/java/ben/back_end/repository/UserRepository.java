package ben.back_end.repository;

import ben.back_end.entity.Rewards_Punishments;
import ben.back_end.entity.Teams;
import ben.back_end.entity.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Integer> {
    @Query(value = "SELECT * FROM users where email = :email", nativeQuery = true)
    Users findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users where userId = :userId", nativeQuery = true)
    Users findByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT * FROM users where userName = :userName", nativeQuery = true)
    Users findByUserName(@Param("userName") String userName);

    @Query(value = """
        SELECT rp.*
        FROM rewards_punishments rp
        WHERE rp.userId = :userId
        """, nativeQuery = true)
    List<Rewards_Punishments> findRPList(@Param("userId") int userId);

    @Query(value = """
        SELECT t.*
        FROM teams t
        JOIN team_members tm ON t.teamId = tm.teamId
        WHERE tm.userId = :userId
        """, nativeQuery = true)
    List<Teams> findTeamsByUserId(@Param("userId") Integer userId);

    @Query(value = """
        SELECT users.*
        FROM users
        ORDER BY users.rating DESC LIMIT 100
        """, nativeQuery = true)
    List<Users> findAllOrderByRating();
}
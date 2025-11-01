package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Integer userId;

    @Column(name = "userName", nullable = false)
    private String userName;

    @Column(name = "avatar", nullable = false)
    private String avatar;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "realName", nullable = false)
    private String realName;

    @Column(name = "identify", nullable = false)
    private String identify;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "firstRace", nullable = false)
    private LocalDateTime firstRace;

    @Column(name = "firstPodium", nullable = false)
    private LocalDateTime firstPodium;

    @Column(name = "firstPole", nullable = false)
    private LocalDateTime firstPole;

    @Column(name = "firstWin", nullable = false)
    private LocalDateTime firstWin;

    @Column(name = "totalRace", nullable = false)
    private Integer totalRace;

    @Column(name = "totalPodium", nullable = false)
    private Integer totalPodium;

    @Column(name = "totalPole", nullable = false)
    private Integer totalPole;

    @Column(name = "totalWin", nullable = false)
    private Integer totalWin;

    @Column(name = "registerTime", nullable = false)
    private LocalDateTime registerTime;

    @Column(name = "lastConnected", nullable = false)
    private LocalDateTime lastConnected;

    @Column(name = "type", columnDefinition = "ENUM('ADMIN', 'USER')")
    @Enumerated(EnumType.STRING)
    private userType type;

    @OneToMany(mappedBy = "userId")
    private List<team_members> teamMemberships;
}
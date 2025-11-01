package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "rewards_punishments")
public class Rewards_Punishments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rpId", nullable = false)
    private int rpId;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Teams team;

    @ManyToOne
    @JoinColumn(name = "sub_eventId", referencedColumnName = "sub_eventId", nullable = false)
    private Sub_Events subEvent;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private rpType type;

    @Column(name = "description", nullable = false)
    private String description;
}

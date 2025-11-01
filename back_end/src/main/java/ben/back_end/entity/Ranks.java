package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "ranks")
public class Ranks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rankId", nullable = false)
    private int rankId;

    @Column(name = "pos", nullable = false)
    private int pos;

    @Column(name = "pts", nullable = false)
    private int pts;

    @Column(name = "type", nullable = false,  columnDefinition = "enum('PRACTICE', 'QUALIFY', 'RACE')")
    @Enumerated(EnumType.STRING)
    private rankType type;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Teams team;

    @ManyToOne
    @JoinColumn(name = "sub_eventId", referencedColumnName = "sub_eventId", nullable = false)
    private Sub_Events subEvent;
}

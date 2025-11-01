package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventId")
    private int eventId;

    @Column(name = "eventName", nullable = false)
    private String eventName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "linkURL", nullable = false)
    private String linkURL;

    @Column(name = "eventAvatar", nullable = false)
    private String eventAvatar;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "type", nullable = false, columnDefinition = "enum('INDIVIDUAL','TEAM')")
    @Enumerated(EnumType.STRING)
    private eventType type;

    @Column(name = "firstEvent", nullable = false)
    private LocalDateTime firstEvent;

    @Column(name = "lastEvent", nullable = false)
    private LocalDateTime lastEvent;

    @Column(name = "status", nullable = false, columnDefinition = "enum('ENDED', 'ONGOING', 'UPDATED')")
    @Enumerated(EnumType.STRING)
    private eventStatus status;

    @Column(name = "num", nullable = false)
    private int num;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catId", referencedColumnName = "catId", nullable = false)
    private Categories cat;
}

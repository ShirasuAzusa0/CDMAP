package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "sub_events")
public class Sub_Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_eventId", nullable = false)
    private int sub_eventId;

    @Column(name = "sub_eventName", nullable = false)
    private String sub_eventName;

    @Column(name = "sub_eventStart", nullable = false)
    private LocalDateTime sub_eventStart;

    @Column(name = "sub_eventEnd", nullable = false)
    private LocalDateTime sub_eventEnd;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "registerEndDate", nullable = false)
    private LocalDateTime registerEndDate;

    @Column(name = "status", nullable = false, columnDefinition = "enum('REGISTER_ONGOING', 'REGISTER_ENDED', 'ENDED', 'ONGOING', 'UPDATED')")
    @Enumerated(EnumType.STRING)
    private sub_eventStatus status;

    @Column(name = "sub_eventType", nullable = false, columnDefinition = "enum('INDIVIDUAL','TEAM')")
    @Enumerated(EnumType.STRING)
    private eventType sub_eventType;

    @ManyToOne
    @JoinColumn(name = "eventId", referencedColumnName = "eventId", nullable = false)
    private Events event;
}

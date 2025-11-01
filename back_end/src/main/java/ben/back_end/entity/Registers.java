package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "registers")
public class Registers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registerId", nullable = false)
    private int registerId;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Teams team;

    @ManyToOne
    @JoinColumn(name = "sub_eventId", referencedColumnName = "sub_eventId", nullable = false)
    private Sub_Events subEvent;

    @ManyToOne
    @JoinColumn(name = "carId", referencedColumnName = "carId", nullable = false)
    private Cars car;

    @Column(name = "status", nullable = false,  columnDefinition = "enum('UNCONFIRMED', 'CONFIRMED', 'REJECTED')")
    @Enumerated(EnumType.STRING)
    private registerStatus status;

    @Column(name = "description", nullable = false)
    private String description;
}

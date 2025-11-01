package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "cars")
public class Cars {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carId", nullable = false)
    private int carId;

    @Column(name = "carName", nullable = false)
    private String carName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", referencedColumnName = "eventId", nullable = false)
    private Events events;
}

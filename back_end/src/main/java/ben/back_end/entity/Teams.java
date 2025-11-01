package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "teams")
public class Teams {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamId", nullable = false)
    private int teamId;

    @Column(name = "teamName", nullable = false)
    private String teamName;

    @Column(name = "teamLogo", nullable = false)
    private String teamLogo;

    @OneToMany(mappedBy = "teamId")
    private List<team_members> members;
}

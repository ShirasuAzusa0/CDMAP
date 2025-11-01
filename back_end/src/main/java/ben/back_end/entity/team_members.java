package ben.back_end.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "team_members")
public class team_members {
    @Id
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private Users userId;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId")
    private Teams teamId;
}

package ben.back_end.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoVO {
    @Data
    public static class TeamInfo {
        private String teamName;
    }

    String userName;
    String avatar;
    String email;
    String realName;
    private List<TeamInfo> teams;
    int rating;
    LocalDateTime firstRace;
    LocalDateTime firstPodium;
    LocalDateTime firstPole;
    LocalDateTime firstWin;
    int totalRace;
    int totalPodium;
    int totalPole;
    int totalWin;
    LocalDateTime registerTime;
    LocalDateTime lastConnected;
}

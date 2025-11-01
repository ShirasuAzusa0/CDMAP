package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalUserRankElementVO {
    private String userName;
    private String teamName;
    private int pts;
}

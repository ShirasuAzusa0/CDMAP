package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubRankElementVO {
    private String userName;
    private String teamName;
    private int pts;
    private int pos;
}

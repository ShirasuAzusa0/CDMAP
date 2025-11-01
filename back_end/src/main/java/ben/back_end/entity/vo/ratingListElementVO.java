package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ratingListElementVO {
    private String userName;
    private int rating;
}

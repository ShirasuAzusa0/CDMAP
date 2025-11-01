package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryListElementVO {
    private int catId;
    private String catName;
    private String description;
}

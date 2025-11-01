package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RPListElementVO {
    private String sub_eventName;
    private String userName;
    private String teamName;
    private String type;
    private String description;
}

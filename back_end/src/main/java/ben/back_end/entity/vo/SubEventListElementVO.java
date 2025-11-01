package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubEventListElementVO {
    private String sub_eventName;
    private String description;
    private String status;
    private String sub_eventType;
}

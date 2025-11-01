package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventListElementVO {
    private String eventName;
    private String description;
    private String linkURL;
    private String eventAvatar;
    private String type;
    private String status;
    private int num;
}

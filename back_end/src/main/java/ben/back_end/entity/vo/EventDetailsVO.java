package ben.back_end.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDetailsVO {
    private String eventName;
    private String eventAvatar;
    private String description;
    private String content;
    private String type;
    private LocalDateTime firstEvent;
    private LocalDateTime lastEvent;
    private String status;
    private int num;
    List<String> cars;
}

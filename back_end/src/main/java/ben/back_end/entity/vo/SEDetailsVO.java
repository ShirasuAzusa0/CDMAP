package ben.back_end.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SEDetailsVO {
    private String sub_eventName;
    private String description;
    private LocalDateTime sub_eventStart;
    private LocalDateTime sub_eventEnd;
    private LocalDateTime registerEndDate;
    private String status;
    private String sub_eventType;
    private String content;
}

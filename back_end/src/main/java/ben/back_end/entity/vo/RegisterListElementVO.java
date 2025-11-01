package ben.back_end.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterListElementVO {
    private int registerId;
    private String userName;
    private String teamName;
    private String eventName;
    private String sub_eventName;
    private String carName;
    private String status;
    private String description;
}

package ben.back_end.entity.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String account;
    private String sub_eventName;
    private String carName;
    private String teamName;
}

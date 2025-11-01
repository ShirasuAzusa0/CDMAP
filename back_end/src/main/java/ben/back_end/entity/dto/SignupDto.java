package ben.back_end.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupDto {
    private String userName;
    private String account;
    private String password;
    private String realName;
    private String identification;
    private String captcha;
    private String captchaKey;
}

package ben.back_end.entity.vo;

import ben.back_end.entity.userType;
import lombok.Data;

// 登录/注册的返回数据
@Data
public class AuthenticationVO {
    private int userId;
    private String userName;
    private String avatar;
    private userType type;
    private String token;
}

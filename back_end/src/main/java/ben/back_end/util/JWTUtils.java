package ben.back_end.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
public class JWTUtils {
    // 加密密钥
    @Value("${spring.security.jwt.key}")
    private String key;

    // JWT令牌有效期设置（单位：天）
    @Value("${spring.security.jwt.expire}")
    private Integer expire;

    // JWT令牌过期时间 expire 的计算方法
    public Date expireTime() {
        Calendar cal = Calendar.getInstance();
        // expire视为天数，配置失常则退回到默认值
        int days = Math.max(1, expire);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    // 解析用户Id
    // 从DecodedJWT中提取用户的数据库主键唯一标识userId
    public Integer toId(DecodedJWT decodedJWT) {
        if (decodedJWT == null) return null;
        Map<String, Claim> claims = decodedJWT.getClaims();
        Claim idClaim = claims.get("userId");
        if (idClaim == null) return null;
        try {
            return idClaim.asInt();
        } catch (Exception e) {
            // 保守处理：若转换失败，返回 null
            return null;
        }
    }

    // 创建JWT令牌，需要用到用户信息（从info中提取出来）、userId、用户名
    public String generateJWT(UserDetails userDetails, int userId, String userName) {
        // 使用HMAC256加密算法
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();

        // 将 authorities 转为 String[] 并用 withArrayClaim 明确写入数组类型
        String[] authoritiesArray = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .toArray(String[]::new);

        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("userName", userName)
                .withArrayClaim("authorities", authoritiesArray)
                .withIssuedAt(new Date())
                .withExpiresAt(expire)
                .sign(algorithm);
    }

    // 格式化token方法，将headerToken的前缀去掉
    private String convertToken(String headerToken) {
        if (headerToken == null) return null;
        String trimmed = headerToken.trim();
        if (!trimmed.startsWith("Bearer ")) {
            return null;
        }
        // 返回去掉前缀并 trim
        return trimmed.substring(7).trim();
    }

    // JWT令牌验证与解析
    public DecodedJWT verifyJWT(String headerToken) {
        // 去掉前缀并检验
        String token = this.convertToken(headerToken);
        if (token == null) return null;

        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt == null) return null;
            else {
                if (new Date().after(expiresAt)) {
                    return null;
                }
            }
            return jwt;
        } catch (JWTVerificationException e) {
            System.out.println("JWT verify failed: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // 任何意外也返回 null，避免抛出到上层
            System.out.println("JWT verify unexpected error: " + e.getMessage());
            return null;
        }
    }
}

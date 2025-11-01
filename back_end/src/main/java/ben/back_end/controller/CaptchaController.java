package ben.back_end.controller;

import ben.back_end.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class CaptchaController {

    private final CaptchaService captchaService;
    private final StringRedisTemplate redisTemplate;

    // 使用构造器注入（避免NPE）
    @Autowired
    public CaptchaController(CaptchaService captchaService, StringRedisTemplate redisTemplate) {
        this.captchaService = captchaService;
        this.redisTemplate = redisTemplate;
    }

    // 验证码图片传给前端
    @GetMapping("/captcha")
    public ResponseEntity<?> getCaptcha() {
        // 生成验证码文本
        String captchaText = captchaService.generateText();

        // 生成唯一标识key
        String key = UUID.randomUUID().toString();

        // 将验证码存入Redis，有效期3min
        redisTemplate.opsForValue().set("captcha: " + key, captchaText, 10, TimeUnit.MINUTES);

        // 生成Base64图片字符串
        String base64Image = captchaService.generateImageBase64(captchaText);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "获取验证码成功",
                "key", key,
                "image", base64Image
        ));
    }
}

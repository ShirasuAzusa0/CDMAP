package ben.back_end.service;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Service
public class CaptchaService {
    // 定义全局安全随机数生成器
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // 生成四位验证码
    public String generateText() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0','9')
                .withinRange('a','z')
                .withinRange('A','Z')
                .usingRandom(SECURE_RANDOM::nextInt)
                .build();
        return generator.generate(4);
    }

    // 生成随机颜色（避免过亮或过暗）
    private Color randomColor(Random random, int min, int max) {
        if (min > 255) min = 255;
        if (max > 255) max = 255;
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }

    // 生成验证码图片
    public String generateImageBase64(String text) {
        BufferedImage image = new BufferedImage(100, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        Random random = new Random();
        g2.setColor(Color.GRAY);
        // 绘制干扰线条
        for (int i = 0; i < 8; i++) {
            g2.setColor(randomColor(random, 100, 200));
            int x1 = random.nextInt(100);
            int y1 = random.nextInt(40);
            int x2 = random.nextInt(100);
            int y2 = random.nextInt(40);
            g2.drawLine(x1, y1, x2, y2);
        }
        // 绘制随机噪点
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(100);
            int y = random.nextInt(40);
            image.setRGB(x, y, randomColor(random, 150, 255).getRGB());
        }
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.drawString(text, 15, 28);

        // 释放资源
        g2.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}

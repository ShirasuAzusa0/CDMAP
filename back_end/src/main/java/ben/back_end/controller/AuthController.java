package ben.back_end.controller;

import ben.back_end.entity.*;
import ben.back_end.entity.dto.LoginDto;
import ben.back_end.entity.dto.RegisterDto;
import ben.back_end.entity.dto.SignupDto;
import ben.back_end.entity.vo.AuthenticationVO;
import ben.back_end.entity.vo.RPListElementVO;
import ben.back_end.entity.vo.UserInfoVO;
import ben.back_end.repository.RegisterRepository;
import ben.back_end.repository.UserRepository;
import ben.back_end.service.CarService;
import ben.back_end.service.SubEventService;
import ben.back_end.service.TeamService;
import ben.back_end.service.UserService;
import ben.back_end.util.JWTUtils;
import ben.back_end.util.RSAKeyUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private JWTUtils jwtUtils;

    @Resource
    private UserRepository userRepository;

    @Resource
    private RegisterRepository registerRepository;

    @Resource
    private RSAKeyUtils rsaKeyUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private CarService carService;

    @Autowired
    private SubEventService subEventService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    StringRedisTemplate redisTemplate;

    private ResponseEntity<?> verifyCaptcha(String captchaKey, String captchaInput) {
        if (captchaKey == null || captchaInput == null) {
            return ResponseEntity.badRequest().build();
        }
        // 从Redis中获取验证码
        String redisCaptcha = redisTemplate.opsForValue().get("captcha: "+ captchaKey);
        if (redisCaptcha == null) {
            return ResponseEntity.badRequest().body(RestBean.failure("验证码已过期"));
        }
        if (!redisCaptcha.equalsIgnoreCase(captchaInput.trim())) {
            return ResponseEntity.badRequest().body(RestBean.failure("验证码错误"));
        }
        // 验证通过后删除验证码，防止重复使用
        redisTemplate.delete("captcha:" + captchaKey);
        return null;
    }

    // 注册
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto dto) {
        if (userRepository.findByEmail(dto.getAccount()) != null) {
            return ResponseEntity.badRequest().body(RestBean.failure("该邮箱已被注册"));
        }

        // 校对验证码
        String captchaKey = dto.getCaptchaKey();
        String captchaInput = dto.getCaptcha();
        ResponseEntity<?> captchaResult = verifyCaptcha(captchaKey, captchaInput);
        // 校对失败直接返回
        if (captchaResult != null) {
            return captchaResult;
        }

        // 对密码进行解密后再加密（RSA解密->BCrypt加密）
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String rawPassword = dto.getPassword();
            // 先把空格替换回加号
            String fixedPassword = rawPassword.replace(' ', '+');
            // 再去除密码中所有空白字符（空格、换行等）
            String encryptedPasswordClean = fixedPassword.replaceAll("\\s", "");

            // 再进行解密存储
            String decryptedPassword = rsaKeyUtil.decrypt(encryptedPasswordClean);
            String encryptedPassword = passwordEncoder.encode(decryptedPassword);

            dto.setPassword(encryptedPassword);
        }

        Users user = new Users();
        // 手动设置
        user.setUserName(dto.getUserName());
        user.setAvatar("https://avatars.githubusercontent.com/u/19370775");
        user.setEmail(dto.getAccount());
        user.setPassword(dto.getPassword());
        user.setRealName(dto.getRealName());
        user.setIdentify(dto.getIdentification());      // 实名信息直接加密存储
        user.setRating(0);
        user.setFirstRace(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0));
        user.setFirstPodium(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0));
        user.setFirstPole(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0));
        user.setFirstWin(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0));
        user.setTotalRace(0);
        user.setTotalPodium(0);
        user.setTotalPole(0);
        user.setTotalWin(0);
        user.setRegisterTime(LocalDateTime.now());
        user.setLastConnected(LocalDateTime.now());
        user.setType(userType.USER);

        userRepository.save(user);

        // 生成 JWT 令牌
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        UserDetails userDetails = User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(auth.getAuthorities())
                .build();

        String token = jwtUtils.generateJWT(userDetails, user.getUserId(), dto.getUserName());
        String bearerToken = "Bearer " + token;
        AuthenticationVO vo = new AuthenticationVO();
        vo.setUserName(dto.getUserName());
        vo.setUserId(user.getUserId());
        vo.setAvatar(user.getAvatar());
        vo.setToken(bearerToken);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "msg", "注册成功",
                        "data", vo
                )
        );
    }

    // 登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        if (userRepository.findByEmail(dto.getAccount()) == null) {
            return ResponseEntity.badRequest().body(RestBean.failure("不存在该账号"));
        }

        // 验证验证码
        String captchaKey = dto.getCaptchaKey();
        String captchaInput = dto.getCaptcha();
        ResponseEntity<?> captchaResult = verifyCaptcha(captchaKey, captchaInput);
        // 验证失败直接返回
        if (captchaResult != null) {
            return captchaResult;
        }

        // 从application/json中获取邮箱
        String email = dto.getAccount();

        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(RestBean.failure("该邮箱未注册账号"));
        }

        // 对密码进行解密
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String rawPassword = dto.getPassword();
            // 先把空格替换回加号
            String fixedPassword = rawPassword.replace(' ', '+');
            // 再去除密码中所有空白字符（空格、换行等）
            String encryptedPasswordClean = fixedPassword.replaceAll("\\s+", "");

            System.out.println(encryptedPasswordClean);

            // 再进行解密
            String decryptedPassword = rsaKeyUtil.decrypt(encryptedPasswordClean);

            // 使用BCrypt验证密码
            if (!passwordEncoder.matches(decryptedPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body(RestBean.failure("密码错误"));
            }
        }

        user.setLastConnected(LocalDateTime.now());
        userRepository.save(user);

        // 生成 JWT 令牌
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        UserDetails userDetails = User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(auth.getAuthorities())
                .build();

        String token = jwtUtils.generateJWT(userDetails, user.getUserId(), user.getUserName());
        String bearerToken = "Bearer " + token;
        AuthenticationVO vo = new AuthenticationVO();
        vo.setUserName(user.getUserName());
        vo.setUserId(user.getUserId());
        vo.setAvatar(user.getAvatar());
        vo.setType(user.getType());
        vo.setToken(bearerToken);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "msg", "登录成功",
                        "data", vo
                )
        );
    }

    // 获取用户个人信息
    @GetMapping("/{userId}/info")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        if(userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(RestBean.failure("userId不能为空"));
        }
        int user_id = Integer.parseInt(userId);
        Users user = userService.findById(user_id);
        UserInfoVO vo = new UserInfoVO();
        vo.setUserName(user.getUserName());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setRealName(user.getRealName());
        vo.setRating(user.getRating());
        vo.setFirstRace(user.getFirstRace());
        vo.setFirstPodium(user.getFirstPodium());
        vo.setFirstPole(user.getFirstPole());
        vo.setFirstWin(user.getFirstWin());
        vo.setTotalRace(user.getTotalRace());
        vo.setTotalPodium(user.getTotalPodium());
        vo.setTotalPole(user.getTotalPole());
        vo.setTotalWin(user.getTotalWin());
        vo.setRegisterTime(user.getRegisterTime());
        vo.setLastConnected(user.getLastConnected());

        // 查询所属车队并封装
        List<Teams> teams = userService.findTeamsByUserId(user_id);
        vo.setTeams(teams.stream()
                .map(t -> {
                    UserInfoVO.TeamInfo teamInfo = new UserInfoVO.TeamInfo();
                    teamInfo.setTeamName(t.getTeamName());
                    return teamInfo;
                })
                .toList());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "用户个人信息获取成功",
                "data", vo
        ));
    }

    // 赛事报名
    @PostMapping("/register")
    public ResponseEntity<?> register4race(@RequestBody RegisterDto dto) {
        Registers registers = new Registers();
        registers.setUser(userService.findByEmail(dto.getAccount()));
        registers.setSubEvent(subEventService.findBySubEventName(dto.getSub_eventName()));
        registers.setTeam(teamService.findByTeamName(dto.getTeamName()));
        registers.setCar(carService.findByCarName(dto.getCarName()));
        registers.setStatus(registerStatus.UNCONFIRMED);
        registers.setDescription("");

        registerRepository.save(registers);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "赛事已报名"
        ));
    }

    // 获取奖惩列表
    @GetMapping("/{userName}/reward_punishment")
    public ResponseEntity<?> getRewardPunishmentList(@PathVariable String userName) {
        List<RPListElementVO> vos = userService.findUserRPList(userName);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "用户的奖惩列表获取成功",
                "data", vos
        ));
    }
}

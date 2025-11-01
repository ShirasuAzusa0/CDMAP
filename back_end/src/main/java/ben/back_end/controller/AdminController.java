package ben.back_end.controller;

import ben.back_end.entity.dto.HandleListElementDto;
import ben.back_end.entity.dto.RPupdateListElementDto;
import ben.back_end.entity.vo.RegisterListElementVO;
import ben.back_end.service.RPService;
import ben.back_end.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private RegisterService registerService;

    @Autowired
    private RPService rpService;

    // 处理报名信息
    @PostMapping("/handle")
    public ResponseEntity<?> handleRegister(@RequestBody List<HandleListElementDto> dtos) {
        registerService.updateRegisters(dtos);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "报名信息已处理完成"
        ));
    }

    // 上传奖惩信息
    @PostMapping("/reward_punishment_update")
    public ResponseEntity<?> updateRP(@RequestBody List<RPupdateListElementDto> dtos) {
        rpService.setRPs(dtos);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "奖惩信息已同步完成"
        ));
    }

    // 获取报名信息列表
    @GetMapping("/register")
    public ResponseEntity<?> getRegisterList(@RequestParam String status) {
        List<RegisterListElementVO> vos = registerService.getRegistersByStatus(status);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "报名信息列表获取成功",
                "data", vos
        ));
    }
}

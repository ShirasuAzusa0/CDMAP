package ben.back_end.controller;

import ben.back_end.entity.RestBean;
import ben.back_end.entity.vo.*;
import ben.back_end.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/source")
public class SourceController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventService eventService;

    @Autowired
    private SubEventService subEventService;

    @Autowired
    private UserService userService;

    @Autowired
    private RankService rankService;

    // 获取赛事分类
    @GetMapping("/categories")
    public ResponseEntity<?> getEventCategories() {
        List<CategoryListElementVO> vos = categoryService.getAllCategories();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "赛事分类获取成功",
                "data", vos
        ));
    }

    // 获取赛事列表
    @GetMapping("/events")
    public ResponseEntity<?> getEvents(@RequestParam String catName) {
        List<EventListElementVO> vos = eventService.getEventsByCatName(catName);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "赛事列表获取成功",
                "data", vos
        ));
    }

    // 获取赛事详情
    @GetMapping("/events/{eventName}")
    public ResponseEntity<?> getEvent(@PathVariable String eventName) {
        EventDetailsVO vo = eventService.getEventByEventName(eventName);
        if (vo.getEventName() == null) return ResponseEntity.badRequest().body(RestBean.failure("未找到赛事" + eventName));
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "赛事详细信息获取成功",
                "data", vo
        ));
    }

    // 获取分站列表
    @GetMapping("/{eventName}/sub_events")
    public ResponseEntity<?> getSubEventList(@PathVariable String eventName) {
        List<SubEventListElementVO> vos = subEventService.getSubEventsByEventName(eventName);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "分站列表获取成功",
                "data", vos
        ));
    }

    // 获取分站详情
    @GetMapping("/{sub_eventName}")
    public ResponseEntity<?> getSubEvent(@PathVariable String sub_eventName) {
        SEDetailsVO vo = subEventService.getSEDetailsBySubEventName(sub_eventName);
        if (vo.getSub_eventName() == null) return ResponseEntity.badRequest().body(RestBean.failure("未找到赛事" + sub_eventName));
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "分站详细信息获取成功",
                "data", vo
        ));
    }

    // 通过分站获取赛事名
    @GetMapping("/getEventName/{sub_eventName}")
    public ResponseEntity<?> getEventName(@PathVariable String sub_eventName) {
        String eventName = subEventService.getEventNameBySubEventName(sub_eventName);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "分站详细信息获取成功",
                "data", eventName
        ));
    }

    // 获取rating榜
    @GetMapping("/rating")
    public ResponseEntity<?> getRatingList() {
        List<ratingListElementVO> vos = userService.findRatingList();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "分站详细信息获取成功",
                "data", vos
        ));
    }

    // 获取赛事总排名
    @GetMapping("/event/{eventName}/rank")
    public ResponseEntity<?> getTotalRankList(@PathVariable String eventName, @RequestParam String type) {
        String eventType = eventService.getEventTypeByName(eventName);
        System.out.println("赛事类型: " + eventType + ", 请求类型: " + type);

        Object vos;
        // 根据请求的type参数决定返回车手榜还是车队榜
        if ("TEAM".equals(type)) {
            // 只有团队赛才有车队榜，个人赛返回空列表
            if ("TEAM".equals(eventType)) {
                vos = rankService.getTotalRank(eventName, TotalTeamRankElementVO.class);
            } else {
                vos = Collections.emptyList(); // 个人赛无车队榜
            }
        } else {
            // 默认返回车手榜（INDIVIDUAL）
            vos = rankService.getTotalRank(eventName, TotalUserRankElementVO.class);
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "赛事总排名获取成功",
                "type", eventType,              // 返回赛事类型供前端判断
                "data", vos
        ));
    }

    // 获取分站排名
    @GetMapping("/sub_event/{sub_eventName}/rank")
    public ResponseEntity<?> getSubRankList(@PathVariable String sub_eventName, @RequestParam String type) {
        List<SubRankElementVO> vos = rankService.getSubRank(sub_eventName, type);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "msg", "分站排名获取成功",
                "data", vos
        ));
    }
}

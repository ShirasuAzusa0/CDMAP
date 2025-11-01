package ben.back_end.service;

import ben.back_end.entity.Ranks;
import ben.back_end.entity.vo.SubRankElementVO;
import ben.back_end.entity.vo.TotalTeamRankElementVO;
import ben.back_end.entity.vo.TotalUserRankElementVO;
import ben.back_end.repository.RankRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RankService {
    private final RankRepository rankRepository;

    public RankService(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    private List<TotalUserRankElementVO> getUserTotalRank(String eventName) {
        List<Object[]> results = rankRepository.findUserTotalRankByEventName(eventName);
        return results.stream()
                .map(r -> new TotalUserRankElementVO(
                        (String) r[0],
                        (String) r[1],
                        ((Number) r[2]).intValue()
                ))
                .toList();
    }

    private List<TotalTeamRankElementVO> getTeamTotalRank(String eventName) {
        List<Object[]> results = rankRepository.findTeamTotalRankByEventName(eventName);
        return results.stream()
                .map(r -> new TotalTeamRankElementVO(
                        (String) r[0],
                        ((Number) r[1]).intValue()
                ))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> castList(List<?> list, Class<T> type) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        if (!type.isInstance(list.get(0))) {
            throw new ClassCastException("列表元素类型不匹配");
        }

        return (List<T>) list;
    }

    // 修改getTotalRank方法，移除内部的eventType判断，直接根据传入的type决定
    public <T> List<T> getTotalRank(String eventName, Class<T> type) {
        // 直接根据传入的Class类型决定返回车队榜还是车手榜
        if (type.equals(TotalTeamRankElementVO.class)) {
            return castList(getTeamTotalRank(eventName), type);
        } else {
            return castList(getUserTotalRank(eventName), type);
        }
    }

    public List<SubRankElementVO> getSubRank(String sub_eventName, String type) {
        List<Ranks> ranksList = rankRepository.findBySubEventName(sub_eventName, type);
        return ranksList.stream()
                .map(r -> new SubRankElementVO(
                        r.getUser().getUserName(),
                        r.getTeam().getTeamName(),
                        r.getPts(),
                        r.getPos()
                )).toList();
    }
}
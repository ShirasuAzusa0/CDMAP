package ben.back_end.service;

import ben.back_end.entity.*;
import ben.back_end.entity.dto.RPupdateListElementDto;
import ben.back_end.repository.RPRepository;
import ben.back_end.repository.SERepository;
import ben.back_end.repository.TeamRepository;
import ben.back_end.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RPService {
    @Autowired
    private RPRepository rpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private SERepository seRepository;

    @Transactional
    public void setRPs(List<RPupdateListElementDto> dtos) {
        for (RPupdateListElementDto dto : dtos) {
            // 获取用户实体
            Users user = userRepository.findByUserName(dto.getUserName());

            // 获取团队实体
            Teams team = teamRepository.findByTeamName(dto.getTeamName());

            // 获取子赛事实体
            Sub_Events subEvent = seRepository.findBySEName(dto.getSub_eventName());

            Rewards_Punishments rp = new Rewards_Punishments();
            rp.setUser(user);
            rp.setTeam(team);
            rp.setSubEvent(subEvent);
            rp.setDescription(dto.getDescription());
            rp.setType(rpType.valueOf(dto.getType()));
            rpRepository.save(rp);
        }
    }


}

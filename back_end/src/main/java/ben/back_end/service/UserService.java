package ben.back_end.service;

import ben.back_end.entity.Rewards_Punishments;
import ben.back_end.entity.Teams;
import ben.back_end.entity.Users;
import ben.back_end.entity.vo.RPListElementVO;
import ben.back_end.entity.vo.ratingListElementVO;
import ben.back_end.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Users findById(int user_id) {
        return userRepository.findByUserId(user_id);
    }

    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Users findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public List<Teams> findTeamsByUserId(int userId) {
        return userRepository.findTeamsByUserId(userId);
    }

    public List<ratingListElementVO> findRatingList() {
        List<Users> userList = userRepository.findAllOrderByRating();
        return userList.stream()
                .map(u -> new ratingListElementVO(
                        u.getUserName(),
                        u.getRating()
                )).toList();
    }

    public List<RPListElementVO> findUserRPList(String userName) {
        List<Rewards_Punishments> rpList = userRepository.findRPList(userRepository.findByUserName(userName).getUserId());
        return rpList.stream()
                .map(rp -> new RPListElementVO(
                        rp.getSubEvent().getSub_eventName(),
                        rp.getUser().getUserName(),
                        rp.getTeam().getTeamName(),
                        rp.getType().name(),
                        rp.getDescription()
                )).toList();
    }
}

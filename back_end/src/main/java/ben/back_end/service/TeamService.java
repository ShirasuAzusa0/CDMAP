package ben.back_end.service;

import ben.back_end.entity.Teams;
import ben.back_end.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public Teams findByTeamName(String teamName) {
        return teamRepository.findByTeamName(teamName);
    }
}

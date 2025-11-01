package ben.back_end.service;

import ben.back_end.entity.Registers;
import ben.back_end.entity.dto.HandleListElementDto;
import ben.back_end.entity.registerStatus;
import ben.back_end.entity.vo.RegisterListElementVO;
import ben.back_end.repository.RegisterRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterService {
    @Autowired
    private RegisterRepository registerRepository;

    @Transactional
    public void updateRegisters(List<HandleListElementDto> dtos) {
        for (HandleListElementDto dto : dtos) {
            registerRepository.findById(dto.getRegisterId()).ifPresent(register -> {
                register.setStatus(registerStatus.valueOf(dto.getStatus()));
                register.setDescription(dto.getDescription());
                registerRepository.save(register);
            });
        }
    }

    public List<RegisterListElementVO> getRegistersByStatus(String status) {
        List<Registers> registersList = registerRepository.findByStatus(status);
        return registersList.stream()
                .map(r -> new RegisterListElementVO(
                        r.getRegisterId(),
                        r.getUser().getUserName(),
                        r.getTeam().getTeamName(),
                        r.getSubEvent().getEvent().getEventName(),
                        r.getSubEvent().getSub_eventName(),
                        r.getCar().getCarName(),
                        r.getStatus().name(),
                        r.getDescription()
                )).toList();
    }
}

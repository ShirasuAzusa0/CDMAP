package ben.back_end.service;

import ben.back_end.entity.Sub_Events;
import ben.back_end.entity.vo.SEDetailsVO;
import ben.back_end.entity.vo.SubEventListElementVO;
import ben.back_end.repository.EventRepository;
import ben.back_end.repository.SERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubEventService {
    @Autowired
    private SERepository seRepository;
    @Autowired
    private EventRepository eventRepository;

    public String getEventNameBySubEventName(String sub_eventName) {
        return seRepository.findEventNameBySEName(sub_eventName);
    }

    public Sub_Events findBySubEventName(String subEventName) {
        return seRepository.findBySEName(subEventName);
    }

    public List<SubEventListElementVO> getSubEventsByEventName(String eventName) {
        List<Sub_Events> sub_eventsList = seRepository.findByEventId(eventRepository.getEventByEventName(eventName).getEventId());
        return sub_eventsList.stream()
                .map(se -> new SubEventListElementVO(
                        se.getSub_eventName(),
                        se.getDescription(),
                        se.getStatus().name(),
                        se.getSub_eventType().name()
                )).toList();
    }

    public SEDetailsVO getSEDetailsBySubEventName(String sub_eventName) {
        Sub_Events se = seRepository.findBySEName(sub_eventName);
        SEDetailsVO vo = new SEDetailsVO();
        if (se == null) {
            System.out.println("Event not found: " + sub_eventName);
            return vo;
        }
        vo.setSub_eventName(se.getSub_eventName());
        vo.setDescription(se.getDescription());
        vo.setSub_eventStart(se.getSub_eventStart());
        vo.setSub_eventEnd(se.getSub_eventEnd());
        vo.setRegisterEndDate(se.getRegisterEndDate());
        vo.setStatus(se.getStatus().name());
        vo.setSub_eventType(se.getSub_eventType().name());
        vo.setContent(se.getContent());
        return vo;
    }
}

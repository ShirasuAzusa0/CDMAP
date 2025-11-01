package ben.back_end.service;

import ben.back_end.entity.Events;
import ben.back_end.entity.vo.EventDetailsVO;
import ben.back_end.entity.vo.EventListElementVO;
import ben.back_end.repository.CarRepository;
import ben.back_end.repository.EventRepository;
import ben.back_end.repository.RankRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final CarRepository carRepository;
    private final RankRepository rankRepository;

    public EventService(EventRepository eventRepository, CarRepository carRepository, RankRepository rankRepository) {
        this.eventRepository = eventRepository;
        this.carRepository = carRepository;
        this.rankRepository = rankRepository;
    }

    public String getEventTypeByName(String eventName) {
        return rankRepository.findEventTypeByName(eventName);
    }

    public List<EventListElementVO> getEventsByCatName(String catName) {
        List<Events> eventsList = eventRepository.findByCatName(catName);
        return eventsList.stream()
                .map(e -> new EventListElementVO(
                        e.getEventName(),
                        e.getDescription(),
                        e.getLinkURL(),
                        e.getEventAvatar(),
                        e.getType().name(),
                        e.getStatus().name(),
                        e.getNum()
                )).toList();
    }

    public EventDetailsVO getEventByEventName(String eventName) {
        Events event = eventRepository.getEventByEventName(eventName);
        EventDetailsVO vo = new EventDetailsVO();
        if (event == null) {
            System.out.println("Event not found: " + eventName);
            return vo;
        }
        vo.setEventName(event.getEventName());
        vo.setEventAvatar(event.getEventAvatar());
        vo.setDescription(event.getDescription());
        vo.setFirstEvent(event.getFirstEvent());
        vo.setLastEvent(event.getLastEvent());
        vo.setContent(event.getContent());
        vo.setType(event.getType().name());
        vo.setStatus(event.getStatus().name());
        vo.setNum(event.getNum());
        vo.setCars(carRepository.findCatNamesByEventId(event.getEventId()));
        return vo;
    }
}

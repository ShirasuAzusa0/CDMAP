package ben.back_end.entity.dto;

import lombok.Data;

@Data
public class HandleListElementDto {
    private int registerId;
    private String status;
    private String description;
}

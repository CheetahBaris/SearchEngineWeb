package searchengine.dto.serachEngine;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

@Data
public class RequestString {
    private String query;
    private int offset;
    private int limit;
}

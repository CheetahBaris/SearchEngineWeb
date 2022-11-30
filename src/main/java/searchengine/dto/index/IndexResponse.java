package searchengine.dto.index;

import lombok.Data;

@Data
public class IndexResponse {
    private boolean result;
    private String error;
//    private SiteIndexingResponse siteIndexingResponse;
}

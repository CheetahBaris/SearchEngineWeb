package searchengine.dto.statics;

import lombok.Data;
import searchengine.model.entities.TypesOfIndexes;

@Data
public class DetailedStatisticsItem {
    private String url;
    private String name;
    private TypesOfIndexes status;
    private long statusTime;
    private String error;
    private long pages;
    private long lemmas;
}
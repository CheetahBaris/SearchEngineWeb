package searchengine.dto.statics;

import lombok.Data;

@Data
public class TotalStatistics {
    private int sites;
    private long pages;
    private long lemmas;
    private boolean indexing;
}
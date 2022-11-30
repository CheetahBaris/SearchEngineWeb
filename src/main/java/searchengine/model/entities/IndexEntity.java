package searchengine.model.entities;

//import jakarta.persistence.*;
import javax.persistence.*;

@Entity
@Table(name = "`index`"
        , indexes = {
        @Index(name = "page_index",columnList = "page_id" ),
        @Index(name = "lemma_index",columnList = "lemma_id" ),
        @Index(name = "rank_index",columnList = "`rank`")}
)
 public class IndexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "INT", nullable = false)
    private int page_id;
    @Column(columnDefinition = "INT", nullable = false)
    private int lemma_id;
    @Column(name = "`rank`", columnDefinition = "INT", nullable = false)
    private int rank;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public int getLemma_id() {
        return lemma_id;
    }

    public void setLemma_id(int lemma_id) {
        this.lemma_id = lemma_id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}

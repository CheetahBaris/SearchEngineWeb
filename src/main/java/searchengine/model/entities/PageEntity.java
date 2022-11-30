package searchengine.model.entities;

//import jakarta.persistence.*;
import org.springframework.web.bind.annotation.Mapping;

import javax.persistence.*;


@Entity
@Table(name = "page"
        , indexes = { @Index(columnList = "path")}
)
 public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "site_id")
    private SiteEntity site_id;
    @Column(columnDefinition = "TEXT",nullable = false)
    private String path;
    @Column(columnDefinition = "INT",nullable = false)
    private int code;
    @Column(columnDefinition = "MEDIUMTEXT",nullable = false)
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SiteEntity getSite_id() {
        return site_id;
    }

    public void setSite_id(SiteEntity site_id) {
        this.site_id = site_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

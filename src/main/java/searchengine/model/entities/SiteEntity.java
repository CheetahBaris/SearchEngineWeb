package searchengine.model.entities;
import org.springframework.stereotype.Component;

import javax.persistence.*;

//import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "site")
 public class SiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    private TypesOfIndexes status;
    @Column(columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime status_time;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String last_error;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypesOfIndexes getStatus() {
        return status;
    }

    public void setStatus(TypesOfIndexes status) {
        this.status = status;
    }

    public LocalDateTime getStatus_time() {
        return status_time;
    }

    public void setStatus_time(LocalDateTime status_time) {
        this.status_time = status_time;
    }

    public String getLast_error() {
        return last_error;
    }

    public void setLast_error(String last_error) {
        this.last_error = last_error;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
package searchengine.model.entities;

//import jakarta.persistence.*;

import org.hibernate.annotations.SQLInsert;
import searchengine.config.Site;

import javax.persistence.*;
//import org.hibernate.annotations.Table;

//import javax.persistence.*;

@Entity
@Table(name = "lemma"
        , indexes = {
        @Index(columnList = "lemma", unique = true)}
)
  @SQLInsert(sql =" INSERT INTO lemma (site_id, lemma, frequency)  VALUES  (?, ?, ?) ON DUPLICATE KEY UPDATE " +
        "frequency = frequency + 1")
 public class LemmaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE )
    @JoinColumn(name = "site_id")
//    @Column(name = "site_id")
    private SiteEntity site_id;
    @Column(columnDefinition = "VARCHAR(255)",nullable = false)
    private String lemma;
    @Column(columnDefinition = "INT",nullable = false)
     private int frequency;

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

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}

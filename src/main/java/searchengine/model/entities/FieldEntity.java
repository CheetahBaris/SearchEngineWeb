package searchengine.model.entities;


//import jakarta.persistence.*;

import javax.persistence.*;

@Entity
@Table(name = "field")
 public class FieldEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;
    @Column(columnDefinition = "VARCHAR(255)",nullable = false)
    private String name;
    @Column(columnDefinition = "VARCHAR(255)",nullable = false)
    private String  selector;
    @Column(columnDefinition = "FLOAT",nullable = false)
    private float weight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}

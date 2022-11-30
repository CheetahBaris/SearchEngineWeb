package searchengine.model.repositories;

import org.springframework.data.repository.CrudRepository;
import searchengine.model.entities.FieldEntity;

public interface FieldEntityCrudRepository extends CrudRepository<FieldEntity,Integer> {
}

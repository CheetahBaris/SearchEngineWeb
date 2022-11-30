package searchengine.model.repositories;

import org.springframework.data.repository.CrudRepository;
import searchengine.model.entities.IndexEntity;

public interface IndexEntityCrudRepository extends CrudRepository<IndexEntity,Integer> {
}

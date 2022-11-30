package searchengine.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.entities.LemmaEntity;

@Repository
public interface LemmaEntityCrudRepository extends CrudRepository<LemmaEntity, Integer>  {

}

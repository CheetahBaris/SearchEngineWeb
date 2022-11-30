package searchengine.model.repositories;

import org.springframework.data.repository.CrudRepository;
import searchengine.model.entities.SiteEntity;

public interface SiteEntityCrudRepository extends CrudRepository<SiteEntity,Integer> {
}

package searchengine.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.entities.PageEntity;


@Repository
public interface PageEntityCrudRepository extends CrudRepository<PageEntity,Integer> {

}

package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteModel;
import searchengine.model.Status;

@Repository
public interface SiteRepository extends JpaRepository<SiteModel, Long> {
    SiteModel findByUrl(String url);
    boolean existsByStatus(Status status);
}
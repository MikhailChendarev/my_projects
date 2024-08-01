package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.SiteModel;
import searchengine.model.Status;

@Repository
public interface SiteRepository extends JpaRepository<SiteModel, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Site", nativeQuery = true)
    void deleteAllNative();
    SiteModel findByUrl(String url);
    boolean existsByStatus(Status status);
}
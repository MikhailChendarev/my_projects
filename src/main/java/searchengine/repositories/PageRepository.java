package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;
import searchengine.model.SiteModel;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    boolean existsByPathAndSiteModel(String path, SiteModel siteModel);
    Page findByPathAndSiteModel(String path, SiteModel siteModel);
    int countBySiteModel(SiteModel siteModel);
    List<Page> findBySiteModel(SiteModel siteModel);
}

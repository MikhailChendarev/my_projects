package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SiteModel;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Index", nativeQuery = true)
    void deleteAllNative();
    List<Index> findByPage(Page page);
    int countByLemma(Lemma lemma);
    int countByPageIn(List<Page> pages);
    List<Index> findByPageAndLemma(Page page, Lemma lemma);
    @Query("SELECT i.page FROM Index i WHERE i.lemma.lemma = :lemma")
    List<Page> findPagesByLemma(@Param("lemma") String lemma);
    @Query("SELECT i.page FROM Index i WHERE i.lemma = :lemma AND i.page.siteModel = :siteModel")
    List<Page> findPagesByLemmaAndSiteModel(@Param("lemma") Lemma lemma, @Param("siteModel") SiteModel siteModel);
}
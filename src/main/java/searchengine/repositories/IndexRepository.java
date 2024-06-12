package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.Collection;
import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {
    List<Index> findByPage(Page page);
    int countByLemma(Lemma lemma);
    int countByPageIn(List<Page> pages);
    Index findByPageAndLemma(Page page, Lemma lemma);
    Collection<Index> findByLemmaAndPageIdIn(Lemma lemma, List<Long> pageIds);
    @Query("SELECT i.page FROM Index i WHERE i.lemma.lemma = :lemma")
    List<Page> findPagesByLemma(@Param("lemma") String lemma);
}

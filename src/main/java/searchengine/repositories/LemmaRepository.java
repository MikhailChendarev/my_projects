package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Lemma", nativeQuery = true)
    void deleteAllNative();
    Lemma findByLemma(String lemma);
}
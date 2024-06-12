package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Index;


@Entity
@Table(name = "page", indexes = {@Index(name = "index_path",  columnList="path", unique = true)})
@Getter
@Setter
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String path;

    @Column(columnDefinition = "INT")
    private Integer code;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @JoinColumn(name = "site_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteModel siteModel;

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", code=" + code +
                '}';
    }
}

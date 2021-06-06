package dev.cheerfun.pixivic.biz.web.app.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/6/6 8:14 PM
 * @description ExternalLink
 */
@Data
@Entity(name = "external_links")
@AllArgsConstructor
@NoArgsConstructor
public class ExternalLink {
    @Id
    @Column(name = "external_link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "external_link_key")
    public String key;
    @Column(name = "external_link_value")
    public String value;

}

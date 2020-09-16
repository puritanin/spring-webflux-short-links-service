package ru.bicubictwice.springwebfluxshortlinksservice.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "metrics")
class Metrics : BaseEntity() {

    @Column(name = "created", columnDefinition = "TIMESTAMP")
    var created: LocalDateTime? = null

    @Column(name = "last_used", columnDefinition = "TIMESTAMP")
    var lastUsed: LocalDateTime? = null

    @OneToOne(mappedBy = "metrics", cascade = [CascadeType.ALL])
    var link: Link? = null
}

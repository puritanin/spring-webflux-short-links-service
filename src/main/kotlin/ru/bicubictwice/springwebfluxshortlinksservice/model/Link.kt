package ru.bicubictwice.springwebfluxshortlinksservice.model

import javax.persistence.*

@Entity
@Table(name = "links")
class Link : BaseEntity() {

    @Column(name = "short_uri")
    var shortUri: String? = null

    @Column(name = "redirect_url")
    var redirectUrl: String? = null

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    var status: Status? = null

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "metrics_id", referencedColumnName = "id")
    var metrics: Metrics? = null

    override fun toString(): String {
        return super.toString() + ",uri=$shortUri,$status"
    }
}

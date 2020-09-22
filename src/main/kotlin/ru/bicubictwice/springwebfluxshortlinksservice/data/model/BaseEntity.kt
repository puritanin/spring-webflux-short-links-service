package ru.bicubictwice.springwebfluxshortlinksservice.data.model

import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as BaseEntity
        return this.id != null && this.id == other.id
    }

    override fun hashCode() = id?.hashCode() ?: 0

    override fun toString(): String {
        return "${this.javaClass.simpleName},id=$id"
    }
}

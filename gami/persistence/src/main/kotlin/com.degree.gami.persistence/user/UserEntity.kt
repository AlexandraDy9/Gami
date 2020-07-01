package com.degree.gami.persistence.user

import com.degree.gami.persistence.base.TimestampedEntity
import com.degree.gami.persistence.userentities.userevents.UserEventEntity
import com.degree.gami.persistence.userentities.userphoto.UserPhotoEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.annotations.NotNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

@Entity(name = "user")
class UserEntity(
        @Column(unique = true)
        @field:NotEmpty
        var user: String? = null,

        @field:NotEmpty
        var pass: String? = null,

        @field:NotEmpty
        var firstname: String? = null,

        @field:NotEmpty
        var lastname: String? = null,

        @Column(unique = true)
        @field:NotEmpty
        @field:Email
        var eMail: String? = null,

        @field:NotEmpty
        var description: String? = null,

        @field:NotNull
        var birthDate: LocalDate? = null,

        @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
        var photos: MutableList<UserPhotoEntity>? = null,

        @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
        var userEvents: MutableList<UserEventEntity>? = null,

        @field:JsonIgnore
        var jwtHash: String? = null

) : TimestampedEntity(), UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        authorities.add(SimpleGrantedAuthority("authorised"))
        return authorities
    }

    @JsonIgnore
    override fun isEnabled(): Boolean = true

    @JsonIgnore
    override fun getUsername(): String = this.user!!

    @JsonIgnore
    override fun getPassword(): String = this.pass!!

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = true

    @PrePersist
    override fun prePersist() {
        super.prePersist()
        if (jwtHash == null) {
            jwtHash = UUID.randomUUID().toString()
        }
    }
}

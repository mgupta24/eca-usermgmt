package com.eca.usermgmt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode
@Data
@Slf4j
@Table(name = "user_details")
public class User implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "createdOn",nullable = false,updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updatedOn",nullable = false)
    private LocalDateTime updatedOn;

    @Version
    private Long version;

    @Column(name = "userName",unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToOne
    @JoinColumn(name = "tenants_id")
    private Tenant tenant;

    @OneToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "userPhoneNo",unique = true)
    private Long userPhoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id")
    )
    private Set<Role> roles;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthority();
    }
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Indicates that the implementing object contains sensitive data,
     * which can be erased using the eraseCredentials method.
     */
    @Override
    public void eraseCredentials() {
        password = null;
    }
    @PrePersist
    public void setCreatedOn() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PostUpdate
    public void setUpdatedOn() {
        updatedOn = LocalDateTime.now();
    }

    private Collection<SimpleGrantedAuthority> getAuthority() {
        var grantedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        this.getRoles()
                .stream()
                .filter(Objects::nonNull)
                .forEach(role -> {
                    grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                    grantedAuthorities.addAll(getPermissions(role.getPermissions()));
                });
        return grantedAuthorities;
    }

    private  List<SimpleGrantedAuthority> getPermissions(List<Permission> permissions) {
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
                .collect(Collectors.toList());
    }
}

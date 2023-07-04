package com.eca.usermgmt.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_role")
@Slf4j
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"version","createdOn","updatedOn","users"})
public class Role implements GrantedAuthority, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@Column(name = "createdOn",nullable = false,updatable = false)
	private LocalDateTime createdOn;

	@Column(name = "updatedOn",nullable = false)
	private LocalDateTime updatedOn;

	private String roleName;

	@ManyToMany(mappedBy = "roles")
	private Set<User> users;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "roles_permissions",
			joinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
	)
	private List<Permission> permissions;

	@Override
	public String getAuthority() {
		log.info("### Role getAuthority ");
		if(CollectionUtils.isNotEmpty(permissions)) {
			return permissions
					.stream()
					.filter(Objects::nonNull)
					.map(Permission::getPermissionName)
					.collect(Collectors.joining(","));
		} else  {
			return this.roleName;
		}
	}

	@PostUpdate
	public void setUpdatedOn() {
		updatedOn = LocalDateTime.now();
	}
	@PrePersist
	public void setCreatedOn() {
		createdOn = LocalDateTime.now();
		updatedOn = LocalDateTime.now();
	}

}

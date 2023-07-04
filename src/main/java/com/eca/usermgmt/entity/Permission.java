package com.eca.usermgmt.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"version","createdOn","updatedOn","roles"})
public class Permission implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@Column(name = "createdOn",nullable = false,updatable = false)
	private LocalDateTime createdOn;

	@Column(name = "updatedOn",nullable = false)
	private LocalDateTime updatedOn;

	private String permissionName;

	@ManyToMany(mappedBy = "permissions",fetch = FetchType.LAZY)
	private Set<Role> roles;

	public Permission(String name) {
		this.permissionName = name;
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

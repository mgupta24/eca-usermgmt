package com.eca.usermgmt.entity;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.constraint.EmailId;
import com.eca.usermgmt.constraint.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Slf4j
@MappedSuperclass
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Owner.class, name = UserConstants.TYPE_OWNER),
        @JsonSubTypes.Type(value = Tenant.class, name = UserConstants.TYPE_TENANT),
        @JsonSubTypes.Type(value = Vendor.class, name = UserConstants.TYPE_VENDOR)
})
public abstract class BaseEntity implements Serializable {

    @Column(name = "createdOn",nullable = false,updatable = false)
    protected LocalDateTime createdOn;

    @Column(name = "updatedOn",nullable = false)
    protected LocalDateTime updatedOn;

    @Version
    protected Integer version;

    @Column(name = "firstName")
    protected String firstName;

    @Column(name = "lastName")
    protected String lastName;

    @Column(name = "phoneNo",unique = true)
    @PhoneNumber
    protected Long phoneNo;

    @NotNull
    @EmailId(message = "EmailId is not valid")
    @Column(name = "emailId",unique = true)
    protected String emailId;

    @Column(name = "addressLine")
    protected String addressLine;

    @Column(name = "city")
    protected String city;

    @Column(name = "state")
    protected String state;

    @Column(name = "zipCode")
    protected String zipCode;

    @Transient
    protected String password;

    @NotNull
    protected Long apartmentId;

    protected String apartmentName;

    @PrePersist
    protected void setOnCreation() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void setUpdatedOn() {
        updatedOn = LocalDateTime.now();
    }
}

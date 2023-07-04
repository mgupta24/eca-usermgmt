package com.eca.usermgmt.entity;

import com.eca.usermgmt.constants.UserConstants;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@JsonTypeName(UserConstants.TYPE_TENANT)
public class Tenant extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "cost")
    private BigDecimal apartmentCost;

    @Column(name = "leaseStartDate")
    private LocalDateTime leaseStartDate;

    @Column(name = "leaseEndsDate")
    @JsonSerialize(using = JsonSerializer.class)
    private LocalDateTime leaseEndDate;

    @Column(name = "type")
    private String type;

}

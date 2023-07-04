package com.eca.usermgmt.enums;

import java.util.Arrays;

public enum TypeOfUser {
    OWNER("Owner"), VENDOR("Vendor"), TENANT("Tenant"), ALL("All");
    final String type;
    TypeOfUser(String type) {
        this.type = type;
    }
    public static TypeOfUser getUserType(String type) {
        return Arrays.stream(TypeOfUser.values())
                .filter(userType -> userType.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot Found userType"));
    }
}

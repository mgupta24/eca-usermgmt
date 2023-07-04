package com.eca.usermgmt;

import com.eca.usermgmt.repository.OwnerRepository;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.service.PermissionService;
import com.eca.usermgmt.service.RoleService;
import com.eca.usermgmt.utils.CreateUserUtil;
import com.eca.usermgmt.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableEurekaClient
@EnableTransactionManagement
@EnableFeignClients
public class EcaUserManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcaUserManagementApplication.class,args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx, @Autowired UserRepository userService,
                                               @Autowired JsonUtils jsonUtils,
                                               @Autowired RoleService roleService,
                                               @Autowired OwnerRepository ownerRepository,
                                               @Autowired PermissionService permissionService) {
        return args -> CreateUserUtil.settingDefaultUserForTesting(userService, jsonUtils, roleService,ownerRepository,permissionService);
    }
}

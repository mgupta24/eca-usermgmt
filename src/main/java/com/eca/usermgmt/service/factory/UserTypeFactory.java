package com.eca.usermgmt.service.factory;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.dto.*;
import com.eca.usermgmt.dto.request.UserRegistrationRequest;
import com.eca.usermgmt.entity.Owner;
import com.eca.usermgmt.entity.Tenant;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.entity.Vendor;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.exception.UserManagementException;
import com.eca.usermgmt.feign.ApartmentFeignClient;
import com.eca.usermgmt.repository.OwnerRepository;
import com.eca.usermgmt.repository.TenantsRepository;
import com.eca.usermgmt.repository.VendorRepository;
import com.eca.usermgmt.service.UserService;
import com.eca.usermgmt.service.notifiation.NotificationService;
import com.eca.usermgmt.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Optional;

import static com.eca.usermgmt.dto.KafkaMessageDTO.EventType.CREATE;
import static com.eca.usermgmt.utils.DateUtils.stringToLocalDateTime;

@Slf4j
@Component
public class UserTypeFactory {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Validator validator;
    @Autowired
    private TenantsRepository tenantsRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired(required = false)
    private NotificationService kafkaNotificationService;
    @Autowired
    private JsonUtils jsonUtils;
    @Value("${app.kafka.enabled}")
    private boolean kafkaEnabled;

    @Autowired
    private ApartmentFeignClient apartmentFeignClient;

    public UserRegistrationResponseDTO createUserType(UserRegistrationRequest userRegistrationRequest) {
        var type = userRegistrationRequest.getType();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        if (StringUtils.equalsIgnoreCase(type, TypeOfUser.OWNER.toString())) {
            var ownerEntity = modelMapper.map(userRegistrationRequest, Owner.class);
            ownerEntity.setType(TypeOfUser.OWNER.toString());
            var apartmentDetails = findApartmentDetails(userRegistrationRequest.getApartmentId());
            ownerEntity.setApartmentId(apartmentDetails.getApartmentId());
            ownerEntity.setApartmentName(apartmentDetails.getApartmentName());
            return registerOwnerType(ownerEntity);
        } else if (StringUtils.equalsIgnoreCase(type, TypeOfUser.TENANT.toString())) {
            var entity = modelMapper.map(userRegistrationRequest, Tenant.class);
            var constraintViolations = validator.validate(entity);
            if (!constraintViolations.isEmpty()) {
                log.error("UserTypeFactory::saveTenants {}",constraintViolations);
                throw new ConstraintViolationException(constraintViolations);
            }
            var leaseStartDate = stringToLocalDateTime(userRegistrationRequest.getLeaseStartDate());
            var leaseEndDate = stringToLocalDateTime(userRegistrationRequest.getLeaseEndDate());
            entity.setLeaseStartDate(leaseStartDate);
            entity.setLeaseEndDate(leaseEndDate);
            entity.setType(TypeOfUser.TENANT.toString());
            var apartmentDetails = findApartmentDetails(userRegistrationRequest.getApartmentId());
            entity.setApartmentId(apartmentDetails.getApartmentId());
            entity.setApartmentName(apartmentDetails.getApartmentName());
            return registerTenantType(entity);
        } else if (StringUtils.equalsIgnoreCase(type, TypeOfUser.VENDOR.toString())) {
            var vendorObject = modelMapper.map(userRegistrationRequest, Vendor.class);
            vendorObject.setType(TypeOfUser.VENDOR.toString());
            var apartmentDetails = findApartmentDetails(userRegistrationRequest.getApartmentId());
            vendorObject.setApartmentId(apartmentDetails.getApartmentId());
            vendorObject.setApartmentName(apartmentDetails.getApartmentName());
            return registerVendorType(vendorObject);
        } else {
            throw new UserManagementException(UserConstants.INVALID_USER_TYPE);
        }
    }

    private UserRegistrationResponseDTO registerOwnerType(Owner ownerEntity) {
        var savedEntity = ownerRepository.save(ownerEntity);
        log.info("UserTypeFactory::registerOwnerType saved owner type in DB {}",savedEntity);
        return Optional.of(savedEntity)
                .map(owner -> {
                    var userDetails = User.builder()
                            .username(owner.getEmailId())
                            .password(owner.getPassword())
                            .userPhoneNumber(owner.getPhoneNo())
                            .owner(owner).build();
                    userService.saveUserWithDefaultRoles(userDetails);
                    log.info("Save Owner In UserDetails {} ", owner);
                    pushNotification(userDetails);
                    return prepareResponse(userDetails, owner.getType());
                })
                .orElseThrow(() -> new UserManagementException(UserConstants.USER_REGISTRATION_ERROR));
    }

    private UserRegistrationResponseDTO registerVendorType(Vendor vendorObject) {
        var savedEntity = vendorRepository.save(vendorObject);
        log.info("UserTypeFactory::registerVendorType saved vendor type in DB {}",savedEntity);
        return Optional.of(savedEntity)
                .map(vendor -> {
                    var userDetails = User.builder()
                            .username(vendor.getEmailId())
                            .password(vendor.getPassword())
                            .userPhoneNumber(vendor.getPhoneNo())
                            .vendor(vendor).build();
                    userService.saveUserWithDefaultRoles(userDetails);
                    log.info("Save Vendor In UserDetails {} ", userDetails);
                    pushNotification(userDetails);
                    return prepareResponse(userDetails, vendor.getType());
                })
                .orElseThrow(() -> new UserManagementException(UserConstants.USER_REGISTRATION_ERROR));
    }

    private UserRegistrationResponseDTO registerTenantType(Tenant entity) {
        var savedEntity = tenantsRepository.save(entity);
        log.info("UserTypeFactory::registerTenantType saved tenant type in DB {}",savedEntity);
        return Optional.of(savedEntity)
                .map(tenants -> {
                    var user = new User();
                    user.setUsername(tenants.getEmailId());
                    user.setPassword(tenants.getPassword());
                    user.setTenant(tenants);
                    user.setUserPhoneNumber(tenants.getPhoneNo());
                    userService.saveUserWithDefaultRoles(user);
                    log.info("Save Tenants In UserDetails {} ", user);
                    pushNotification(user);
                    return prepareResponse(user, tenants.getType());
                }).orElseThrow(() -> new UserManagementException(UserConstants.USER_REGISTRATION_ERROR));
    }

    private void pushNotification(User user) {
        if (kafkaEnabled) {
            var kafkaMessageDTO = KafkaMessageDTO.builder()
                    .userId(user.getUsername())
                    .build();
            kafkaMessageDTO.setEventType(CREATE);
            buildVendorOwnerTenant(kafkaMessageDTO,user);
            kafkaNotificationService.sendNotification(jsonUtils.toJson(kafkaMessageDTO));
        }
    }

    private void buildVendorOwnerTenant(KafkaMessageDTO kafkaMessageDTO, User user) {
        if(user.getTenant() != null) {
            kafkaMessageDTO.setTenant(modelMapper.map(user.getTenant(),TenantsDTO.class));
            kafkaMessageDTO.getTenant().setId(null);
        } else if( user.getVendor() !=null ) {
            kafkaMessageDTO.setVendor(jsonUtils.convertObject(user.getVendor(),VendorDTO.class));
            kafkaMessageDTO.getVendor().setId(null);
        } else if (user.getOwner() !=null) {
            kafkaMessageDTO.setOwner(modelMapper.map(user.getOwner(),OwnerDTO.class));
            kafkaMessageDTO.getOwner().setId(null);
        }
    }

    private UserRegistrationResponseDTO prepareResponse(User user, String type) {
        return  UserRegistrationResponseDTO.builder()
                .username(user.getUsername())
                .type(type)
                .build();
    }

    private ApartmentDetailsDTO findApartmentDetails(Long apartmentId) {
        Optional<ApartmentDetailsDTO> apartmentById = apartmentFeignClient.getApartmentById(apartmentId);
        log.info("findApartmentDetails feign call {} ",apartmentById);
        if(apartmentById.isEmpty()) {
            log.error("Unable to find details with "+apartmentById);
            throw new UserManagementException("Unable to find details with : "+apartmentId);
        }
        return apartmentById.get();
    }

}

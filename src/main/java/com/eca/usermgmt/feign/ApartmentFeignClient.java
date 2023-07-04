package com.eca.usermgmt.feign;

import com.eca.usermgmt.dto.ApartmentDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "catalog-service", url = "${catalog-service.baseUrl}")
public interface ApartmentFeignClient {
    @GetMapping(value = "/v1/catalog/getapartment")
    Optional<ApartmentDetailsDTO> getApartmentById(@RequestParam(name = "apartmentId") Long apartmentId);

}

package dev.gerardo.shippingapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import dev.gerardo.shippingapp.service.PackageTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class PackageTypeController {

    private static final Logger logger = LoggerFactory.getLogger(PackageTypeController.class);
    private PackageTypeService packageTypeService;

    public PackageTypeController(PackageTypeService packageTypeService) {
        this.packageTypeService = packageTypeService;
    }

    @GetMapping(value = "/type")
    public ResponseEntity<?> getPackageTypes() {
        List<PackageType> types;
        List<String> uiPackageTypes;
        try {
            types = packageTypeService.getPackageTypes();
            uiPackageTypes = types.stream().map(PackageType::getDescription).collect(Collectors.toList());
        } catch (UnavailableServiceException | JsonProcessingException exc) {
            logger.error("An error occured fetching data");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching data", exc);
        }
        return new ResponseEntity<>(uiPackageTypes, HttpStatus.OK);
    }

}

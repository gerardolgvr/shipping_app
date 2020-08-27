package dev.gerardo.shippingapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import dev.gerardo.shippingapp.service.PackageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class PackageTypeController {

    @Autowired
    PackageTypeService packageTypeService;

    @CrossOrigin
    @GetMapping(value = "/type")
    public ResponseEntity<?> getPackageTypes() {
        List<PackageType> types;
        List<String> uiPackageTypes;
        try {
            types = packageTypeService.requestAndReceive();
            uiPackageTypes = packageTypeService.getUiPackageTypes(types);
        } catch (UnavailableServiceException | JsonProcessingException exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching data", exc);
        }
        return new ResponseEntity<>(uiPackageTypes, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping(value = "/home")
    public ResponseEntity<String> testHome() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

}

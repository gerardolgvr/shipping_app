package dev.gerardo.shippingapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.gerardo.shippingapp.domain.PackageSize;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import dev.gerardo.shippingapp.service.PackageSizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PackageSizeController {

    private static final Logger logger = LoggerFactory.getLogger(PackageSizeController.class);
    private PackageSizeService packageSizeService;


    public PackageSizeController(PackageSizeService packageSizeService) {
        this.packageSizeService = packageSizeService;
    }

    @GetMapping(value = "/size/{type}")
    public ResponseEntity<?> getPackageSizes(@PathVariable(value = "type") String type) {
        List<PackageSize> sizes;
        List<String> uiPackageSizes;
        try {
            sizes = packageSizeService.getPackageSizes();
            uiPackageSizes = sizes.stream().map(PackageSize::getDescription).collect(Collectors.toList());
        } catch (UnavailableServiceException | JsonProcessingException exc) {
            logger.error("An error occured fetching package size data");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching data", exc);
        }
        return new ResponseEntity<>(uiPackageSizes, HttpStatus.OK);
    }
}

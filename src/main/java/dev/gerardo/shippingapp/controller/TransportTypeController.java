package dev.gerardo.shippingapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import dev.gerardo.shippingapp.service.TransportTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class TransportTypeController {

    private static final Logger logger = LoggerFactory.getLogger(TransportTypeController.class);
    private final TransportTypeService transportTypeService;


    public TransportTypeController(TransportTypeService transportTypeService) {
        this.transportTypeService = transportTypeService;
    }

    @GetMapping(value = "/transport/{size}")
    public ResponseEntity<?> getTransportTypes(@PathVariable(value = "size") String size) {
        List<String> uiTransportTypes;
        try {
            uiTransportTypes = transportTypeService.getTransportTypes();
        } catch (UnavailableServiceException | JsonProcessingException exc) {
            logger.error("An error occured fetching transport types data");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching data", exc);
        }
        return new ResponseEntity<>(uiTransportTypes, HttpStatus.OK);
    }
}

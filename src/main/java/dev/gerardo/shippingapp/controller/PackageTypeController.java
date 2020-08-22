package dev.gerardo.shippingapp.controller;

import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.service.PackageTypeService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
public class PackageTypeController {

    @Autowired
    PackageTypeService packageTypeService;

    @CrossOrigin
    @GetMapping(value = "/type")
    public ResponseEntity<List<String>> getPackageTypes() throws JSONException {
        List<PackageType> types = packageTypeService.requestAndReceive();
        List<String> uiPackageTypes = packageTypeService.getUiPackageTypes(types);
        return new ResponseEntity<>(uiPackageTypes, HttpStatus.OK);
    }
}

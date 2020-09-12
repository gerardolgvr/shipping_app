package dev.gerardo.shippingapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportType {

    private int id;
    private String description;
    private int pricePerMile;

}

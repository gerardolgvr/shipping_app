package dev.gerardo.shippingapp.service;

import dev.gerardo.shippingapp.domain.PackageType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PackageTypeService {

    private List<PackageType> packageTypes;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${shippingapp.rabbitmq.exchange}")
    private String exchange;

    @Value("${shippingapp.rabbitmq.routingkey}")
    private String routingKey;

    public List<PackageType> requestAndReceive() throws JSONException {
        String request = "{\"type\":\"packageType\"}";
        boolean noData = true;
        String response = null;

        while (noData) {
            response = String.valueOf(rabbitTemplate.convertSendAndReceive(exchange, routingKey, request));
            noData = (response.equals("null")) ? true : false;
        }

        packageTypes = parseToPackageTypes(response);
        return packageTypes;
    }

    public List<String> getUiPackageTypes(List<PackageType> typesList) {
        List<String> uiPackageTypes = new LinkedList<>();

        for(int i = 0; i < typesList.size(); i++) {
            uiPackageTypes.add(typesList.get(i).getDescription());
        }

        return uiPackageTypes;
    }

    public List<PackageType> parseToPackageTypes(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        List<PackageType> types = new LinkedList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            PackageType packageType = new PackageType(Integer.parseInt(String.valueOf(object.get("id"))), String.valueOf(object.get("description")), Float.parseFloat(String.valueOf(object.get("price"))));
            types.add(packageType);
        }

        return types;
    }

    public List<PackageType> getPackageTypes() {
        return packageTypes;
    }

    public void setPackageTypes(List<PackageType> packageTypes) {
        this.packageTypes = packageTypes;
    }
}

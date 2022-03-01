package guru.springframework.msscbeerservice.service.inventory;

import guru.springframework.msscbeerservice.service.inventory.model.BeerInventoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Profile("localmysql")
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
@Component
public class BeerInventoryServiceRestTemplate implements BeerInventoryService {


    private static final String INVENTORY_PATH = "/api/v1/beer/{beerId}/inventory";
    private final RestTemplate restTemplate;

    private String beerInventoryServiceHost;

    @Autowired
    public BeerInventoryServiceRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Integer getOnHandInventory(UUID beerId) {

        ResponseEntity<List<BeerInventoryDto>> responseEntity = restTemplate
            .exchange(beerInventoryServiceHost + INVENTORY_PATH, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BeerInventoryDto>>(){}, (Object) beerId);

        return Objects.requireNonNull(responseEntity.getBody())
            .stream()
            .mapToInt(BeerInventoryDto::getQuantityOnHand)
            .sum();

    }

    public void setBeerInventoryServiceHost(String beerInventoryServiceHost) {
        this.beerInventoryServiceHost = beerInventoryServiceHost;
    }
}

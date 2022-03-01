package guru.springframework.msscbeerservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.events.BrewBeerEvent;
import guru.springframework.msscbeerservice.repository.BeerRepository;
import guru.springframework.msscbeerservice.service.inventory.BeerInventoryService;
import guru.springframework.msscbeerservice.web.mapper.BeerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static guru.springframework.msscbeerservice.config.JmsConfig.BREWERING_REQUEST_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService {

    private final BeerRepository beerRepository;
    private final BeerInventoryService beerInventoryService;
    private final JmsTemplate jmsTemplate;
    private final BeerMapper beerMapper;

    @Scheduled(fixedRate = 5000)
    public void checkForLowInventory() {

        List<Beer> beers = beerRepository.findAll();

        beers.forEach(beer -> {

            Integer inventoryQH = beerInventoryService.getOnHandInventory(beer.getId());

            log.debug("Min onHand is: " + beer.getMinOnHand());
            log.debug("Inventory: " + inventoryQH);

            if (beer.getMinOnHand() < inventoryQH) {
                jmsTemplate.convertAndSend(BREWERING_REQUEST_QUEUE,
                    new BrewBeerEvent(beerMapper.beerToBeerDto(beer)));
            }
        });


    }
}

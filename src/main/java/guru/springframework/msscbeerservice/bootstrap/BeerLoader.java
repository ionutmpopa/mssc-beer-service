package guru.springframework.msscbeerservice.bootstrap;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repository.BeerRepository;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class BeerLoader implements CommandLineRunner {

    private final BeerRepository beerRepository;

    public BeerLoader(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Override
    public void run(String... args) {
        loadBeerObjects();
    }


    private void loadBeerObjects() {

        if (beerRepository.count() == 0) {
            beerRepository.save(Beer.builder()
                .beerName("Mango Bobs")
                .beerStyle(BeerStyleEnum.IPA)
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .quantityToBrew(200)
                .minOnHand(12)
                .upc("0631234200036")
                .price(new BigDecimal("12.95"))
                .version(1L)
                .build());

            beerRepository.save(Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .quantityToBrew(200)
                .minOnHand(12)
                .upc("0631234300019")
                .price(new BigDecimal("12.95"))
                .version(1L)
                .build());

            beerRepository.save(Beer.builder()
                .beerName("Pinball Porter")
                .beerStyle(BeerStyleEnum.PORTER)
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .quantityToBrew(200)
                .minOnHand(12)
                .upc("0083783375213")
                .price(new BigDecimal("12.95"))
                .version(1L)
                .build());
        }
        log.info("Loaded beers: {}", beerRepository.count());
    }
}

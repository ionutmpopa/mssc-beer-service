package guru.springframework.msscbeerservice.bootstrap;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repository.BeerRepository;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;

@Slf4j
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
                .beerName("Budweiser")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .quantityToBrew(200)
                .minOnHand(12)
                .upc("33212544")
                .price(new BigDecimal("12.95"))
                .build());

            beerRepository.save(Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .quantityToBrew(200)
                .minOnHand(12)
                .upc("33219544")
                .price(new BigDecimal("11.95"))
                .build());
        }
        log.info("Loaded beers: {}", beerRepository.count());
    }
}

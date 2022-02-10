package guru.springframework.msscbeerservice.service;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repository.BeerRepository;
import guru.springframework.msscbeerservice.web.exception.NotFoundException;
import guru.springframework.msscbeerservice.web.mapper.BeerMapper;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerPagedList;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    public BeerServiceImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }

    @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false")
    @Override
    public BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, boolean showInventoryOnHand) {
        Page<Beer> beerPage;

        log.info("It is called");

        if (beerName != null && beerStyle != null) {
            beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
        } else if (beerName == null && beerStyle != null) {
            beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
        } else if (beerName != null) {
            beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
        } else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        Function<Beer, BeerDto> beerToBeerDtoFunction;

        if (showInventoryOnHand) {
            beerToBeerDtoFunction = beerMapper::beerToBeerDtoWithInventory;
        } else {
            beerToBeerDtoFunction = beerMapper::beerToBeerDto;
        }

        return new BeerPagedList(beerPage
            .getContent()
            .stream()
            .map(beerToBeerDtoFunction)
            .collect(Collectors.toList()),
            PageRequest
                .of(beerPage.getPageable().getPageNumber(),
                    beerPage.getPageable().getPageSize()),
            beerPage.getTotalElements());
    }

    @Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false")
    @Override
    public BeerDto getById(UUID beerId, boolean showInventoryOnHand) {
        if (showInventoryOnHand) {
            return beerMapper.beerToBeerDtoWithInventory(
                beerRepository.findById(beerId).orElseThrow(NotFoundException::new)
            );
        } else {
            return beerMapper.beerToBeerDto(
                beerRepository.findById(beerId).orElseThrow(NotFoundException::new)
            );
        }
    }

    @Cacheable(cacheNames = "beerUpcCache", key = "#upc", condition = "#showInventoryOnHand == false")
    @Override
    public BeerDto getByUpc(final String upc, boolean showInventoryOnHand) {

        log.info("Get beer by UPC!");

        if (showInventoryOnHand) {
            return beerMapper.beerToBeerDtoWithInventory(
                beerRepository.findByUpc(upc).orElseThrow(NotFoundException::new)
            );
        } else {
            return beerMapper.beerToBeerDto(
                beerRepository.findByUpc(upc).orElseThrow(NotFoundException::new)
            );
        }
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        Beer beer = beerMapper.beerDtoToBeer(beerDto);
        return beerMapper.beerToBeerDto(beerRepository.save(beer));
    }

    @Override
    public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {

        Beer beer = beerRepository.findById(beerId).orElseThrow(NotFoundException::new);
        beer.setBeerName(beerDto.getBeerName());
        beer.setBeerStyle(beerDto.getBeerStyle());
        beer.setPrice(beerDto.getPrice());
        beer.setUpc(beerDto.getUpc());
        return beerMapper.beerToBeerDto(beerRepository.save(beer));

    }
}

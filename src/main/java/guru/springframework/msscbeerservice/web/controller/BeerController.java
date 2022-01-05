package guru.springframework.msscbeerservice.web.controller;

import guru.springframework.msscbeerservice.web.model.BeerDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/beer")
@RestController
public class BeerController {

    @GetMapping("/beerId")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable("beerId") final UUID beerId) {

        //todo impl
        return new ResponseEntity<>(BeerDto.builder().build(), HttpStatus.OK);
    }

    @PostMapping
    private ResponseEntity<BeerDto> saveNewBeer(@RequestBody final BeerDto beerDto) {

        //todo impl
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/beerId")
    public ResponseEntity<BeerDto> updateBeerById(@PathVariable("beerId") final UUID beerId, @RequestBody BeerDto beerDto) {

        //todo impl
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

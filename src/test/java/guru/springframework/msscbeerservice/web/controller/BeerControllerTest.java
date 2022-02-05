package guru.springframework.msscbeerservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbeerservice.service.BeerService;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "localhost", uriPort = 8080)
@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    @Test
    void getBeerById() throws Exception {

        when(beerService.getById(any(), anyBoolean())).thenReturn(getDefaultBeer());

        mockMvc.perform(get("/api/v1/beer/{beerId}", UUID.randomUUID())
                .param("isCold", "yes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("v1/beer-get",
                pathParameters(
                    parameterWithName("beerId").description("UUID of desired beer to get")
                ),
                requestParameters(
                    parameterWithName("isCold").description("Is Beer Cold Query param.")
                ),
                responseFields(
                    fieldWithPath("id").description("Id of beer"),
                    fieldWithPath("version").description("Version number"),
                    fieldWithPath("createdDate").description("Date created"),
                    fieldWithPath("lastModifiedDate").description("Date modified"),
                    fieldWithPath("beerName").description("Beer Name"),
                    fieldWithPath("beerStyle").description("Beer Style"),
                    fieldWithPath("upc").description("UPC of beer"),
                    fieldWithPath("price").description("Price"),
                    fieldWithPath("quantityToBrew").description("Quantity On Hand")
                )));

    }

    @Test
    void saveNewBeer() throws Exception {
        String beerDtoJson = objectMapper.writeValueAsString(createBeerDto());


        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);

        mockMvc.perform(post("/api/v1/beer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
            .andExpect(status().isCreated())
            .andDo(document("v1/beer-new",
                requestFields(
                    fields.withPath("id").ignored(),
                    fields.withPath("version").ignored(),
                    fields.withPath("createdDate").ignored(),
                    fields.withPath("lastModifiedDate").ignored(),
                    fields.withPath("beerName").description("Name of the beer"),
                    fields.withPath("beerStyle").description("Beer Style"),
                    fields.withPath("upc").description("UPC of beer"),
                    fields.withPath("price").description("Price"),
                    fields.withPath("quantityToBrew").description("Quantity On Hand")
                )));
    }

    @Test
    void updateBeerById() throws Exception {
        String beerDtoJson = objectMapper.writeValueAsString(createBeerDto());

        mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
            .andExpect(status().isNoContent());

    }

    private BeerDto createBeerDto() {
        return BeerDto.builder()
            .beerName("Ursus")
            .beerStyle(BeerStyleEnum.PALE_ALE)
            .price(new BigDecimal("11.25"))
            .quantityToBrew(5)
            .upc(5L)
            .build();
    }

    private BeerDto getDefaultBeer() {
        return BeerDto.builder()
            .beerName("Name")
            .beerStyle(BeerStyleEnum.ALE)
            .upc(1234567L)
            .price(new BigDecimal("0.00"))
            .quantityToBrew(5)
            .build();
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                .collectionToDelimitedString(this.constraintDescriptions
                    .descriptionsForProperty(path), ". ")));
        }
    }

}

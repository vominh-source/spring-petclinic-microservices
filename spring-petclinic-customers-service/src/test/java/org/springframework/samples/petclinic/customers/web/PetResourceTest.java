package org.springframework.samples.petclinic.customers.web;

import java.util.Optional;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.samples.petclinic.customers.config.MetricConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.customers.CustomersServiceApplication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when; // Thêm import này
import org.springframework.beans.factory.annotation.Autowired; // Thêm import này

@Autowired
ApplicationContext context;


/**
 * @author Maciej Szarlinski
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PetRepository petRepository;

    @MockBean
    OwnerRepository ownerRepository;

    @Test
    void shouldGetAPetInJSonFormat() throws Exception {

        Pet pet = setupPet();

        given(petRepository.findById(2)).willReturn(Optional.of(pet));


        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    // My own test
    @Test
    void shouldGetAPetInJsonFormat() throws Exception {
        Pet pet = setupPet();
        given(petRepository.findById(2)).willReturn(Optional.of(pet));

        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    @Test
    void shouldReturnNotFoundWhenPetDoesNotExist() throws Exception {
        given(petRepository.findById(99)).willReturn(Optional.empty());

        mvc.perform(get("/owners/2/pets/99").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        given(ownerRepository.findById(99)).willReturn(Optional.empty());

        mvc.perform(get("/owners/99/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    
    // My own test

    private Pet setupPet() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet pet = new Pet();

        pet.setName("Basil");
        pet.setId(2);

        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);

        owner.addPet(pet);
        return pet;
    }

    @Test
    void shouldMapOwnerRequestToOwner() {
        OwnerEntityMapper mapper = new OwnerEntityMapper();

        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Springfield", "1234567890");
        Owner owner = new Owner();

        Owner updatedOwner = mapper.map(owner, request);

        assertEquals("John", updatedOwner.getFirstName());
        assertEquals("Doe", updatedOwner.getLastName());
        assertEquals("123 Main St", updatedOwner.getAddress());
        assertEquals("Springfield", updatedOwner.getCity());
        assertEquals("1234567890", updatedOwner.getTelephone());
    }

    

    //add
    @Test
    void shouldCreateValidPetRequest() {
        Date birthDate = new Date();
        PetRequest petRequest = new PetRequest(1, birthDate, "Buddy", 5);
        
        assertEquals(1, petRequest.id());
        assertEquals(birthDate, petRequest.birthDate());
        assertEquals("Buddy", petRequest.name());
        assertEquals(5, petRequest.typeId());
    }

    @Test
    void shouldNotAllowEmptyNameInPetRequest() {
        PetRequest petRequest = new PetRequest(1, new Date(), "", 5);
        assertEquals("", petRequest.name()); // Kiểm tra tên có bị rỗng không
    }



}



  


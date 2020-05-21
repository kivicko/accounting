package com.kivilcimeray.accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kivilcimeray.accounting.AccountingApplication;
import com.kivilcimeray.accounting.dto.TransactionDTO;
import com.kivilcimeray.accounting.model.Player;
import com.kivilcimeray.accounting.repository.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountingApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PlayerRepository playerRepository;

    private ObjectWriter ow;

    @Before
    public void before() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void shouldReturnPlayerBalanceWhenGivenIdExist() throws Exception {
        Player player = new Player(123L, $(100));
        saveSamplePlayer(player);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(get("/balance/123").headers(header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.playerID", is(123)))
                .andExpect(jsonPath("$.currentBalance", is(100.00)));
    }

    @Test
    public void shouldReturnPlayerTransactionLogsWhenGivenIdExist() throws Exception {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(get("/log/5").headers(header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].playerID", is(5)))
                .andExpect(jsonPath("$[0].amount", is(200.0)))
                .andExpect(jsonPath("$[1].playerID", is(5)))
                .andExpect(jsonPath("$[1].amount", is(100.0)));
    }

    @Test
    public void shouldThrowExceptionWhenBalanceIsNegative() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .amount($(-123))
                .playerID(123L)
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");


        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
                .andExpect(jsonPath("$.message", is("The amount should be greater than zero.")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldThrowExceptionWhenTransactionCodeIsNull() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .amount($(123))
                .playerID(123L)
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
                .andExpect(jsonPath("$.message", is("The transactionCode must be specified.")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldThrowExceptionWhenPlayerIDIsNull() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .amount($(123))
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
                .andExpect(jsonPath("$.message", is("The playerID should be specified.")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldThrowExceptionWhenPlayerIsNotFound() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .playerID(1234L)
                .amount($(123))
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("player not found for playerID : 1234")));
    }

    @Test
    public void shouldThrowExceptionWhenBalanceIsNegativeOnWithdraw() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .amount($(-123))
                .playerID(123L)
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");


        mvc.perform(post("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
                .andExpect(jsonPath("$.message", is("The amount should be greater than zero.")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldThrowExceptionWhenTransactionCodeIsNullOnWithdraw() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .amount($(123))
                .playerID(123L)
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
                .andExpect(jsonPath("$.message", is("The transactionCode must be specified.")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldThrowExceptionWhenPlayerIDIsNullOnWithdraw() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .amount($(123))
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
                .andExpect(jsonPath("$.message", is("The playerID should be specified.")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldThrowExceptionWhenPlayerIsNotFoundOnWithdraw() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .playerID(1234L)
                .amount($(123))
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("player not found for playerID : 1234")));
    }

    @Test
    public void shouldUpdatePlayerBalanceWhenCreditOperationValid() throws Exception {
        Player player = new Player(99L, $(0));
        saveSamplePlayer(player);

        TransactionDTO sampleDTO = TransactionDTO.builder()
                .playerID(99L)
                .amount($(123))
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerID", is(99)))
                .andExpect(jsonPath("$.currentBalance", is(123.00)));
    }

    @Test
    public void shouldUpdatePlayerBalanceWhenWithdrawOperationValid() throws Exception {
        Player player = new Player(99L, $(123));
        saveSamplePlayer(player);

        TransactionDTO sampleDTO = TransactionDTO.builder()
                .playerID(99L)
                .amount($(123))
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerID", is(99)))
                .andExpect(jsonPath("$.currentBalance", is(0.0)));
    }

    @Test
    public void shouldThrowExWhenPlayerBalanceNotEnoughForWithdraw() throws Exception {
        Player player = new Player(99L, $(100));
        saveSamplePlayer(player);

        TransactionDTO sampleDTO = TransactionDTO.builder()
                .playerID(99L)
                .amount($(123))
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=");

        mvc.perform(post("/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status", is("NOT_ACCEPTABLE")))
                .andExpect(jsonPath("$.message", is("Player Balance not enough for Withdrawal. Current balance : 100.00")));
    }

    @Test
    public void shouldThrowExceptionWhenApiUserRoleIsNotAdminOnCredit() throws Exception {
        TransactionDTO sampleDTO = TransactionDTO.builder()
                .amount($(1))
                .playerID(123L)
                .transactionCode(UUID.randomUUID())
                .build();

        String requestJson = ow.writeValueAsString(sampleDTO);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Basic dXNlcjp1c2Vy"); //user auth

        mvc.perform(post("/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .headers(header))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private void saveSamplePlayer(Player player) {
        playerRepository.save(player);
    }

}
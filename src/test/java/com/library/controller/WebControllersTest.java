package com.library.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import java.util.stream.Collectors;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class WebControllersTest {

  private final MockMvc mockMvc;

  @Autowired
  public WebControllersTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  public void login() throws Exception {
    MvcResult result = mockMvc
        .perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"admin@email.ro\", \"password\": \"1234\"}"))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(result.getResponse().getContentAsString()).isNotEmpty();
  }

  @Test
  public void createUpdateGetDeleteCustomer() throws Exception {
    MvcResult createResult = mockMvc
        .perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"new_user@email.ro\", \"password\": \"1234\"}"))
        .andExpect(status().isCreated())
        .andReturn();
    int id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

    MvcResult loginResult = mockMvc
        .perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"new_user@email.ro\", \"password\": \"1234\"}"))
        .andExpect(status().isOk())
        .andReturn();
    Object jwt = loginResult.getResponse().getHeaderValue(HttpHeaders.AUTHORIZATION);

    mockMvc
        .perform(put("/api/customers/" + id)
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"updated_user@email.ro\", \"password\": \"12345\"}"))
        .andExpect(status().isOk());

    MvcResult loginResult2 = mockMvc
        .perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"updated_user@email.ro\", \"password\": \"12345\"}"))
        .andExpect(status().isOk())
        .andReturn();
    Object jwt2 = loginResult2.getResponse().getHeaderValue(HttpHeaders.AUTHORIZATION);

    MvcResult getAllCustomersResult = mockMvc
        .perform(get("/api/customers")
            .header(HttpHeaders.AUTHORIZATION, jwt2))
        .andExpect(status().isOk()).andReturn();
    JSONArray jArray = JsonPath
        .read(getAllCustomersResult.getResponse().getContentAsString(), "$.[*].username");

    List<String> usernames = jArray.stream().map(Object::toString).collect(Collectors.toList());

    assertThat(usernames).contains("updated_user@email.ro");
    assertThat(usernames.size()).isEqualTo(2);

    mockMvc.perform(delete("/api/customers/" + id).header(HttpHeaders.AUTHORIZATION, jwt2))
        .andExpect(status().isNoContent());
  }

  @Test
  public void createCustomerWrongEmail() throws Exception {
    MvcResult createResult = mockMvc
        .perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"new_user\", \"password\": \"1234\"}"))
        .andExpect(status().isBadRequest())
        .andReturn();
    assertThat(createResult.getResponse().getContentAsString()).isNotEmpty();
    assertThat(createResult.getResponse().getContentAsString()).contains("Invalid email provided");
  }

  @Test
  public void createUpdateAndGetBook() throws Exception {
    MvcResult loginResult = mockMvc
        .perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"admin@email.ro\", \"password\": \"1234\"}"))
        .andExpect(status().isOk())
        .andReturn();
    Object jwt = loginResult.getResponse().getHeaderValue(HttpHeaders.AUTHORIZATION);

    MvcResult createResult = mockMvc
        .perform(post("/api/books")
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"My Book\"}"))
        .andExpect(status().isCreated())
        .andReturn();
    int id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(put("/api/books/" + id)
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"My new book\"}"))
        .andExpect(status().isOk());

    MvcResult getAllCustomersResult = mockMvc
        .perform(get("/api/books")
            .header(HttpHeaders.AUTHORIZATION, jwt))
        .andExpect(status().isOk()).andReturn();
    JSONArray jArray = JsonPath
        .read(getAllCustomersResult.getResponse().getContentAsString(), "$.[*].title");

    List<String> titles = jArray.stream().map(Object::toString).collect(Collectors.toList());

    assertThat(titles).contains("My new book");
    assertThat(titles.size()).isEqualTo(1);

    mockMvc.perform(delete("/api/books").header(HttpHeaders.AUTHORIZATION, jwt))
        .andExpect(status().isNoContent());
  }

}
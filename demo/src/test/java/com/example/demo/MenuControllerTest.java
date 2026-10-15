package com.example.demo;

import com.example.demo.repository.InMemoryMenuRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class MenuControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private InMemoryMenuRepository repository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        repository.deleteAll();
    }

    @Test
    void testServerStartsEmpty() throws Exception {
        mockMvc.perform(get("/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCreateMenuItemAndRetrieveIt() throws Exception {
        String newMenuItem = """
                {
                    "name": "Margherita Pizza",
                    "description": "Tomato sauce, fresh mozzarella, basil",
                    "price": 12.99,
                    "category": "Pizza",
                    "available": true
                }
                """;

        // POST /menu-items
        MvcResult postResult = mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMenuItem))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.price").value(12.99))
                .andExpect(jsonPath("$.category").value("Pizza"))
                .andExpect(jsonPath("$.available").value(true))
                .andReturn();

        Number id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

        // GET all items
        mockMvc.perform(get("/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Margherita Pizza"));

        // GET by ID
        mockMvc.perform(get("/menu-items/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Margherita Pizza"));
    }

    @Test
    void testPathAliases() throws Exception {
        String item = """
                {
                    "name": "Tiramisu",
                    "description": "Traditional Italian coffee dessert",
                    "price": 7.50,
                    "category": "Dessert"
                }
                """;

        // POST via /menu alias
        mockMvc.perform(post("/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(item))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tiramisu"));

        // GET via /api/menu alias
        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // GET via /items alias
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testCustomAndDynamicFieldsPreserved() throws Exception {
        String customItem = """
                {
                    "name": "Spicy Wings",
                    "price": 9.99,
                    "spicyLevel": 3,
                    "chefSpecial": true,
                    "sauceOptions": ["BBQ", "Buffalo"]
                }
                """;

        mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customItem))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Spicy Wings"))
                .andExpect(jsonPath("$.spicyLevel").value(3))
                .andExpect(jsonPath("$.chefSpecial").value(true));
    }

    @Test
    void testUpdateMenuItem() throws Exception {
        String initial = """
                {
                    "name": "Veggie Burger",
                    "price": 10.00,
                    "category": "Burgers"
                }
                """;

        MvcResult postResult = mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initial))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

        String update = """
                {
                    "name": "Supreme Veggie Burger",
                    "price": 11.50,
                    "category": "Burgers",
                    "available": true
                }
                """;

        mockMvc.perform(put("/menu-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Supreme Veggie Burger"))
                .andExpect(jsonPath("$.price").value(11.50));
    }

    @Test
    void testPatchMenuItem() throws Exception {
        String initial = """
                {
                    "name": "Lemonade",
                    "price": 3.50,
                    "category": "Drinks",
                    "available": true
                }
                """;

        MvcResult postResult = mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initial))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

        String patch = """
                {
                    "price": 4.00,
                    "available": false
                }
                """;

        mockMvc.perform(patch("/menu-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lemonade"))
                .andExpect(jsonPath("$.price").value(4.00))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testDeleteMenuItem() throws Exception {
        String item = """
                {
                    "name": "Soup of the Day",
                    "price": 5.00
                }
                """;

        MvcResult postResult = mockMvc.perform(post("/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(item))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(postResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/menu-items/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/menu-items/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testItemNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/menu-items/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void testFilterByCategoryAndPrice() throws Exception {
        mockMvc.perform(post("/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Caesar Salad", "category": "Salad", "price": 8.00}
                        """));

        mockMvc.perform(post("/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Greek Salad", "category": "Salad", "price": 9.50}
                        """));

        mockMvc.perform(post("/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Ribeye Steak", "category": "Mains", "price": 28.00}
                        """));

        // Filter by category
        mockMvc.perform(get("/menu-items?category=Salad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Filter by price range
        mockMvc.perform(get("/menu-items?minPrice=9.00&maxPrice=15.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Greek Salad"));
    }
}

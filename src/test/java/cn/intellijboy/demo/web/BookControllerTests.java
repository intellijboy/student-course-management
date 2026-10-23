package cn.intellijboy.demo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void listBooksReturnsCreatedBooks() throws Exception {
        createBook("Spring in Action", "Craig Walls");
        createBook("Effective Java", "Joshua Bloch");
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createBookReturns201WithGeneratedId() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Spring in Action\",\"author\":\"Craig Walls\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Spring in Action"))
                .andExpect(jsonPath("$.author").value("Craig Walls"));
    }

    @Test
    void getBookReturns200WhenExists() throws Exception {
        long id = createBook("Effective Java", "Joshua Bloch");
        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    void getBookReturns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/books/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookReturns200WhenExists() throws Exception {
        long id = createBook("Old Title", "Old Author");
        mockMvc.perform(put("/api/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Title\",\"author\":\"New Author\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.author").value("New Author"));
    }

    @Test
    void updateBookReturns404WhenNotFound() throws Exception {
        mockMvc.perform(put("/api/books/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"author\":\"Y\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBookReturns204WhenExists() throws Exception {
        long id = createBook("To Delete", "Someone");
        mockMvc.perform(delete("/api/books/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBookReturns404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/books/9999"))
                .andExpect(status().isNotFound());
    }

    private long createBook(String title, String author) throws Exception {
        String body = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\",\"author\":\"" + author + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(body.replaceAll("\\D+", ""));
    }
}

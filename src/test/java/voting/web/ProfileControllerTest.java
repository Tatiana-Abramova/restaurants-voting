package voting.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import voting.model.Role;
import voting.model.User;
import voting.repository.UserRepository;
import voting.to.UserTo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ProfileControllerTest {

    private final static String REST_URL = "/api/profile";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setDateFormat(df);

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    void get() throws Exception {
        User expected = new User(2, "Admin", "admin@gmail.com", null, Role.USER, Role.ADMIN);
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        User actual = mapper.readValue(action.andReturn().getResponse().getContentAsString(), User.class);
        Assertions.assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected);
        Assertions.assertThat(actual.getRegistered()).isEqualToIgnoringSeconds(expected.getRegistered());
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register() throws Exception {
        UserTo userTo = new UserTo(null, "Test", "test@mail.test", "pass");
        User expected = new User(null, "Test", "test@mail.test", null, Role.USER);
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userTo)))
                .andExpect(status().isCreated());

        User actual = mapper.readValue(action.andReturn().getResponse().getContentAsString(), User.class);
        expected.setId(actual.id());
        Assertions.assertThat(actual).usingRecursiveComparison().ignoringFields("registered").isEqualTo(expected);
        Assertions.assertThat(actual.getRegistered()).isEqualToIgnoringSeconds(expected.getRegistered());
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    void update() throws Exception {
        UserTo userTo = new UserTo(null, "Test", "admin@gmail.com", "pass");
        User expected = new User(2, "Test", "admin@gmail.com", "pass", Role.ADMIN, Role.USER);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userTo)))
                .andExpect(status().isNoContent());
        User actual = userRepository.get(2).get();
        Assertions.assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "password").isEqualTo(expected);
        Assertions.assertThat(actual.getRegistered()).isEqualToIgnoringSeconds(expected.getRegistered());
    }
}
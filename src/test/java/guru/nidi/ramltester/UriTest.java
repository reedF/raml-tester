package guru.nidi.ramltester;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static guru.nidi.ramltester.spring.RamlResultMatchers.requestResponse;

/**
 *
 */
@Controller
public class UriTest extends TestBase {
    private RamlDefinition uri = RamlDefinition.fromClasspath(getClass(), "uri.raml");
    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(this).build();
    }

    @RequestMapping(value = {"/raml/v1/{def}/{type}", "/v1/{def}/{type}", "/{def}/{type}"})
    @ResponseBody
    public HttpEntity<String> test() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new HttpEntity<>(headers);
    }

    @Test(expected = AssertionError.class)
    public void standardServletUri() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/raml/v1/undefd"))
                .andExpect(requestResponse().matchesRaml(uri));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidServletUri() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/raml/v1/undefd"))
                .andExpect(requestResponse().withServletUri("invalid").matchesRaml(uri));
    }

    @Test
    public void correctServletUri() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/raml/v1/undefd/type"))
                .andExpect(requestResponse().withServletUri("http://nidi.guru").matchesRaml(uri));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/undefd/type"))
                .andExpect(requestResponse().withServletUri("http://nidi.guru/raml").matchesRaml(uri));

        mockMvc.perform(MockMvcRequestBuilders.get("/undefd/type"))
                .andExpect(requestResponse().withServletUri("http://nidi.guru/raml/v1").matchesRaml(uri));
    }
}

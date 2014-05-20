/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.ramltester;

import guru.nidi.ramltester.core.RamlReport;
import guru.nidi.ramltester.core.RamlRequest;
import guru.nidi.ramltester.core.RamlResponse;
import guru.nidi.ramltester.core.RamlTester;
import guru.nidi.ramltester.httpcomponents.RamlHttpClient;
import guru.nidi.ramltester.servlet.ServletTester;
import guru.nidi.ramltester.spring.RamlMatcher;
import guru.nidi.ramltester.spring.RamlRestTemplate;
import org.apache.http.impl.client.CloseableHttpClient;
import org.raml.model.Raml;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 *
 */
public class RamlDefinition {
    private final Raml raml;
    private final SchemaValidators schemaValidators;
    private final String servletUri;

    public RamlDefinition(Raml raml, SchemaValidators schemaValidators, String servletUri) {
        this.raml = raml;
        this.schemaValidators = schemaValidators;
        this.servletUri = servletUri;
    }

    public RamlDefinition(Raml raml, SchemaValidators schemaValidators) {
        this(raml, schemaValidators, null);
    }

    public RamlDefinition assumingServletUri(String servletUri) {
        return new RamlDefinition(raml, schemaValidators, servletUri);
    }

    public RamlReport testAgainst(RamlRequest request, RamlResponse response) {
        return createTester().test(request, response);
    }

    public RamlReport testAgainst(MvcResult mvcResult) {
        return matches().testAgainst(mvcResult);
    }

    public RamlReport testAgainst(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        return new ServletTester(createTester(), servletUri).testAgainst(request, response, chain);
    }

    public RamlMatcher matches() {
        return new RamlMatcher(createTester(), servletUri);
    }

    public RamlRestTemplate createRestTemplate(ClientHttpRequestFactory requestFactory) {
        return new RamlRestTemplate(createTester(), servletUri, requestFactory);
    }

    public RamlRestTemplate createRestTemplate(RestTemplate restTemplate) {
        return new RamlRestTemplate(createTester(), servletUri, restTemplate);
    }

    public RamlHttpClient createHttpClient() {
        return new RamlHttpClient(createTester(), servletUri);
    }

    public RamlHttpClient createHttpClient(CloseableHttpClient httpClient) {
        return new RamlHttpClient(createTester(), servletUri, httpClient);
    }

    public RamlTester createTester() {
        return new RamlTester(raml, schemaValidators.getValidators());
    }

}


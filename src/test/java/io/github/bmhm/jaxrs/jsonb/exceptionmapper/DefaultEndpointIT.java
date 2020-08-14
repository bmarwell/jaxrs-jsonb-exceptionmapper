/*
 *  Copyright 2020 github.com/bmhm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.github.bmhm.jaxrs.jsonb.exceptionmapper;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This project is configured that liberty will execute this test using {@code ./mvnw clean liberty:dev}.
 */
public class DefaultEndpointIT {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEndpointIT.class);

    private static String URL;

    @BeforeAll
    public static void init() {
        final String port = System.getProperty("http.port");
        URL = "http://localhost:" + port + "/" + System.getProperty("war.name");
    }

    @Test
    public void testLoginSuccess() throws IOException {
        final CloseableHttpClient client = HttpClients.createDefault();
        final String uri = URL + "/endpoint";
        final HttpPost method = new HttpPost(uri);

        LOG.info("POST to [{}]", uri);

        final JsonObject jsonObject = Json.createObjectBuilder()
                .add("invalidName", "joe")
                .build();
        final BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        basicHttpEntity.setContent(new ByteArrayInputStream(jsonObject.toString().getBytes(StandardCharsets.UTF_8)));
        basicHttpEntity.setContentType(MediaType.APPLICATION_JSON);
        method.setEntity(basicHttpEntity);

        try {
            final HttpResponse response = client.execute(method);

            assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode(), "HTTP POST failed");
            final InputStream responseBody = response.getEntity().getContent();
            final JsonReader reader = Json.createReader(responseBody);
            final JsonObject jsonResponseObject = reader.readObject();
            assertTrue(jsonResponseObject.containsKey("message"), "JsonbExceptionMapper not invoked, otherwise there would be a message field.");
            assertEquals("JsonbException", jsonResponseObject.getString("type", ""));
        } finally {
            method.releaseConnection();
        }
    }

}

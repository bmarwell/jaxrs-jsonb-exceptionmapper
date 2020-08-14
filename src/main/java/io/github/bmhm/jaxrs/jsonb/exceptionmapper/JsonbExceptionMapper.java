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

import javax.json.bind.JsonbException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * This exception mapper is actually not a sole {@link JsonbException} mapper.
 *
 * <p>If you have more exceptions to map from a {@link BadRequestException},
 * rename it accordingly.</p>
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonbExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(final BadRequestException badRequestEx) {
        final Map<String, Object> entity = new HashMap<>();

        final Throwable cause = badRequestEx.getCause();
        if (cause == null) {
            // this mimics the default behaviour.
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }

        // adjust to your needs.
        // a switch statement will also do if you have multiple causes you want to map.
        if (cause instanceof JsonbException) {
            entity.put("message", cause.getMessage());
            entity.put("type", cause.getClass().getSimpleName());
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}

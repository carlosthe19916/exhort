/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.exhort.analytics.segment;

import java.util.Base64;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class AuthenticationHeaderFactory implements ClientHeadersFactory {

  @ConfigProperty(name = "telemetry.write-key")
  Optional<String> writeKey;

  String basicAuthHeader;

  @PostConstruct
  void initialize() {
    this.basicAuthHeader =
        "Basic "
            + Base64.getEncoder()
                .encodeToString(
                    writeKey
                        .orElseThrow(
                            () ->
                                new IllegalArgumentException(
                                    "Missing required telemetry.write-key"))
                        .getBytes());
  }

  @Override
  public MultivaluedMap<String, String> update(
      MultivaluedMap<String, String> incomingHeaders,
      MultivaluedMap<String, String> clientOutgoingHeaders) {
    MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
    result.add("Authorization", basicAuthHeader);
    return result;
  }
}

/*
 *    Copyright 2022 Mark Nellemann <mark.nellemann@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package biz.nellemann.svci;

import biz.nellemann.svci.dto.json.EnclosureStat;
import biz.nellemann.svci.dto.json.NodeStat;
import biz.nellemann.svci.dto.json.System;
import biz.nellemann.svci.dto.toml.SvcConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

class VolumeController implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(VolumeController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Integer refreshValue;
    private final Integer discoverValue;
    //private final List<ManagedSystem> managedSystems = new ArrayList<>();


    private final RestClient restClient;
    private final InfluxClient influxClient;
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);

    protected Integer responseErrors = 0;

    protected System system;


    VolumeController(SvcConfiguration configuration, InfluxClient influxClient) {
        this.refreshValue = configuration.refresh;
        this.discoverValue = configuration.discover;
        this.influxClient = influxClient;
        restClient = new RestClient(configuration.url, configuration.username, configuration.password, configuration.trust);

    }


    @Override
    public void run() {

        log.trace("run()");

        restClient.login();
        discover();

        do {
            Instant instantStart = Instant.now();
            try {
                refresh();
            } catch (Exception e) {
                log.error("run() - fatal error: {}", e.getMessage());
                keepRunning.set(false);
                throw new RuntimeException(e);
            }

            Instant instantEnd = Instant.now();
            long timeSpend = Duration.between(instantStart, instantEnd).toMillis();
            log.trace("run() - duration millis: " + timeSpend);
            if(timeSpend < (refreshValue * 1000)) {
                try {
                    long sleepTime = (refreshValue * 1000) - timeSpend;
                    log.trace("run() - sleeping millis: " + sleepTime);
                    if(sleepTime > 0) {
                        //noinspection BusyWait
                        sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    log.error("run() - sleep interrupted", e);
                }
            } else {
                log.warn("run() - possible slow response from this HMC");
            }

        } while (keepRunning.get());

    }


    void discover() {
        log.debug("discover()");
        influxClient.write(getSystem(), Instant.now(),"system");
    }


    void refresh() {
        log.debug("refresh()");
        influxClient.write(getNodeStats(), Instant.now(),"node_stats");
        influxClient.write(getEnclosureStats(), Instant.now(),"enclosure_stats");
    }


    List<Measurement> getSystem() {

        List<Measurement> measurementList = new ArrayList<>();
        try {
            String response = restClient.postRequest("/rest/v1/lssystem");

            // Do not try to parse empty response
            if(response == null || response.length() <= 1) {
                log.warn("getSystem() - no data.");
                return measurementList;
            }

            // Save for use elsewhere when referring to system name
            system = objectMapper.readValue(response, System.class);

            HashMap<String, String> tagsMap = new HashMap<>();
            HashMap<String, Object> fieldsMap = new HashMap<>();

            tagsMap.put("name", system.name);
            fieldsMap.put("location", system.location);
            fieldsMap.put("code_level", system.codeLevel);
            fieldsMap.put("product_name", system.productName);

            log.trace("getNodeStats() - fields: " + fieldsMap);

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        } catch (IOException e) {
            log.error("getSystem() - error 2: {}", e.getMessage());
        }

        return measurementList;
    }


    List<Measurement> getNodeStats() {
        List<Measurement> measurementList = new ArrayList<>();

        try {
            String response = restClient.postRequest("/rest/v1/lsnodestats");

            // Do not try to parse empty response
            if(response == null || response.length() <= 1) {
                log.warn("getNodeStats() - no data.");
                return measurementList;
            }

            List<NodeStat> pojo = Arrays.asList(objectMapper.readValue(response, NodeStat[].class));
            pojo.forEach((stat) -> {

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();

                tagsMap.put("id", stat.nodeId);
                tagsMap.put("name", stat.nodeName);
                tagsMap.put("system", system.name);

                fieldsMap.put(stat.statName, stat.statCurrent);
                log.trace("getNodeStats() - fields: " + fieldsMap);

                measurementList.add(new Measurement(tagsMap, fieldsMap));

                //log.info("{}: {} -> {}", stat.nodeName, stat.statName, stat.statCurrent);
            });

        } catch (IOException e) {
            log.error("getNodeStats() - error 2: {}", e.getMessage());
        }

        return measurementList;
    }

    List<Measurement> getEnclosureStats() {
        List<Measurement> measurementList = new ArrayList<>();

        try {
            String response = restClient.postRequest("/rest/v1/lsenclosurestats");

            // Do not try to parse empty response
            if(response == null || response.length() <= 1) {
                log.warn("getEnclosureStats() - no data.");
                return measurementList;
            }

            List<EnclosureStat> pojo = Arrays.asList(objectMapper.readValue(response, EnclosureStat[].class));
            pojo.forEach((stat) -> {

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();

                tagsMap.put("id", stat.enclosureId);
                tagsMap.put("system", system.name);

                fieldsMap.put(stat.statName, stat.statCurrent);
                log.trace("getEnclosureStats() - fields: " + fieldsMap);

                measurementList.add(new Measurement(tagsMap, fieldsMap));

                //log.info("{}: {} -> {}", stat.nodeName, stat.statName, stat.statCurrent);
            });

        } catch (IOException e) {
            log.error("getEnclosureStats() - error 2: {}", e.getMessage());
        }

        return measurementList;
    }
}

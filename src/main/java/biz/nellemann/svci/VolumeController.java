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

import java.io.IOException;
import static java.lang.Thread.sleep;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import biz.nellemann.svci.dto.json.*;
import biz.nellemann.svci.dto.json.System;
import biz.nellemann.svci.dto.xml.DiskStatCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nellemann.svci.dto.toml.SvcConfiguration;

class VolumeController implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(VolumeController.class);

    private final Integer refreshValue;
    private final RestClient restClient;
    private final InfluxClient influxClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper xmlMapper = new XmlMapper();
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private final ArrayList<String> fileDownloadList = new ArrayList<>();
    protected System system;


    VolumeController(SvcConfiguration configuration, InfluxClient influxClient) {
        this.refreshValue = configuration.refresh;
        this.influxClient = influxClient;
        restClient = new RestClient(configuration.url, configuration.username, configuration.password, configuration.trust);
    }


    @Override
    public void run() {

        log.trace("run()");
        restClient.login();

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
                log.warn("run() - possible slow response from this SVC");
            }

        } while (keepRunning.get());

    }


    void refresh() {
        log.debug("refresh()");
        influxClient.write(getSystem(),"system");
        influxClient.write(getNodeStats(),"node_stats");
        influxClient.write(getEnclosureStats(),"enclosure_stats");
        influxClient.write(getMDiskGroups(), "m_disk_groups");
        influxClient.write(getStats(), "io_stats");
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
            fieldsMap.put("total_free_tb", system.totalFreeTB);
            fieldsMap.put("total_used_tb", system.totalUsedTB);
            fieldsMap.put("mdisk_total_tb", system.mDiskTotalTB);
            fieldsMap.put("vdisk_total_tb", system.vDiskTotalTB);
            fieldsMap.put("vdisk_allocated_tb", system.vDiskAllocatedTB);

            log.trace("getSystem() - fields: " + fieldsMap);

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
            if(system == null || response == null || response.length() <= 1) {
                log.warn("getNodeStats() - no data.");
                return measurementList;
            }

            List<NodeStat> list = Arrays.asList(objectMapper.readValue(response, NodeStat[].class));
            list.forEach( (stat) -> {

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();

                tagsMap.put("id", stat.nodeId);
                tagsMap.put("name", stat.nodeName);
                tagsMap.put("system", system.name);

                fieldsMap.put(stat.statName, stat.statCurrent);
                log.trace("getNodeStats() - fields: " + fieldsMap);

                measurementList.add(new Measurement(tagsMap, fieldsMap));

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
            if(system == null || response == null || response.length() <= 1) {
                log.warn("getEnclosureStats() - no data.");
                return measurementList;
            }

            List<EnclosureStat> list = Arrays.asList(objectMapper.readValue(response, EnclosureStat[].class));
            list.forEach( (stat) -> {

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();

                tagsMap.put("id", stat.enclosureId);
                tagsMap.put("system", system.name);

                fieldsMap.put(stat.statName, stat.statCurrent);
                log.trace("getEnclosureStats() - fields: " + fieldsMap);

                measurementList.add(new Measurement(tagsMap, fieldsMap));

            });

        } catch (IOException e) {
            log.error("getEnclosureStats() - error 2: {}", e.getMessage());
        }

        return measurementList;
    }


    List<Measurement> getVDisk() {
        List<Measurement> measurementList = new ArrayList<>();

        try {
            String response = restClient.postRequest("/rest/v1/lsvdisk");

            // Do not try to parse empty response
            if(system == null || response == null || response.length() <= 1) {
                log.warn("getVDisk() - no data.");
                return measurementList;
            }

            List<VDisk> list = Arrays.asList(objectMapper.readValue(response, VDisk[].class));
            list.forEach( (stat) -> {

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();

                tagsMap.put("id", stat.id);
                tagsMap.put("name", stat.name);
                tagsMap.put("type", stat.type);
                tagsMap.put("system", system.name);

                fieldsMap.put("capacity_tb", stat.capacity);
                log.trace("getVDisk() - fields: " + fieldsMap);

                measurementList.add(new Measurement(tagsMap, fieldsMap));
            });

        } catch (IOException e) {
            log.error("getVDisk() - error 2: {}", e.getMessage());
        }

        return measurementList;
    }


    List<Measurement> getMDiskGroups() {
        List<Measurement> measurementList = new ArrayList<>();

        try {
            String response = restClient.postRequest("/rest/v1/lsmdiskgrp");

            // Do not try to parse empty response
            if(system == null || response == null || response.length() <= 1) {
                log.warn("getMDiskGroups() - no data.");
                return measurementList;
            }

            List<MDiskGroup> list = Arrays.asList(objectMapper.readValue(response, MDiskGroup[].class));
            list.forEach( (stat) -> {

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();

                tagsMap.put("id", stat.id);
                tagsMap.put("name", stat.name);
                tagsMap.put("system", system.name);

                fieldsMap.put("mdisk_count", stat.mDiskCount);
                fieldsMap.put("vdisk_count", stat.vDiskCount);
                fieldsMap.put("capacity_free_tb", stat.capacityFree);
                fieldsMap.put("capacity_real_tb", stat.capacityReal);
                fieldsMap.put("capacity_used_tb", stat.capacityUsed);
                fieldsMap.put("capacity_total_tb", stat.capacityTotal);
                fieldsMap.put("capacity_virtual_tb", stat.capacityVirtual);
                log.trace("getMDiskGroups() - fields: " + fieldsMap);

                measurementList.add(new Measurement(tagsMap, fieldsMap));

                //log.info("{}: {} -> {}", stat.nodeName, stat.statName, stat.statCurrent);
            });

        } catch (IOException e) {
            log.error("getMDiskGroups() - error 2: {}", e.getMessage());
        }

        return measurementList;
    }


    List<Measurement> getStats() {
        List<Measurement> measurementList = new ArrayList<>();

        List<Dump> dumps = listDumps();
        for(Dump dump : Objects.requireNonNull(dumps)) {

            // Keep track of downloaded files, so we don't process a file twice
            if(fileDownloadList.contains(dump.filename)) {
                continue;
            }

            fileDownloadList.add(dump.filename);
            if(fileDownloadList.size() > 1000) {
                fileDownloadList.subList(0, 1000).clear();
            }

            log.debug("getStats() - processing filename: {}", dump.filename);
            String output = getFile(dump.filename);
            if(output == null || output.isEmpty()) {
                continue;
            }

            try {
                DiskStatCollection diskStatCollection = xmlMapper.readerFor(DiskStatCollection.class).readValue(output);
                log.info("getStats() - file content: {}", diskStatCollection.toString());

                diskStatCollection.diskStatList.forEach((stat) -> {

                    // Convert to measurement
                    Instant timestamp = Utils.parseDateTime(diskStatCollection.timestampUtc);

                    HashMap<String, String> tagsMap = new HashMap<>();
                    HashMap<String, Object> fieldsMap = new HashMap<>();
                    tagsMap.put("idx", stat.idx);
                    tagsMap.put("node", diskStatCollection.id);
                    tagsMap.put("cluster", diskStatCollection.cluster);
                    fieldsMap.put("pre", stat.pre);
                    fieldsMap.put("pro", stat.pro);
                    fieldsMap.put("pwe", stat.pwe);
                    fieldsMap.put("pwo", stat.pwo);
                    fieldsMap.put("rb", stat.rb);
                    fieldsMap.put("re", stat.re);
                    fieldsMap.put("ro", stat.ro);
                    fieldsMap.put("rq", stat.rq);
                    fieldsMap.put("ure", stat.ure);
                    fieldsMap.put("urq", stat.urq);
                    fieldsMap.put("uwe", stat.uwe);
                    fieldsMap.put("uwq", stat.uwq);
                    fieldsMap.put("wb", stat.wb);
                    fieldsMap.put("we", stat.we);
                    fieldsMap.put("wo", stat.wo);
                    fieldsMap.put("wq", stat.wq);
                    log.trace("getStats() - tags: {}, fields: {}", tagsMap, fieldsMap);
                    measurementList.add(new Measurement(timestamp, tagsMap, fieldsMap));

                });

            } catch (JsonProcessingException e) {
                log.warn("getStats() - error: {}", e.getMessage());
            }

        }

        return measurementList;
    }


    private List<Dump> listDumps() {

        List<Dump> list = new ArrayList<>();

        try {
            String response = restClient.postRequest("/rest/v1/lsdumps","{\"prefix\":\"/dumps/iostats\"}" );

            // Do not try to parse empty response
            if(system == null || response == null || response.length() <= 1) {
                log.debug("listDumps() - no data.");
                return list;
            }
            log.debug("listDumps() => {}", response);
            list = Arrays.asList(objectMapper.readValue(response, Dump[].class));
        } catch (IOException e) {
            log.error("listDumps() - error: {}", e.getMessage());
        }

        return list;
    }


    private String getFile(String filename) {

        try {
            String payload = String.format("{\"prefix\":\"/dumps/iostats\",\"filename\":\"%s\"}", filename);
            String response = restClient.postRequest("/rest/v1/download", payload);

            // Do not try to parse empty response
            if(system == null || response == null || response.length() <= 1) {
                log.debug("getFile() - no data.");
                return null;
            }

            log.info(response);
            return response;

        } catch (IOException e) {
            log.error("getFile() - error: {}", e.getMessage());
        }

        return null;
    }

}

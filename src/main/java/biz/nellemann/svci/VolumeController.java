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
import biz.nellemann.svci.dto.json.NodeStat;
import biz.nellemann.svci.dto.json.System;
import biz.nellemann.svci.dto.xml.*;
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
    private final ShellClient shellClient;
    private final InfluxClient influxClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper xmlMapper = new XmlMapper();
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private final ArrayList<String> fileDownloadList = new ArrayList<>();
    protected System system;
    protected Boolean useShellForDownload = false;

    VolumeController(SvcConfiguration configuration, InfluxClient influxClient) {
        this.refreshValue = configuration.refresh;
        this.influxClient = influxClient;
        restClient = new RestClient(configuration.hostname, configuration.username, configuration.password, 7443, configuration.trust);
        shellClient = new ShellClient(configuration.hostname, configuration.username, configuration.password, 22);
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

        // For IO Stats
        processStats();
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


    /**
     * When statistics collection is enabled with *startstats -internal x* we list the files and download.
     * https://www.ibm.com/docs/en/flashsystem-9x00/8.6.x?topic=monitoring-statistics-collection
     */
    void processStats() {

        // Nd_stats: for drives
        // Ng_stats: for volumes groups
        // Nm_stats: for managed disks (MDisks)
        // Nn_stats: for nodes (and ports)
        // Nv_stats: for virtual disks (VDisks)

        List<Dump> dumps = listDumps();
        for(Dump dump : Objects.requireNonNull(dumps)) {

            // Keep track of downloaded files, so we don't process a file twice
            if(fileDownloadList.contains(dump.filename)) {
                //log.debug("processStats() - file already processed: {}", dump.filename);
                continue;
            }

            // Trim size of list
            fileDownloadList.add(dump.filename);
            if(fileDownloadList.size() > 1000) {
                fileDownloadList.subList(0, 1000).clear();
            }

            //log.debug("processStats() - processing filename: {}", dump.filename);
            String output = getFile(dump.filename);
            if(output == null || output.isEmpty()) {
                continue;
            }

            if(dump.filename.startsWith("Nd_stats")) {
                log.debug("processStats() - drives: {}", dump.filename);
                influxClient.write(getDriveStats(output), "stat_drive");
            }

            if(dump.filename.startsWith("Ng_stats")) {
                log.debug("processStats() - volume groups: {}", dump.filename);
                influxClient.write(getVolumeGroupStats(output), "stat_vg");
            }

            if(dump.filename.startsWith("Nm_stats")) {
                log.debug("processStats() - managed disks: {}", dump.filename);
                influxClient.write(getMDiskStats(output), "stat_mdisk");
            }

            if(dump.filename.startsWith("Nn_stats")) {
                log.debug("processStats() - nodes: {}", dump.filename);
                influxClient.write(getNodeStats(output), "stat_node");
                influxClient.write(getPortStats(output), "stat_port");
            }

            if(dump.filename.startsWith("Nv_stats")) {
                log.debug("processStats() - virtual disks: {}", dump.filename);
                influxClient.write(getVDiskStats(output), "stat_vdisk");
            }

        }

    }


    List<Measurement> getDriveStats(String stats) {

        List<Measurement> measurementList = new ArrayList<>();

        try {
            DriveStatCollection statCollection = xmlMapper.readerFor(DriveStatCollection.class).readValue(stats);
            //log.debug("getDriveStats() - file content: {}", statCollection.toString());

            statCollection.driveStats.forEach((stat) -> {

                // Convert to measurement
                //Instant timestamp = Utils.parseDateTime( (statCollection.timestampUtc != null) ? statCollection.timestampUtc : statCollection.timestamp );
                Instant timestamp = Utils.parseDateTime(statCollection.timestamp);

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();
                tagsMap.put("idx", stat.idx);
                tagsMap.put("node", statCollection.id);
                tagsMap.put("cluster", statCollection.cluster);
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
                log.trace("getDriveStats() - tags: {}, fields: {}", tagsMap, fieldsMap);
                measurementList.add(new Measurement(timestamp, tagsMap, fieldsMap));

            });

        } catch (JsonProcessingException e) {
            log.warn("getDriveStats() - error: {}", e.getMessage());
        }

        return measurementList;

    }


    List<Measurement> getVolumeGroupStats(String stats) {

        List<Measurement> measurementList = new ArrayList<>();

        try {
            VolumeGroupStatCollection statCollection = xmlMapper.readerFor(VolumeGroupStatCollection.class).readValue(stats);
            //log.debug("getVolumeGroupStats() - file content: {}", statCollection.toString());

            statCollection.volumeGroupStats.forEach((stat) -> {

                // Convert to measurement
                //Instant timestamp = Utils.parseDateTime( (statCollection.timestampUtc != null) ? statCollection.timestampUtctimestampUtc : statCollection.timestamp );
                Instant timestamp = Utils.parseDateTime(statCollection.timestamp);

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();
                tagsMap.put("idx", stat.idx);
                tagsMap.put("name", stat.name);
                tagsMap.put("node", statCollection.id);
                tagsMap.put("cluster", statCollection.cluster);
                fieldsMap.put("rarp", stat.rarp);
                fieldsMap.put("rwrp", stat.rwrp);
                fieldsMap.put("rnrw", stat.rnrw);
                fieldsMap.put("rnrb", stat.rnrb);
                fieldsMap.put("rhalwl", stat.rhalwl);
                fieldsMap.put("rhalwc", stat.rhalwc);
                fieldsMap.put("rhalww", stat.rhalww);
                fieldsMap.put("rharwl", stat.rharwl);
                fieldsMap.put("rharwc", stat.rharwc);
                log.trace("getVolumeGroupStats() - tags: {}, fields: {}", tagsMap, fieldsMap);
                measurementList.add(new Measurement(timestamp, tagsMap, fieldsMap));

            });

        } catch (JsonProcessingException e) {
            log.warn("getVolumeGroupStats() - error: {}", e.getMessage());
        }

        return measurementList;

    }


    List<Measurement> getMDiskStats(String stats) {

        List<Measurement> measurementList = new ArrayList<>();

        try {
            MDiskStatCollection statCollection = xmlMapper.readerFor(MDiskStatCollection.class).readValue(stats);
            //log.debug("getMDiskStats() - file content: {}", statCollection.toString());

            statCollection.mDiskStats.forEach((stat) -> {

                // Convert to measurement
                //Instant timestamp = Utils.parseDateTime( (statCollection.timestampUtc != null) ? statCollection.timestampUtc : statCollection.timestamp );
                Instant timestamp = Utils.parseDateTime(statCollection.timestamp);

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();
                tagsMap.put("idx", stat.idx);
                tagsMap.put("id", stat.id);
                tagsMap.put("node", statCollection.id);
                tagsMap.put("cluster", statCollection.cluster);
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
                log.trace("getMDiskStats() - tags: {}, fields: {}", tagsMap, fieldsMap);
                measurementList.add(new Measurement(timestamp, tagsMap, fieldsMap));

            });

        } catch (JsonProcessingException e) {
            log.warn("getMDiskStats() - error: {}", e.getMessage());
        }

        return measurementList;

    }


    List<Measurement> getNodeStats(String stats) {

        List<Measurement> measurementList = new ArrayList<>();

        try {
            NodeStatCollection statCollection = xmlMapper.readerFor(NodeStatCollection.class).readValue(stats);
            log.debug("getNodeStats() - file content: {}", statCollection.toString());

            statCollection.nodeStats.forEach((stat) -> {

                // Convert to measurement
                //Instant timestamp = Utils.parseDateTime( (statCollection.timestampUtc != null) ? statCollection.timestampUtc : statCollection.timestamp );
                Instant timestamp = Utils.parseDateTime(statCollection.timestamp);

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();
                tagsMap.put("id", stat.id);
                tagsMap.put("cluster", stat.cluster);
                fieldsMap.put("ro", stat.ro);
                fieldsMap.put("wo", stat.wo);
                fieldsMap.put("rb", stat.rb);
                fieldsMap.put("wb", stat.wb);
                fieldsMap.put("lrb", stat.lrb);
                fieldsMap.put("lwb", stat.lwb);
                fieldsMap.put("re", stat.re);
                fieldsMap.put("we", stat.we);
                fieldsMap.put("rq", stat.rq);
                fieldsMap.put("wq", stat.wq);
                log.trace("getNodeStats() - tags: {}, fields: {}", tagsMap, fieldsMap);
                measurementList.add(new Measurement(timestamp, tagsMap, fieldsMap));

            });

        } catch (JsonProcessingException e) {
            log.warn("getNodeStats() - error: {}", e.getMessage());
        }

        return measurementList;

    }


    List<Measurement> getPortStats(String stats) {

        List<Measurement> measurementList = new ArrayList<>();

        try {
            NodeStatCollection statCollection = xmlMapper.readerFor(NodeStatCollection.class).readValue(stats);
            //log.debug("getPortStats() - file content: {}", statCollection.toString());

            statCollection.portStats.forEach((stat) -> {

                // Convert to measurement
                //Instant timestamp = Utils.parseDateTime( (statCollection.timestampUtc != null) ? statCollection.timestampUtc : statCollection.timestamp );
                Instant timestamp = Utils.parseDateTime(statCollection.timestamp);

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();
                tagsMap.put("id", stat.id);
                tagsMap.put("type", stat.type);
                tagsMap.put("node", statCollection.id);
                tagsMap.put("cluster", statCollection.cluster);
                fieldsMap.put("hbt", stat.hbt);
                fieldsMap.put("hbr", stat.hbr);
                fieldsMap.put("het", stat.het);
                fieldsMap.put("her", stat.her);
                fieldsMap.put("cbt", stat.cbt);
                fieldsMap.put("cbr", stat.cbr);
                fieldsMap.put("cet", stat.cet);
                fieldsMap.put("cer", stat.cer);
                fieldsMap.put("lnbt", stat.lnbt);
                fieldsMap.put("lnbr", stat.lnbr);
                fieldsMap.put("lnet", stat.lnet);
                fieldsMap.put("lner", stat.lner);
                fieldsMap.put("rmbt", stat.rmbt);
                fieldsMap.put("rmbr", stat.rmbr);
                fieldsMap.put("rmet", stat.rmet);
                fieldsMap.put("rmer", stat.rmer);
                log.trace("getPortStats() - tags: {}, fields: {}", tagsMap, fieldsMap);
                measurementList.add(new Measurement(timestamp, tagsMap, fieldsMap));

            });

        } catch (JsonProcessingException e) {
            log.warn("getPortStats() - error: {}", e.getMessage());
        }

        return measurementList;

    }

    List<Measurement> getVDiskStats(String stats) {

        List<Measurement> measurementList = new ArrayList<>();

        try {
            VDiskStatCollection statCollection = xmlMapper.readerFor(VDiskStatCollection.class).readValue(stats);
            //log.debug("getVDiskStats() - file content: {}", statCollection.toString());

            statCollection.vDiskStats.forEach((stat) -> {

                // Convert to measurement
                //Instant timestamp = Utils.parseDateTime( (statCollection.timestampUtc != null) ? statCollection.timestampUtc : statCollection.timestamp );
                Instant timestamp = Utils.parseDateTime(statCollection.timestamp);

                HashMap<String, String> tagsMap = new HashMap<>();
                HashMap<String, Object> fieldsMap = new HashMap<>();
                tagsMap.put("id", stat.id);
                tagsMap.put("idx", stat.idx);
                tagsMap.put("node", statCollection.id);
                fieldsMap.put("ro", stat.ro);
                fieldsMap.put("wo", stat.wo);
                fieldsMap.put("rb", stat.rb);
                fieldsMap.put("wb", stat.wb);
                log.trace("getVDiskStats() - tags: {}, fields: {}", tagsMap, fieldsMap);
                measurementList.add(new Measurement(timestamp, tagsMap, fieldsMap));

            });

        } catch (JsonProcessingException e) {
            log.warn("getVDiskStats() - error: {}", e.getMessage());
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
            log.trace("listDumps() => {}", response);
            list = Arrays.asList(objectMapper.readValue(response, Dump[].class));
        } catch (IOException e) {
            log.error("listDumps() - error: {}", e.getMessage());
        }

        return list;
    }


    private String getFile(String filename) {

        try {
            String response;
            if(useShellForDownload) {
                response = shellClient.read(String.format("/dumps/iostats/%s", filename));
            } else {
                String payload = String.format("{\"prefix\":\"/dumps/iostats\",\"filename\":\"%s\"}", filename);
                response = restClient.postRequest("/rest/v1/download", payload);
            }

            // Do not try to parse empty response
            if(system == null || response == null || response.length() <= 1) {
                log.debug("getFile() - no data, fallback to Shell Transfer.");
                useShellForDownload = true;
                return null;
            }

            return response;

        } catch (IOException e) {
            log.error("getFile() - error: {}", e.getMessage());
        }

        return null;
    }

}

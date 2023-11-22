/*
 *    Copyright 2020 Mark Nellemann <mark.nellemann@gmail.com>
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

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import biz.nellemann.svci.dto.toml.InfluxConfiguration;

public final class InfluxClient {

    private final static Logger log = LoggerFactory.getLogger(InfluxClient.class);

    final private String url;
    final private String org;   // v2 only
    final private String token;
    final private String bucket;  // Bucket in v2, Database in v1

    private InfluxDBClient influxDBClient;
    private WriteApi writeApi;

    InfluxClient(InfluxConfiguration config) {
        this.url = config.url;
        if(config.org != null) {
            this.org = config.org;
        } else {
            this.org = "svci";  // In InfluxDB 1.x, there is no concept of organization.
        }
        if(config.token != null) {
            this.token = config.token;
        } else {
            this.token = config.username + ":" + config.password;
        }
        if(config.bucket != null) {
            this.bucket = config.bucket;
        } else {
            this.bucket = config.database;
        }
    }

    synchronized void login() throws RuntimeException, InterruptedException {

        if(influxDBClient != null) {
            return;
        }

        boolean connected = false;
        int loginErrors = 0;

        do {
            try {
                log.debug("Connecting to InfluxDB - {}", url);
                influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
                influxDBClient.version(); // This ensures that we actually try to connect to the db
                Runtime.getRuntime().addShutdownHook(new Thread(influxDBClient::close));

                // Todo: Handle events - https://github.com/influxdata/influxdb-client-java/tree/master/client#handle-the-events
                //writeApi = influxDBClient.makeWriteApi();
                writeApi = influxDBClient.makeWriteApi(
                    WriteOptions.builder()
                        .bufferLimit(20_000)
                        .flushInterval(5_000)
                        .build());

                connected = true;

            } catch(Exception e) {
                sleep(15 * 1000);
                if(loginErrors++ > 3) {
                    log.error("login() - error, giving up: {}", e.getMessage());
                    throw new RuntimeException(e);
                } else {
                    log.warn("login() - error, retrying: {}", e.getMessage());
                }
            }
        } while(!connected);

    }


    synchronized void logoff() {
        if(influxDBClient != null) {
            influxDBClient.close();
        }
        influxDBClient = null;
    }


    public void write(List<Measurement> measurements, String name) {
        log.debug("write() - measurement: {} {}", name, measurements.size());
        if(!measurements.isEmpty()) {
            processMeasurementMap(measurements, name).forEach((point) -> {
                writeApi.writePoint(point);
            });
        }
    }

    private List<Point> processMeasurementMap(List<Measurement> measurements, String name) {
        List<Point> listOfPoints = new ArrayList<>();
        measurements.forEach( (m) -> {
            log.trace("processMeasurementMap() - timestamp: {}, tags: {}, fields: {}", m.timestamp, m.tags, m.fields);
            Point point = new Point(name)
                .time(m.timestamp.getEpochSecond(), WritePrecision.S)
                .addTags(m.tags)
                .addFields(m.fields);
            listOfPoints.add(point);
        });
        return listOfPoints;
    }

}

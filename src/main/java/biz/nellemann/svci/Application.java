/*
   Copyright 2022 mark.nellemann@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package biz.nellemann.svci;

import biz.nellemann.svci.dto.toml.Configuration;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "svci",
    mixinStandardHelpOptions = true,
    versionProvider = biz.nellemann.svci.VersionProvider.class,
    defaultValueProvider = biz.nellemann.svci.DefaultProvider.class)
public class Application implements Callable<Integer> {

    @Option(names = { "-c", "--conf" }, description = "Configuration file [default: ${DEFAULT-VALUE}].", paramLabel = "<file>")
    private File configurationFile;

    @Option(names = { "-d", "--debug" }, description = "Enable debugging [default: false].")
    private boolean[] enableDebug = new boolean[0];


    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() {

        InfluxClient influxClient;
        List<Thread> threadList = new ArrayList<>();

        if(!configurationFile.exists()) {
            System.err.println("Error - No configuration file found at: " + configurationFile.toString());
            return -1;
        }

        switch (enableDebug.length) {
            case 1:
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel" , "DEBUG");
                break;
            case 2:
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel ", "TRACE");
                break;
        }

        try {
            TomlMapper mapper = new TomlMapper();
            Configuration configuration = mapper.readerFor(Configuration.class)
                .readValue(configurationFile);

            influxClient = new InfluxClient(configuration.influx);
            influxClient.login();

            if(configuration.svc == null || configuration.svc.size() < 1) {
                return 0;
            }

            configuration.svc.forEach((key, value) -> {
                try {
                    VolumeController volumeController = new VolumeController(value, influxClient);
                    Thread t = new Thread(volumeController);
                    t.setName(key);
                    t.start();
                    threadList.add(t);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            });

            for (Thread thread : threadList) {
                thread.join();
            }

            influxClient.logoff();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 1;
        }

        return 0;
    }

}

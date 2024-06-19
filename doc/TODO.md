# TODO


## TODO List

- [] Update docs
    - [x] Update install doc for podman,
    - [] Update install doc for other os, bump InfluxDB version,
    - [] Update main readme with link to doc
    - [] Update readme with influxDB info.
    - [] Update information about how the data is gather, se below.
    
- [] Github Action
    - [] Check out dockerfile with Chainguard java image for CVE check.
    - [] Depandabot with github action
    - [] release with  with container image

- [] Container
   - [] Use best practice for dockerfiles, like run with user for non root. 
   - [] K8 Deployment?
   - [] Config file as a environment variables? secret in K8/OC?

- [] Grafana.
    - [] Updated Grafana dashboard with new stats from XML
    - [] Events/Message/Alarms from Virtualize? - /lseventlog

- [] SVCi/Virtualize Metric
    - [] Logging level 



## Details / Drafts


1: XML stats pull and parse. There are dozens of KPIs in there, but they require maths to decode each metric as they are pure counters
2: Real Time Perf stats (using lssystemstats and lsnodestats/lsportstats) - in 8.5.0 you can only have 5 minutes of status history there, 
   in 8.6.2 this is extended to provide up to 60 data points with roll up for up to 60 days of history). 

Extended stats
```shell
svctask stopstats
svctask startstats -interval 5
lsdumps -prefix /dumps/iostats
```


The files generated are written to the /dumps/iostats directory.

https://www.ibm.com/docs/en/flashsystem-5x00/8.4.x?topic=commands-startstats
https://www.ibm.com/support/pages/overview-svc-v510-performance-statistics

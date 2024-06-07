# SVCi data gathering.

We are using 2 methods to collect the statics.

Storage Virtualize Crrently don't provide detailed host and vdisk performance stats in native JSON on RestAPI. So we require to use the XML endpoint fo rdetailed performance stats, This means we requires to parse the XML. 

1. Real Time Perf stats from RestAPI (using lssystemstats and lsnodestats/lsportstats) - 
   in 8.5.0 you can only have 5 minutes of status history there, in 8.6.2 this is extended to provide up to 60 data points with roll up for up to 60 days of history).

2. For detailed performance stats we are using XML stats pull and parse. There are dozens of KPIs
in there, but they require maths to decode each metric as they are pure counters
The XML files are downloaded using the restapi download function and parse. this from 8.6.+ code. 
Virtualize systems that have lower code, then the SVCi will switch over and try with SCP to download the files. 



**Status of RestApi for Storage Virtualize v8.6.0**

- **Volume Stats**
  - Stats is not available  for individual volumes, only for the whole system
- **Disk stats**
  - Stats is not available  for  individual disk drives,
- **Host Stats**
  - Stats is not available  for individual hosts
- **FC Port Stats**
  - /lsportstats - From 8.6.0 code
- **iSCSI Port Stats**
  -  /lsportstats - From 8.6.0 code
- **Node stats**
  - /lsnodestats or lsnodecanisterstats
- **CPU Stats**
  - /lsnodestats
- **Latency**
  - Latency/ms overall for system on vdisk, mdisk, drive, cloud_up
    /lsnodestats or lsnodecanisterstats



## Here's an example curl command you can use to download the XML files using restapi
Need to get the token first, using auth. 

>:bolb: there is a restapi explorer/swagger on https://<clusterip>:7443/rest/explorer/  

1. Get a list of files on the node (config):
```shell
curl -X 'POST' \
 'https://<cluster_ip>:7443/rest/v1/lsdumps' \
 -H "X-Auth-Token: $token" \
 -d '{
 "prefix": "/dumps/iostats/"
}'
````
2. Download your favourite one:
```shell
curl -X 'POST' \
 'https://<cluster_ip>:7443/rest/v1/download' \
 -H "X-Auth-Token: $token" \
 -d '{
 "prefix": "/dumps/iostats",
 "filename": "Nn_stats_78E0021-1_221208_132315"
}'
```


## XML info:

Details from: 
https://www.ibm.com/docs/en/sanvolumecontroller/8.6.x?topic=troubleshooting-starting-statistics-collection

The IBM Storage Virtualize system collects statistics over an interval and creates files that can be viewed.

For each collection interval, the management GUI creates five statistics files: 
- one for managed disks (MDisks), which is named **Nm_stats**
- one for volumes and volume copies, which is named **Nv_stats**
- one for volumes groups, which is named **Ng_stats**
- one for nodes, which is named **Nn_stats**
- one for drives, which is named **Nd_stats**. 

The files are written to the /dumps/iostats directory on the node. To retrieve the statistics files from the non-configuration nodes onto the configuration node, use the svctask cpdumps command.

>:bulb: atm We are gathering infomation from **Nm_stats** and **Nv_stats**

In the same link there is an table that describe the Statistic name from the XML file.

|Statistic name   |  Description     |
| -------------   | ----------- |
|id	              |  Indicates the name of the MDisk for which the statistics apply.|
|idx	          |   Indicates the identifier of the MDisk for which the statistics apply.|


### Timestamp:

From v8.6. it's have UTC timestamp. 
SVCi uses timestampUtc if present, otherwise timestamp. So it's important that time is correct and NTP setup on the flashsystems

```json
timestamp="2024-03-25 17:42:51" timezone="GMT+0:00" timestamp_utc="2024-03-25 17:42:51">
On V8.5.0.x
contains="driveStats" timestamp="2024-03-25 19:30:21" timezone="GMT+1:00"> 
```

### Example of xml files that is collected with 

```json
[{ "filename": "Nn_stats_78F1981-1_240507_111254" },{ "port id": "1", "type": "FC", "type_id": "1", "wwpn": "0x5005076812111631", "fc_wwpn": "0x5005076812111631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "2391" , "hbr": "0" , "het": "0" , "her": "114", "cbt": "0" , "cbr": "459" , "cet": "66" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "11036984" , "lner": "11036978", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "28010" , "dtdc": "285" , "dtdm": "11037092", "dtdt2": "28010" , "dtdc2": "285", "lf": "3" , "lsy": "3" , "lsi": "2" , "pspe": "0", "itw": "140" , "icrc": "0" , "bbcz": "0", "tmp": "50" , "tmpht": "85", "txpwr": "519" , "txpwrlt": "126", "rxpwr": "612" , "rxpwrlt": "31", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "2", "type": "FC", "type_id": "2", "wwpn": "0x5005076812121631", "fc_wwpn": "0x5005076812121631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "0" , "dtdc": "0" , "dtdm": "0", "dtdt2": "0" , "dtdc2": "0", "lf": "0" , "lsy": "0" , "lsi": "0" , "pspe": "0", "itw": "0" , "icrc": "0" , "bbcz": "0", "tmp": "0" , "tmpht": "0", "txpwr": "0" , "txpwrlt": "0", "rxpwr": "0" , "rxpwrlt": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "3", "type": "FC", "type_id": "3", "wwpn": "0x5005076812131631", "fc_wwpn": "0x5005076812131631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "0" , "dtdc": "0" , "dtdm": "0", "dtdt2": "0" , "dtdc2": "0", "lf": "0" , "lsy": "0" , "lsi": "0" , "pspe": "0", "itw": "0" , "icrc": "0" , "bbcz": "0", "tmp": "0" , "tmpht": "0", "txpwr": "0" , "txpwrlt": "0", "rxpwr": "0" , "rxpwrlt": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "4", "type": "FC", "type_id": "4", "wwpn": "0x5005076812141631", "fc_wwpn": "0x5005076812141631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "0" , "dtdc": "0" , "dtdm": "0", "dtdt2": "0" , "dtdc2": "0", "lf": "0" , "lsy": "0" , "lsi": "0" , "pspe": "0", "itw": "0" , "icrc": "0" , "bbcz": "0", "tmp": "0" , "tmpht": "0", "txpwr": "0" , "txpwrlt": "0", "rxpwr": "0" , "rxpwrlt": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "5", "type": "FC", "type_id": "5", "wwpn": "0x0000000000000000", "fc_wwpn": "0x5005076812211631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "2391" , "hbr": "0" , "het": "0" , "her": "90", "cbt": "0" , "cbr": "459" , "cet": "66" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "11036822" , "lner": "11037507", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "21346" , "dtdc": "195" , "dtdm": "11037597", "dtdt2": "21346" , "dtdc2": "195", "lf": "3" , "lsy": "3" , "lsi": "2" , "pspe": "0", "itw": "28" , "icrc": "0" , "bbcz": "0", "tmp": "53" , "tmpht": "85", "txpwr": "517" , "txpwrlt": "126", "rxpwr": "627" , "rxpwrlt": "31", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "6", "type": "FC", "type_id": "6", "wwpn": "0x0000000000000000", "fc_wwpn": "0x5005076812221631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "0" , "dtdc": "0" , "dtdm": "0", "dtdt2": "0" , "dtdc2": "0", "lf": "0" , "lsy": "0" , "lsi": "0" , "pspe": "0", "itw": "0" , "icrc": "0" , "bbcz": "0", "tmp": "0" , "tmpht": "0", "txpwr": "0" , "txpwrlt": "0", "rxpwr": "0" , "rxpwrlt": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "7", "type": "FC", "type_id": "7", "wwpn": "0x0000000000000000", "fc_wwpn": "0x5005076812231631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "0" , "dtdc": "0" , "dtdm": "0", "dtdt2": "0" , "dtdc2": "0", "lf": "0" , "lsy": "0" , "lsi": "0" , "pspe": "0", "itw": "0" , "icrc": "0" , "bbcz": "0", "tmp": "0" , "tmpht": "0", "txpwr": "0" , "txpwrlt": "0", "rxpwr": "0" , "rxpwrlt": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "8", "type": "FC", "type_id": "8", "wwpn": "0x0000000000000000", "fc_wwpn": "0x5005076812241631", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "dtdt": "0" , "dtdc": "0" , "dtdm": "0", "dtdt2": "0" , "dtdc2": "0", "lf": "0" , "lsy": "0" , "lsi": "0" , "pspe": "0", "itw": "0" , "icrc": "0" , "bbcz": "0", "tmp": "0" , "tmpht": "0", "txpwr": "0" , "txpwrlt": "0", "rxpwr": "0" , "rxpwrlt": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "9", "type": "PCIe", "type_id": "1", "wwpn": "0x0000000000000000", "fc_wwpn": "", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "3950497" , "hbr": "122880" , "het": "0" , "her": "2056", "cbt": "258048" , "cbr": "24298317" , "cet": "2862" , "cer": "0", "lnbt": "539466033824" , "lnbr": "123495432567" , "lnet": "1322607377" , "lner": "1326902404", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0"},{ "port id": "10", "type": "NVMe", "type_id": "1", "wwpn": "0x0000000000000000", "fc_wwpn": "", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0"},{ "port id": "11", "type": "IPREP", "type_id": "1", "wwpn": "0x0000000000000000", "fc_wwpn": "", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "iptx": "0" , "iprx": "0" , "ipre": "0" , "ipsz": "0", "ipbz": "0" , "iprt": "0" , "iptc": "0" , "iprc": "0"},{ "port id": "12", "type": "iSCSI", "type_id": "1", "wwpn": "0x0000000000000000", "fc_wwpn": "", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "iqn.1986-03.com.ibm:2145.fs5200-2.node1", "hbt": "22958590" , "hbr": "12582912" , "het": "0" , "her": "182830", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"},{ "port id": "13", "type": "iSCSI", "type_id": "2", "wwpn": "0x0000000000000000", "fc_wwpn": "", "fcoe_wwpn": "", "sas_wwn": "", "iqn": "iqn.1986-03.com.ibm:2145.fs5200-2.node2", "hbt": "0" , "hbr": "0" , "het": "0" , "her": "0", "cbt": "0" , "cbr": "0" , "cet": "0" , "cer": "0", "lnbt": "0" , "lnbr": "0" , "lnet": "0" , "lner": "0", "rmbt": "0" , "rmbr": "0" , "rmet": "0" , "rmer": "0", "hsr": "0" , "hsw": "0" , "har": "0" , "haw": "0"}]
```
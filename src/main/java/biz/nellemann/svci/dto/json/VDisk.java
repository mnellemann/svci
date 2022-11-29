package biz.nellemann.svci.dto.json;

import biz.nellemann.svci.CapacityToDoubleConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VDisk {

    public String id;

    public String name;

    public String type;

    public String status;

    @JsonDeserialize(converter = CapacityToDoubleConverter.class)
    public Number capacity;

    @JsonProperty("IO_group_id")
    public Number ioGroupId;

    @JsonProperty("IO_group_name")
    public String ioGroupName;

    @JsonProperty("mdisk_grp_id")
    public Number mDiskGroupId;

    @JsonProperty("mdisk_grp_name")
    public String mDiskGroupName;

    @JsonProperty("parent_mdisk_grp_id")
    public Number parentMDiskGroupId;

    @JsonProperty("parent_mdisk_grp_name")
    public String parentMDiskGroupName;


    /*
    {
    "FC_id": "",
    "FC_name": "",
    "RC_id": "",
    "RC_name": "",
    "vdisk_UID": "6005076400840184F80000000000005F",
    "fc_map_count": "0",
    "copy_count": "1",
    "fast_write_state": "empty",
    "se_copy_count": "1",
    "RC_change": "no",
    "compressed_copy_count": "0",
    "owner_id": "",
    "owner_name": "",
    "formatting": "no",
    "encrypt": "no",
    "volume_id": "72",
    "volume_name": "volume-Image_rhcos_4-11_volume_1-e4c39c5a-c8bf",
    "function": "",
    "protocol": ""
    },
     */
}

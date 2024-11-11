package biz.nellemann.svci.dto.json;

import biz.nellemann.svci.CapacityToDoubleConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MDiskGroup {

    public String id;

    public String name;

    public String status;

    @JsonProperty("mdisk_count")
    public Long mDiskCount = 0L;

    @JsonProperty("vdisk_count")
    public Long vDiskCount = 0L;

    @JsonProperty("capacity")
    @JsonDeserialize(converter = CapacityToDoubleConverter.class)
    public Double capacity = 0.0;

    @JsonProperty("free_capacity")
    @JsonDeserialize(converter = CapacityToDoubleConverter.class)
    public Double capacityFree = 0.0;

    @JsonProperty("virtual_capacity")
    @JsonDeserialize(converter = CapacityToDoubleConverter.class)
    public Double capacityVirtual = 0.0;

    @JsonProperty("used_capacity")
    @JsonDeserialize(converter = CapacityToDoubleConverter.class)
    public Double capacityUsed = 0.0;

    @JsonProperty("real_capacity")
    @JsonDeserialize(converter = CapacityToDoubleConverter.class)
    public Double capacityReal = 0.0;

    @JsonProperty("parent_mdisk_grp_id")
    public String parentMDiskGroupId;

    @JsonProperty("parent_mdisk_grp_name")
    public String parentMDiskGroupName;

    /*
    {
    "extent_size": "1024",
    "overallocation": "41",
    "warning": "80",
    "easy_tier": "auto",
    "easy_tier_status": "balanced",
    "compression_active": "no",
    "compression_virtual_capacity": "0.00MB",
    "compression_compressed_capacity": "0.00MB",
    "compression_uncompressed_capacity": "0.00MB",
    "child_mdisk_grp_count": "0",
    "child_mdisk_grp_capacity": "0.00MB",
    "type": "parent",
    "encrypt": "no",
    "owner_type": "none",
    "owner_id": "",
    "owner_name": "",
    "site_id": "",
    "site_name": "",
    "data_reduction": "no",
    "used_capacity_before_reduction": "0.00MB",
    "used_capacity_after_reduction": "0.00MB",
    "overhead_capacity": "0.00MB",
    "deduplication_capacity_saving": "0.00MB",
    "reclaimable_capacity": "0.00MB",
    "easy_tier_fcm_over_allocation_max": "",
    "provisioning_policy_id": "",
    "provisioning_policy_name": ""
    },
    */
}

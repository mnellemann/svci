package biz.nellemann.svci.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class System {

    public String name;

    public String location;

    @JsonProperty("statistics_status")
    public String statisticsStatus;

    @JsonProperty("statistics_frequency")
    public Number statisticsFrequency;

    @JsonProperty("code_level")
    public String codeLevel;

    @JsonProperty("product_name")
    public String productName;


    /**

     "id": "000001002100613E",
     "name": "V7000_A2U12",
     "location": "local",
     "partnership": "",
     "total_mdisk_capacity": "60.9TB",
     "space_in_mdisk_grps": "60.9TB",
     "space_allocated_to_vdisks": "2.87TB",
     "total_free_space": "58.0TB",
     "total_vdiskcopy_capacity": "20.42TB",
     "total_used_capacity": "2.60TB",
     "total_overallocation": "33",
     "total_vdisk_capacity": "20.42TB",
     "total_allocated_extent_capacity": "2.92TB",
     "statistics_status": "on",
     "statistics_frequency": "5",
     "cluster_locale": "en_US",
     "time_zone": "13 Africa/Casablanca",
     "code_level": "8.4.2.0 (build 154.20.2109031944000)",
     "console_IP": "10.32.64.182:443",
     "id_alias": "000001002100613E",
     "gm_link_tolerance": "300",
     "gm_inter_cluster_delay_simulation": "0",
     "gm_intra_cluster_delay_simulation": "0",
     "gm_max_host_delay": "5",
     "email_reply": "",
     "email_contact": "",
     "email_contact_primary": "",
     "email_contact_alternate": "",
     "email_contact_location": "",
     "email_contact2": "",
     "email_contact2_primary": "",
     "email_contact2_alternate": "",
     "email_state": "stopped",
     "inventory_mail_interval": "0",
     "cluster_ntp_IP_address": "",
     "cluster_isns_IP_address": "",
     "iscsi_auth_method": "none",
     "iscsi_chap_secret": "",
     "auth_service_configured": "no",
     "auth_service_enabled": "no",
     "auth_service_url": "",
     "auth_service_user_name": "",
     "auth_service_pwd_set": "no",
     "auth_service_cert_set": "no",
     "auth_service_type": "ldap",
     "relationship_bandwidth_limit": "25",
     "tiers": [
     {
     "tier": "tier_scm",
     "tier_capacity": "0.00MB",
     "tier_free_capacity": "0.00MB"
     },
     {
     "tier": "tier0_flash",
     "tier_capacity": "0.00MB",
     "tier_free_capacity": "0.00MB"
     },
     {
     "tier": "tier1_flash",
     "tier_capacity": "49.17TB",
     "tier_free_capacity": "46.25TB"
     },
     {
     "tier": "tier_enterprise",
     "tier_capacity": "11.74TB",
     "tier_free_capacity": "11.74TB"
     },
     {
     "tier": "tier_nearline",
     "tier_capacity": "0.00MB",
     "tier_free_capacity": "0.00MB"
     }
     ],
     "easy_tier_acceleration": "off",
     "has_nas_key": "no",
     "layer": "storage",
     "rc_buffer_size": "256",
     "compression_active": "no",
     "compression_virtual_capacity": "0.00MB",
     "compression_compressed_capacity": "0.00MB",
     "compression_uncompressed_capacity": "0.00MB",
     "cache_prefetch": "on",
     "email_organization": "",
     "email_machine_address": "",
     "email_machine_city": "",
     "email_machine_state": "XX",
     "email_machine_zip": "",
     "email_machine_country": "",
     "total_drive_raw_capacity": "79.25TB",
     "compression_destage_mode": "off",
     "local_fc_port_mask": "1111111111111111111111111111111111111111111111111111111111111111",
     "partner_fc_port_mask": "1111111111111111111111111111111111111111111111111111111111111111",
     "high_temp_mode": "off",
     "topology": "standard",
     "topology_status": "",
     "rc_auth_method": "none",
     "vdisk_protection_time": "15",
     "vdisk_protection_enabled": "yes",
     "product_name": "IBM Storwize V7000",
     "odx": "off",
     "max_replication_delay": "0",
     "partnership_exclusion_threshold": "315",
     "gen1_compatibility_mode_enabled": "no",
     "ibm_customer": "",
     "ibm_component": "",
     "ibm_country": "",
     "tier_scm_compressed_data_used": "0.00MB",
     "tier0_flash_compressed_data_used": "0.00MB",
     "tier1_flash_compressed_data_used": "0.00MB",
     "tier_enterprise_compressed_data_used": "0.00MB",
     "tier_nearline_compressed_data_used": "0.00MB",
     "total_reclaimable_capacity": "0.00MB",
     "physical_capacity": "60.91TB",
     "physical_free_capacity": "58.00TB",
     "used_capacity_before_reduction": "0.00MB",
     "used_capacity_after_reduction": "0.00MB",
     "overhead_capacity": "0.00MB",
     "deduplication_capacity_saving": "0.00MB",
     "enhanced_callhome": "on",
     "censor_callhome": "off",
     "host_unmap": "off",
     "backend_unmap": "on",
     "quorum_mode": "standard",
     "quorum_site_id": "",
     "quorum_site_name": "",
     "quorum_lease": "short",
     "automatic_vdisk_analysis_enabled": "on",
     "callhome_accepted_usage": "no",
     "safeguarded_copy_suspended": "no"

     */
}

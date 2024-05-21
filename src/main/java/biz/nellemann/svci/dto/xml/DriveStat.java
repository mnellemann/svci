package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * See {@link https://www.ibm.com/docs/en/sanvolumecontroller/8.6.x?topic=troubleshooting-starting-statistics-collection}
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DriveStat {


    /**
     * Indicates the name of the MDisk for which the statistics apply.
     */
    @JsonProperty("id")
    public String id;


    /**
     * Indicates the identifier of the MDisk for which the statistics apply.
     */
    @JsonProperty("idx")
    public String idx;


    /**
     * Indicates the peak of read external response time in milliseconds for
     * each MDisk. The external response time for disk reads is calculated by
     * starting a timer when a SCSI read command is issued and stopped when the
     * command completes successfully.
     */
    @JsonProperty("pre")
    public Long pre;


    /**
     * Indicates the peak of read queued response time in milliseconds for each MDisk.
     * The value means the peak elapsed time that is taken for read commands to complete
     * from the time they join the queue.
     */
    @JsonProperty("pro")
    public Long pro;


    /**
     * Indicates the peak of write external response time in milliseconds for each MDisk
     * The external response time for disk writes is calculated by starting a timer when
     * a SCSI write command is issued and stopped when the command completes successfully.
     */
    @JsonProperty("pwe")
    public Long pwe;


    /**
     * Indicates the peak of write queued response time in milliseconds for each MDisk.
     * The value means the peak elapsed time that is taken for write commands to complete
     * from the time they join the queue.
     */
    @JsonProperty("pwo")
    public Long pwo;


    /**
     * Indicates the cumulative number of blocks of data that was read (since the node started).
     */
    @JsonProperty("rb")
    public Long rb;


    /**
     * Indicates the cumulative read external response time in milliseconds for each MDisk. The
     * cumulative response time for disk reads is calculated by starting a timer when a SCSI read
     * command is issued and stopped when the command completes successfully. The elapsed time
     * is added to the cumulative counter.
     */
    @JsonProperty("re")
    public Long re;


    /**
     * Indicates the cumulative number of MDisk read operations that were processed (since the node started).
     */
    @JsonProperty("ro")
    public Long ro;


    /**
     * Indicates the cumulative read queued response time in milliseconds for each MDisk. This response
     * is measured from above the queue of commands to be sent to an MDisk because the queue depth is
     * already full. This calculation includes the elapsed time that is taken for read commands to
     * complete from the time they join the queue.
     */
    @JsonProperty("rq")
    public Long rq;


    /**
     * Indicates the cumulative read external response time in microseconds for each MDisk.
     * The cumulative response time for disk reads is calculated by starting a timer when a SCSI
     * read command is issued and stopped when the command completes successfully. The elapsed
     * time is added to the cumulative counter.
     */
    @JsonProperty("ure")
    public Long ure;


    /**
     * Indicates the cumulative read queued response time in microseconds for each MDisk.
     * This response is measured from above the queue of commands to be sent to an MDisk
     * because the queue depth is already full. This calculation includes the elapsed time
     * that is taken for read commands to complete from the time they join the queue.
     */
    @JsonProperty("urq")
    public Long urq;


    /**
     * Indicates the cumulative write external response time in microseconds for each MDisk.
     * The cumulative response time for disk writes is calculated by starting a timer when a SCSI
     * write command is issued and stopped when the command completes successfully. The elapsed
     * time is added to the cumulative counter.
     */
    @JsonProperty("uwe")
    public Long uwe;


    /**
     * Indicates the cumulative write queued response time in microseconds for each MDisk.
     * This time is measured from above the queue of commands to be sent to an MDisk because
     * the queue depth is already full. This calculation includes the elapsed time that is
     * taken for write commands to complete from the time they join the queue.
     */
    @JsonProperty("uwq")
    public Long uwq;


    /**
     * Indicates the cumulative number of blocks of data written (since the node started).
     */
    @JsonProperty("wb")
    public Long wb;


    /**
     * Indicates the cumulative write external response time in milliseconds for each MDisk.
     * The cumulative response time for disk writes is calculated by starting a timer when an
     * SCSI write command is issued and stopped when the command completes successfully.
     * The elapsed time is added to the cumulative counter.
     *
     */
    @JsonProperty("we")
    public Long we;


    /**
     * Indicates the cumulative number of MDisk write operations that were processed (since the node started).
     */
    @JsonProperty("wo")
    public Long wo;


    /**
     * Indicates the cumulative write queued response time in milliseconds for each MDisk.
     * This time is measured from above the queue of commands to be sent to an MDisk because the queue depth
     * is already full. This calculation includes the elapsed time that is taken for write commands to
     * complete from the time they join the queue.
     */
    @JsonProperty("wq")
    public Long wq;


}

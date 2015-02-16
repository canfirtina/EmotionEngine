package persistentdatamanagement;

/**
 * Data structure to keep unprocessed sensor data.
 */
public interface SensorData {
    /**
     * Converts binary data to meaningful class variables.
     * @param bytes sensor data in byte representation
     */
    public void fromBinary(byte[] bytes);

    /**
     * Converts sensor data to binary format.
     * @return sensor data in bineary representation
     */
    public byte[] asBinary();
}

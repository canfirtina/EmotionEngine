package PersistentDataManagement;

/**
 * Created by Mustafa on 13.2.2015.
 */
public interface SensorData {
    public void fromBinary(byte[] bytes);
    public byte[] asBinary();
}

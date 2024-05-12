package io.streamnative;

import io.streamnative.oxia.client.api.GetResult;
import io.streamnative.oxia.client.api.OxiaClientBuilder;
import io.streamnative.oxia.client.api.SyncOxiaClient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientTests {

    private static OxiaClientBuilder builder = OxiaClientBuilder.create("localhost:6648");

    private SyncOxiaClient client;

    @Test
    public void singleWriteTest() throws Exception {
        try {
            client = builder.syncClient();
            String key = "test-key";
            byte[] value = "test-value".getBytes();
            client.put(key, value);

            GetResult retrievedValue = client.get(key);
            assertNotNull(retrievedValue);
            assertEquals(new String(value), new String(retrievedValue.getValue()));
            client.delete(key);

        } finally {
            client.close();
        }
    }

    @Test
    public void valueOverwriteTest() throws Exception {
        try {
            client = builder.syncClient();
            String key = "test-key2";
            byte[] value1 = "test-value".getBytes();
            byte[] value2 = "other-value".getBytes();

            client.put(key, value1);
            client.put(key, value2);

            GetResult retrievedValue = client.get(key);
            assertNotNull(retrievedValue);
            assertEquals(new String(value2), new String(retrievedValue.getValue()));
            client.delete(key);
        } finally {
            client.close();
        }
    }

}

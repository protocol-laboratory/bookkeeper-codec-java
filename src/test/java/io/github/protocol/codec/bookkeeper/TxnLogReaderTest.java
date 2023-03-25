package io.github.protocol.codec.bookkeeper;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TxnLogReaderTest {

    @Test
    public void testReadTxn() throws Exception {
        TxnLogReader reader = new TxnLogReader();
        TxnLog txnLog = reader.read(Resources.getResource("bk/bk-txn-log").getPath());
        Assertions.assertEquals(6, txnLog.getVersion());
    }

}

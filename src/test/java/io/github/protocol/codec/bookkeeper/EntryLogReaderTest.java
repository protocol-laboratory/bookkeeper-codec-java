package io.github.protocol.codec.bookkeeper;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntryLogReaderTest {

    @Test
    public void testReadEntry() throws Exception {
        EntryLogReader reader = new EntryLogReader();
        EntryLog entryLog = reader.read(Resources.getResource("bk/bk-entry-log").getPath());
        Assertions.assertEquals(1, entryLog.getVersion());
    }

}

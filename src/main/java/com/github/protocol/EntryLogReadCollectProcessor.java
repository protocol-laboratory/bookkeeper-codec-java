package com.github.protocol;

public class EntryLogReadCollectProcessor implements EntryLogReadProcessor {

    private final EntryLog entryLog = new EntryLog();

    @Override
    public void logHeader(EntryLogHeader header) {
        entryLog.setVersion(header.version);
    }

    @Override
    public void ledgerSize(long ledgerId, long size) {
        entryLog.putLedgerSize(ledgerId, size);
    }

    @Override
    public void entryContent(long ledgerId, long entryId, byte[] content) {
        entryLog.putEntry(ledgerId, entryId, content);
    }

    public EntryLog getEntryLog() {
        return entryLog;
    }
}

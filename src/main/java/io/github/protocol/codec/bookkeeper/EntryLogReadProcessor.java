package io.github.protocol.codec.bookkeeper;

public interface EntryLogReadProcessor {
    void logHeader(EntryLogHeader header);

    void ledgerSize(long ledgerId, long size);

    void entryContent(long ledgerId, long entryId, byte[] content);
}

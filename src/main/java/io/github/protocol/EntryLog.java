package io.github.protocol;

import java.util.HashMap;
import java.util.Map;

public class EntryLog {

    private int version;

    private final Map<Long, Long> ledgerSizeMap;

    private final Map<Long, Map<Long, byte[]>> ledgerEntryMap;

    public EntryLog() {
        this.ledgerSizeMap = new HashMap<>();
        this.ledgerEntryMap = new HashMap<>();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Long, Long> getLedgerSizeMap() {
        return ledgerSizeMap;
    }

    public Map<Long, Map<Long, byte[]>> getLedgerEntryMap() {
        return ledgerEntryMap;
    }

    public void putLedgerSize(long ledgerId, long size) {
        this.ledgerSizeMap.put(ledgerId, size);
    }

    public void getLedgerSize(long ledgerId) {
        this.ledgerSizeMap.get(ledgerId);
    }

    public void putEntry(long ledgerId, long entryId, byte[] content) {
        Map<Long, byte[]> entryMap = this.ledgerEntryMap.computeIfAbsent(ledgerId, k -> new HashMap<>());
        entryMap.put(entryId, content);
    }

    public byte[] getEntry(long ledgerId, long entryId) {
        Map<Long, byte[]> entryMap = this.ledgerEntryMap.get(ledgerId);
        if (entryMap == null) {
            return null;
        }
        return entryMap.get(entryId);
    }

}

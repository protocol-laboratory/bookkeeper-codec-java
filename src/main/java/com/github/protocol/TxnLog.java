package com.github.protocol;

import java.util.HashMap;
import java.util.Map;

public class TxnLog {

    private int version;

    private final Map<Long, Map<Long, TxnRecord>> ledgerRecordMap;

    public TxnLog() {
        this.ledgerRecordMap = new HashMap<>();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Long, Map<Long, TxnRecord>> getLedgerRecordMap() {
        return ledgerRecordMap;
    }

    public void putRecord(long ledgerId, long entryId, long lac, byte[] bytes) {
        TxnRecord txnRecord = new TxnRecord(ledgerId, entryId, lac, bytes);
        Map<Long, TxnRecord> recordMap = this.ledgerRecordMap.computeIfAbsent(ledgerId, k -> new HashMap<>());
        recordMap.put(entryId, txnRecord);
    }

    public TxnRecord getRecord(long ledgerId, long entryId) {
        Map<Long, TxnRecord> recordMap = this.ledgerRecordMap.get(ledgerId);
        if (recordMap == null) {
            return null;
        }
        return recordMap.get(entryId);
    }
}

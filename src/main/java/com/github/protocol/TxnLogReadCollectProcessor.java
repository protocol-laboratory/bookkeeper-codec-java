package com.github.protocol;

public class TxnLogReadCollectProcessor implements TxnLogReadProcessor {

    private final TxnLog txnLog = new TxnLog();

    @Override
    public void txnHeader(TxnHeader header) {
        txnLog.setVersion(header.version);
    }

    @Override
    public void ledgerMasterKey(long ledgerId, byte[] bytes) {
    }

    @Override
    public void ledgerFenced(long ledgerId) {
    }

    @Override
    public void entryContent(long ledgerId, long entryId, long lac, byte[] bytes) {
        txnLog.putRecord(ledgerId, entryId, lac, bytes);
    }

    public TxnLog getTxnLog() {
        return txnLog;
    }
}

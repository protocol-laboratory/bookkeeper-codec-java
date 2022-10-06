package com.github.protocol;

public interface TxnLogReadProcessor {
    void txnHeader(TxnHeader header);

    void ledgerMasterKey(long ledgerId, byte[] bytes);

    void ledgerFenced(long ledgerId);

    void entryContent(long ledgerId, long entryId, long lac, byte[] bytes);
}

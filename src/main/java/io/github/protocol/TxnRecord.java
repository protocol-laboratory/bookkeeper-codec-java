package io.github.protocol;

public class TxnRecord {

    private long ledgerId;

    private long entryId;

    private long lac;

    private byte[] content;

    public TxnRecord() {
    }

    public TxnRecord(long ledgerId, long entryId, long lac, byte[] content) {
        this.ledgerId = ledgerId;
        this.entryId = entryId;
        this.lac = lac;
        this.content = content;
    }

    public long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public long getLac() {
        return lac;
    }

    public void setLac(long lac) {
        this.lac = lac;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

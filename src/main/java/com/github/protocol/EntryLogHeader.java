package com.github.protocol;

public class EntryLogHeader {

    final int version;
    final long ledgersMapOffset;
    final int ledgersCount;

    EntryLogHeader(int version, long ledgersMapOffset, int ledgersCount) {
        this.version = version;
        this.ledgersMapOffset = ledgersMapOffset;
        this.ledgersCount = ledgersCount;
    }

}

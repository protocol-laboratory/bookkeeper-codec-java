package io.github.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class EntryLogReader {

    private static final int HEADER_SIZE = 1024;

    public EntryLog read(String fileName) throws Exception {
        EntryLogReadCollectProcessor processor = new EntryLogReadCollectProcessor();
        this.process(fileName, processor);
        return processor.getEntryLog();
    }

    public void process(String fileName, EntryLogReadProcessor processor) throws Exception {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
             FileChannel fileChannel = randomAccessFile.getChannel()) {
            final EntryLogHeader header = readHeader(fileChannel);
            processor.logHeader(header);

            // read ledger map at end of file
            long offset = header.ledgersMapOffset;

            int ledgersMapSize = ledgerMapSize(fileChannel, offset);

            readLedgerSize(processor, fileChannel, offset, ledgersMapSize);

            readEntry(processor, fileChannel, offset);
        }
    }

    private void readEntry(EntryLogReadProcessor processor, FileChannel fileChannel, long offset) throws IOException {
        fileChannel.position(HEADER_SIZE);
        final int size = (int) fileChannel.size();
        final int excludeHeaderCapacity = size - HEADER_SIZE;
        final int bodyCapacity = (int) (excludeHeaderCapacity - (size - offset));
        final ByteBuf bodyBuf = Unpooled.buffer(bodyCapacity);
        final int read = fileChannel.read(bodyBuf.internalNioBuffer(0, bodyCapacity), HEADER_SIZE);
        bodyBuf.writerIndex(read);
        while (bodyBuf.readableBytes() != 0) {
            final int entrySize = bodyBuf.readInt();
            final long ledgerId = bodyBuf.readLong();
            final long entryId = bodyBuf.readLong();
            final byte[] bodyBytes = new byte[entrySize - 16];
            bodyBuf.readBytes(bodyBytes);
            processor.entryContent(ledgerId, entryId, bodyBytes);
        }
    }

    private void readLedgerSize(EntryLogReadProcessor processor,
                                FileChannel fileChannel, long offset, int ledgersMapSize) throws IOException {
        final ByteBuf ledgerMapBuf = Unpooled.buffer(ledgersMapSize);
        final int readAux = fileChannel.read(ledgerMapBuf.internalNioBuffer(0, ledgersMapSize), offset + 4);
        ledgerMapBuf.writerIndex(readAux);

        // Discard ledgerId and entryId
        long lid = ledgerMapBuf.readLong();
        if (lid != -1) {
            throw new IOException("Cannot deserialize ledgers map from ledger " + lid);
        }

        long entryId = ledgerMapBuf.readLong();
        if (entryId != -2) {
            throw new IOException("Cannot deserialize ledgers map from entryId " + entryId);
        }

        // Read the number of ledgers in the current entry batch
        int ledgersCount = ledgerMapBuf.readInt();

        for (int i = 0; i < ledgersCount; i++) {
            final long ledgerId = ledgerMapBuf.readLong();
            final long size = ledgerMapBuf.readLong();
            processor.ledgerSize(ledgerId, size);
        }
    }

    private int ledgerMapSize(FileChannel fileChannel, long offset) throws Exception {
        final ByteBuf sizeBuf = Unpooled.buffer(4);
        final int read = fileChannel.read(sizeBuf.internalNioBuffer(0, 4), offset);
        sizeBuf.writerIndex(read);
        return sizeBuf.readInt();
    }

    private EntryLogHeader readHeader(FileChannel fileChannel) throws Exception {
        final ByteBuf headers = Unpooled.buffer(1024);
        final int read = fileChannel.read(headers.internalNioBuffer(0, 1024));
        headers.writerIndex(read);
        final byte[] bkloByte = new byte[4];
        headers.readBytes(bkloByte, 0, 4);
        final String bklo = new String(bkloByte);
        if (!bklo.equals("BKLO")) {
            throw new IllegalStateException("magic number is not bklo");
        }
        final int headerVersion = headers.readInt();
        final long ledgersMapOffset = headers.readLong();
        int ledgersCount = headers.readInt();
        return new EntryLogHeader(headerVersion, ledgersMapOffset, ledgersCount);
    }

}

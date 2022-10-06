package com.github.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class TxnLogReader {

    private static final int PADDING_MASK = -0x100;

    private static final long ENTRY_ID_LEDGER_KEY = -0x1000;

    private static final long ENTRY_ID_FENCE_KEY = -0x2000;

    private static final long ENTRY_ID_FORCE_LEDGER  = -0x4000;

    private static final long ENTRY_ID_LEDGER_EXPLICITLAC  = -0x8000;

    private static final int HEADER_SIZE = 512;

    public TxnLog read(String fileName) throws Exception {
        TxnLogReadCollectProcessor processor = new TxnLogReadCollectProcessor();
        this.process(fileName, processor);
        return processor.getTxnLog();
    }

    public void process(String fileName, TxnLogReadProcessor processor) throws Exception {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
             FileChannel fileChannel = randomAccessFile.getChannel()) {

            final TxnHeader header = readHeader(fileChannel);
            processor.txnHeader(header);

            {
                fileChannel.position(HEADER_SIZE);
                final int fileSize = (int) fileChannel.size();
                final int excludeHeaderCapacity = fileSize - HEADER_SIZE;
                final ByteBuf bodyBuf = Unpooled.buffer(excludeHeaderCapacity);
                final int read = fileChannel.read(bodyBuf.internalNioBuffer(0, excludeHeaderCapacity), HEADER_SIZE);
                bodyBuf.writerIndex(read);
                while (bodyBuf.readableBytes() != 0) {
                    final int size = bodyBuf.readInt();
                    if (size > 0) {
                        final long ledgerId = bodyBuf.readLong();
                        final long entryId = bodyBuf.readLong();
                        if (entryId < 0) {
                            if (entryId == ENTRY_ID_LEDGER_KEY) {
                                int masterKeySize = bodyBuf.readInt();
                                final byte[] bytes = new byte[masterKeySize];
                                bodyBuf.readBytes(bytes);
                                processor.ledgerMasterKey(ledgerId, bytes);
                            } else if (entryId == ENTRY_ID_FENCE_KEY) {
                                processor.ledgerFenced(ledgerId);
                            }
                            continue;
                        }
                        final long lac = bodyBuf.readLong();
                        final byte[] bytes = new byte[size - 24];
                        bodyBuf.readBytes(bytes);
                        processor.entryContent(ledgerId, entryId, lac, bytes);
                    } else if (size == 0) {
                        // do nothing
                    } else if (size == PADDING_MASK) {
                        final int readInt = bodyBuf.readInt();
                        final byte[] bytes = new byte[readInt];
                        bodyBuf.readBytes(bytes);
                    } else {
                        throw new IllegalStateException("can not reach here");
                    }
                }
            }
        }
    }

    private TxnHeader readHeader(FileChannel fileChannel) throws Exception {
        final ByteBuf headers = Unpooled.buffer(HEADER_SIZE);
        final int read = fileChannel.read(headers.internalNioBuffer(0, HEADER_SIZE));
        headers.writerIndex(read);
        final byte[] bklgByte = new byte[4];
        headers.readBytes(bklgByte, 0, 4);
        final int headerVersion = headers.readInt();
        return new TxnHeader(headerVersion);
    }


}

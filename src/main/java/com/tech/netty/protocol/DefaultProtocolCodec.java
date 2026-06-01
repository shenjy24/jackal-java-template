package com.tech.netty.protocol;

import io.netty.buffer.ByteBuf;

public class DefaultProtocolCodec implements ProtocolCodec {

  @Override
  public ProtocolFrame decodeFrame(ByteBuf in) {
    int frameStart = in.readerIndex();
    if (in.readableBytes() < ProtocolConstants.HEADER_SIZE + ProtocolConstants.CHECKSUM_SIZE) {
      return null;
    }

    short magic = in.readShort();
    if (magic != ProtocolConstants.MAGIC) {
      in.readerIndex(frameStart);
      throw new ProtocolDecodeException("Invalid magic: 0x" + Integer.toHexString(magic & 0xFFFF));
    }

    byte version = in.readByte();
    if (version != ProtocolConstants.VERSION) {
      in.readerIndex(frameStart);
      throw new ProtocolDecodeException("Unsupported version: " + version);
    }

    int msgType = in.readUnsignedShort();
    byte flags = in.readByte();
    int seqId = in.readInt();
    int bodyLength = in.readInt();

    if (in.readableBytes() < bodyLength + ProtocolConstants.CHECKSUM_SIZE) {
      in.readerIndex(frameStart);
      return null;
    }

    byte[] body = new byte[bodyLength];
    if (bodyLength > 0) {
      in.readBytes(body);
    }

    int checksum = in.readInt();
    int crcLength = in.readerIndex() - frameStart - ProtocolConstants.CHECKSUM_SIZE;
    int expected = Crc32Util.compute(in, frameStart, crcLength);
    if (checksum != expected) {
      throw new ProtocolDecodeException("CRC32 mismatch");
    }

    return ProtocolFrame.builder()
        .version(version)
        .msgType(msgType)
        .flags(flags)
        .seqId(seqId)
        .body(body)
        .build();
  }

  @Override
  public void encodeFrame(ProtocolFrame frame, ByteBuf out) {
    byte[] body = frame.getBody() == null ? new byte[0] : frame.getBody();
    int bodyLength = body.length;
    int frameStart = out.writerIndex();

    out.writeShort(ProtocolConstants.MAGIC);
    out.writeByte(frame.getVersion() == 0 ? ProtocolConstants.VERSION : frame.getVersion());
    out.writeShort(frame.getMsgType());
    out.writeByte(frame.getFlags());
    out.writeInt(frame.getSeqId());
    out.writeInt(bodyLength);
    if (bodyLength > 0) {
      out.writeBytes(body);
    }

    int checksum =
        Crc32Util.compute(out, frameStart, out.writerIndex() - frameStart);
    out.writeInt(checksum);
  }
}

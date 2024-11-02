package ca.teamdman.sfm;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class CompressionTests {
    private static final int DEFAULT_NBT_QUOTA = 2097152; // 2 MB

    @Test
    public void assert_naive_throws() {
        Set<BlockPos> positions = new HashSet<>();
        BlockPos first = new BlockPos(14, -58, 22);
        BlockPos second = new BlockPos(32, -25, 3);
        BlockPos.betweenClosedStream(first, second).map(BlockPos::immutable).forEach(positions::add);
        assertEquals(12920, positions.size());

        CompoundTag tag = new CompoundTag();
        tag.put(
                "sfm:cable_positions",
                positions.stream().map(NbtUtils::writeBlockPos).collect(ListTag::new, ListTag::add, ListTag::addAll)
        );
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeNbt(tag);
        System.out.printf("Compressed %d positions to %d bytes\n", positions.size(), buf.readableBytes());
        assertTrue(buf.readableBytes() < DEFAULT_NBT_QUOTA);
        RuntimeException exception = assertThrows(RuntimeException.class, buf::readNbt);
        exception.printStackTrace();
    }

    @Test
    public void assert_dense_works() {
        Set<BlockPos> positions = new HashSet<>();
        BlockPos first = new BlockPos(14, -58, 22);
        BlockPos second = new BlockPos(32, -25, 3);
        BlockPos.betweenClosedStream(first, second).map(BlockPos::immutable).forEach(positions::add);
        assertEquals(12920, positions.size());

        CompoundTag tag = new CompoundTag();
        tag.put(
                "sfm:cable_positions",
                positions
                        .stream()
                        .map(BlockPos::asLong)
                        .map(LongTag::valueOf)
                        .collect(ListTag::new, ListTag::add, ListTag::addAll)
        );
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeNbt(tag);
        System.out.printf("Compressed %d positions to %d bytes\n", positions.size(), buf.readableBytes());
        assertTrue(buf.readableBytes() < DEFAULT_NBT_QUOTA);
        buf.readNbt();
    }

    @Test
    public void assert_big_dense_throws() {
        Set<BlockPos> positions = new HashSet<>();
        BlockPos first = new BlockPos(0, 0, 0);
        BlockPos second = new BlockPos(255, 1, 255);
        BlockPos.betweenClosedStream(first, second).map(BlockPos::immutable).forEach(positions::add);
        // Total positions: 256 * 2 * 256 = 131072

        CompoundTag tag = new CompoundTag();
        tag.put(
                "sfm:cable_positions",
                positions
                        .stream()
                        .map(BlockPos::asLong)
                        .map(LongTag::valueOf)
                        .collect(ListTag::new, ListTag::add, ListTag::addAll)
        );
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeNbt(tag);
        System.out.printf("Compressed %d positions to %d bytes\n", positions.size(), buf.readableBytes());
        assertTrue(buf.readableBytes() < DEFAULT_NBT_QUOTA);
        RuntimeException exception = assertThrows(RuntimeException.class, buf::readNbt);
        exception.printStackTrace();
    }

    /**
     * Test method to compress large BlockPos set using GZIP and verify integrity.
     */
    @Test
    public void assert_big_gzip_works() {
        try {
            Set<BlockPos> positions = new HashSet<>();
            BlockPos first = new BlockPos(0, 0, 0);
            BlockPos second = new BlockPos(255, 1, 255);
            BlockPos.betweenClosedStream(first, second).map(BlockPos::immutable).forEach(positions::add);
            assertEquals(131072, positions.size());

            // Serialize the BlockPos set to bytes
            byte[] serializedData = serializeBlockPosSet(positions);

            // Compress using GZIP
            byte[] compressedData = compressGZIP(serializedData);
            System.out.printf("GZIP Compressed %d positions to %d bytes\n", positions.size(), compressedData.length);
            assertTrue(compressedData.length < DEFAULT_NBT_QUOTA, "GZIP compressed data exceeds quota");

            // Store compressed data in NBT
            CompoundTag tag = new CompoundTag();
            tag.putByteArray("sfm:cable_positions_compressed_gzip", compressedData);

            // Write NBT to buffer
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeNbt(tag);

            // Ensure buffer size is within quota
            System.out.printf("NBT Buffer Size after GZIP: %d bytes\n", buf.readableBytes());
            assertTrue(buf.readableBytes() < DEFAULT_NBT_QUOTA, "NBT buffer exceeds quota after GZIP compression");

            // Read NBT from buffer
            CompoundTag readTag = buf.readNbt();
            assertNotNull(readTag, "Read NBT tag is null");
            byte[] readCompressedData = readTag.getByteArray("sfm:cable_positions_compressed_gzip");
            assertNotNull(readCompressedData, "Read compressed data is null");

            // Decompress
            byte[] decompressedData = decompressGZIP(readCompressedData);

            // Deserialize
            Set<BlockPos> decompressedPositions = deserializeBlockPosSet(decompressedData);

            // Verify integrity
            assertEquals(positions, decompressedPositions, "Decompressed positions do not match original");
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }

    /**
     * Helper method to serialize Set<BlockPos> to a byte array as longs.
     */
    private byte[] serializeBlockPosSet(Set<BlockPos> blockPositions) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (BlockPos pos : blockPositions) {
            dos.writeLong(pos.asLong());
        }
        dos.close();
        return baos.toByteArray();
    }

    /**
     * Helper method to deserialize byte array back to Set<BlockPos>.
     */
    private Set<BlockPos> deserializeBlockPosSet(byte[] data) throws IOException {
        Set<BlockPos> blockPositions = new HashSet<>();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        while (dis.available() >= 8) { // Each long is 8 bytes
            long posLong = dis.readLong();
            blockPositions.add(BlockPos.of(posLong));
        }
        dis.close();
        return blockPositions;
    }

    /**
     * Compress data using GZIP.
     */
    private byte[] compressGZIP(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOS = new GZIPOutputStream(baos);
        gzipOS.write(data);
        gzipOS.close();
        return baos.toByteArray();
    }

    /**
     * Decompress data using GZIP.
     */
    private byte[] decompressGZIP(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        GZIPInputStream gzipIS = new GZIPInputStream(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = gzipIS.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        gzipIS.close();
        return baos.toByteArray();
    }
}

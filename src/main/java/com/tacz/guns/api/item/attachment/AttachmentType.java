package com.tacz.guns.api.item.attachment;

import com.google.gson.annotations.SerializedName;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum AttachmentType {
    /**
     * 瞄具
     */
    @SerializedName("scope")
    SCOPE(1),
    /**
     * 枪口组件
     */
    @SerializedName("muzzle")
    MUZZLE(2),
    /**
     * 枪托
     */
    @SerializedName("stock")
    STOCK(3),
    /**
     * 握把
     */
    @SerializedName("grip")
    GRIP(4),
    /**
     * 激光指示器
     */
    @SerializedName("laser")
    LASER(5),
    /**
     * 扩容弹夹（匣）
     */
    @SerializedName("extended_mag")
    EXTENDED_MAG(6),
    /**
     * 用来表示物品不是配件的情况。
     */
    NONE(0)
    ;
    private final int id;
    AttachmentType(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public static final IntFunction<AttachmentType> BY_ID =
            ByIdMap.continuous(
                    AttachmentType::getId,
                    AttachmentType.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );
    public static final StreamCodec<ByteBuf, AttachmentType> ID_STREAM_CODEC = ByteBufCodecs.idMapper(AttachmentType.BY_ID, AttachmentType::getId);
}

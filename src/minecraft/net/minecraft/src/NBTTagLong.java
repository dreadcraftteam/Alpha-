package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase {
	public long longValue;

	public NBTTagLong() {
	}

	public NBTTagLong(long value) {
		this.longValue = value;
	}

	void writeTagContents(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeLong(this.longValue);
	}

	void readTagContents(DataInput dataInput) throws IOException {
		this.longValue = dataInput.readLong();
	}

	public byte getType() {
		return (byte)4;
	}

	public String toString() {
		return "" + this.longValue;
	}
}

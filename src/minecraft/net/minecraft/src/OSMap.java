package net.minecraft.src;

public class OSMap {
	public static final int[] osValues = new int[EnumOS.values().length];

	static {
		try {
			osValues[EnumOS.linux.ordinal()] = 1;
		} catch (NoSuchFieldError noSuchFieldError4) {
		}

		try {
			osValues[EnumOS.solaris.ordinal()] = 2;
		} catch (NoSuchFieldError noSuchFieldError3) {
		}

		try {
			osValues[EnumOS.windows.ordinal()] = 3;
		} catch (NoSuchFieldError noSuchFieldError2) {
		}

		try {
			osValues[EnumOS.macos.ordinal()] = 4;
		} catch (NoSuchFieldError noSuchFieldError1) {
		}

	}
}

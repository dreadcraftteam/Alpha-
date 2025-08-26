package net.minecraft.src;

class OSMapIsom {
	static final int[] osValues = new int[EnumOSIsom.values().length];

	static {
		try {
			osValues[EnumOSIsom.linux.ordinal()] = 1;
		} catch (NoSuchFieldError noSuchFieldError4) {
		}

		try {
			osValues[EnumOSIsom.solaris.ordinal()] = 2;
		} catch (NoSuchFieldError noSuchFieldError3) {
		}

		try {
			osValues[EnumOSIsom.windows.ordinal()] = 3;
		} catch (NoSuchFieldError noSuchFieldError2) {
		}

		try {
			osValues[EnumOSIsom.macos.ordinal()] = 4;
		} catch (NoSuchFieldError noSuchFieldError1) {
		}

	}
}

package net.minecraft.src;

public class MovementInputFromOptions extends MovementInput {
	private boolean[] movementKeyStates = new boolean[10];
	private GameSettings gameSettings;

	public MovementInputFromOptions(GameSettings gameSettings) {
		this.gameSettings = gameSettings;
	}

	public void checkKeyForMovementInput(int key, boolean state) {
		byte b3 = -1;
		if(key == this.gameSettings.keyBindForward.keyCode) {
			b3 = 0;
		}

		if(key == this.gameSettings.keyBindBack.keyCode) {
			b3 = 1;
		}

		if(key == this.gameSettings.keyBindLeft.keyCode) {
			b3 = 2;
		}

		if(key == this.gameSettings.keyBindRight.keyCode) {
			b3 = 3;
		}

		if(key == this.gameSettings.keyBindJump.keyCode) {
			b3 = 4;
		}

		if(b3 >= 0) {
			this.movementKeyStates[b3] = state;
		}

	}

	public void resetKeyState() {
		for(int i1 = 0; i1 < 10; ++i1) {
			this.movementKeyStates[i1] = false;
		}

	}

	public void updatePlayerMoveState(EntityPlayer entityPlayer1) {
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;
		if(this.movementKeyStates[0]) {
			++this.moveForward;
		}

		if(this.movementKeyStates[1]) {
			--this.moveForward;
		}

		if(this.movementKeyStates[2]) {
			++this.moveStrafe;
		}

		if(this.movementKeyStates[3]) {
			--this.moveStrafe;
		}

		this.jump = this.movementKeyStates[4];
	}
}

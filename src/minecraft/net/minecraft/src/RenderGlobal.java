package net.minecraft.src;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class RenderGlobal implements IWorldAccess {
	public List tileEntities = new ArrayList();
	private World theWorld;
	private RenderEngine renderEngine;
	private List worldRenderersToUpdate = new ArrayList();
	private WorldRenderer[] sortedWorldRenderers;
	private WorldRenderer[] worldRenderers;
	private int renderChunksWide;
	private int renderChunksTall;
	private int renderChunksDeep;
	private int glRenderListBase;
	private Minecraft mc;
	private RenderBlocks globalRenderBlocks;
	private IntBuffer glOcclusionQueryBase;
	private boolean occlusionEnabled = false;
	private int cloudTickCounter = 0;
	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
	private int minBlockX;
	private int minBlockY;
	private int minBlockZ;
	private int maxBlockX;
	private int maxBlockY;
	private int maxBlockZ;
	private int renderDistance = -1;
	private int renderEntitiesStartupCounter = 2;
	private int countEntitiesTotal;
	private int countEntitiesRendered;
	private int countEntitiesHidden;
	int[] dummyBuf50k = new int[50000];
	IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
	private int renderersLoaded;
	private int renderersBeingClipped;
	private int renderersBeingOccluded;
	private int renderersBeingRendered;
	private int renderersSkippingRenderPass;
	private List glRenderLists = new ArrayList();
	private RenderList[] allRenderLists = new RenderList[]{new RenderList(), new RenderList(), new RenderList(), new RenderList()};
	int dummyRenderInt = 0;
	int unusedGLCallList = GLAllocation.generateDisplayLists(1);
	double prevSortX = -9999.0D;
	double prevSortY = -9999.0D;
	double prevSortZ = -9999.0D;
	public float damagePartialTime;
	int frustumCheckOffset = 0;

	public RenderGlobal(Minecraft minecraft1, RenderEngine renderEngine2) {
		this.mc = minecraft1;
		this.renderEngine = renderEngine2;
		byte b3 = 64;
		this.glRenderListBase = GLAllocation.generateDisplayLists(b3 * b3 * b3 * 3);
		this.occlusionEnabled = minecraft1.getOpenGlCapsChecker().checkARBOcclusion();
		if(this.occlusionEnabled) {
			this.occlusionResult.clear();
			this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(b3 * b3 * b3);
			this.glOcclusionQueryBase.clear();
			this.glOcclusionQueryBase.position(0);
			this.glOcclusionQueryBase.limit(b3 * b3 * b3);
			ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
		}

		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		Tessellator tessellator4 = Tessellator.instance;
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		byte b6 = 64;
		int i7 = 256 / b6 + 2;
		float f5 = 16.0F;

		int i8;
		int i9;
		for(i8 = -b6 * i7; i8 <= b6 * i7; i8 += b6) {
			for(i9 = -b6 * i7; i9 <= b6 * i7; i9 += b6) {
				tessellator4.startDrawingQuads();
				tessellator4.addVertex((double)(i8 + 0), (double)f5, (double)(i9 + 0));
				tessellator4.addVertex((double)(i8 + b6), (double)f5, (double)(i9 + 0));
				tessellator4.addVertex((double)(i8 + b6), (double)f5, (double)(i9 + b6));
				tessellator4.addVertex((double)(i8 + 0), (double)f5, (double)(i9 + b6));
				tessellator4.draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		f5 = -16.0F;
		tessellator4.startDrawingQuads();

		for(i8 = -b6 * i7; i8 <= b6 * i7; i8 += b6) {
			for(i9 = -b6 * i7; i9 <= b6 * i7; i9 += b6) {
				tessellator4.addVertex((double)(i8 + b6), (double)f5, (double)(i9 + 0));
				tessellator4.addVertex((double)(i8 + 0), (double)f5, (double)(i9 + 0));
				tessellator4.addVertex((double)(i8 + 0), (double)f5, (double)(i9 + b6));
				tessellator4.addVertex((double)(i8 + b6), (double)f5, (double)(i9 + b6));
			}
		}

		tessellator4.draw();
		GL11.glEndList();
	}

	private void renderStars() {
		Random random1 = new Random(10842L);
		Tessellator tessellator2 = Tessellator.instance;
		tessellator2.startDrawingQuads();

		for(int i3 = 0; i3 < 1500; ++i3) {
			double d4 = (double)(random1.nextFloat() * 2.0F - 1.0F);
			double d6 = (double)(random1.nextFloat() * 2.0F - 1.0F);
			double d8 = (double)(random1.nextFloat() * 2.0F - 1.0F);
			double d10 = (double)(0.25F + random1.nextFloat() * 0.25F);
			double d12 = d4 * d4 + d6 * d6 + d8 * d8;
			if(d12 < 1.0D && d12 > 0.01D) {
				d12 = 1.0D / Math.sqrt(d12);
				d4 *= d12;
				d6 *= d12;
				d8 *= d12;
				double d14 = d4 * 100.0D;
				double d16 = d6 * 100.0D;
				double d18 = d8 * 100.0D;
				double d20 = Math.atan2(d4, d8);
				double d22 = Math.sin(d20);
				double d24 = Math.cos(d20);
				double d26 = Math.atan2(Math.sqrt(d4 * d4 + d8 * d8), d6);
				double d28 = Math.sin(d26);
				double d30 = Math.cos(d26);
				double d32 = random1.nextDouble() * Math.PI * 2.0D;
				double d34 = Math.sin(d32);
				double d36 = Math.cos(d32);

				for(int i38 = 0; i38 < 4; ++i38) {
					double d39 = 0.0D;
					double d41 = (double)((i38 & 2) - 1) * d10;
					double d43 = (double)((i38 + 1 & 2) - 1) * d10;
					double d47 = d41 * d36 - d43 * d34;
					double d49 = d43 * d36 + d41 * d34;
					double d53 = d47 * d28 + d39 * d30;
					double d55 = d39 * d28 - d47 * d30;
					double d57 = d55 * d22 - d49 * d24;
					double d61 = d49 * d22 + d55 * d24;
					tessellator2.addVertex(d14 + d57, d16 + d53, d18 + d61);
				}
			}
		}

		tessellator2.draw();
	}

	public void changeWorld(World world1) {
		if(this.theWorld != null) {
			this.theWorld.removeWorldAccess(this);
		}

		this.prevSortX = -9999.0D;
		this.prevSortY = -9999.0D;
		this.prevSortZ = -9999.0D;
		RenderManager.instance.set(world1);
		this.theWorld = world1;
		this.globalRenderBlocks = new RenderBlocks(world1);
		if(world1 != null) {
			world1.addWorldAccess(this);
			this.loadRenderers();
		}

	}

	public void loadRenderers() {
		Block.leaves.setGraphicsLevel(this.mc.options.fancyGraphics);
		this.renderDistance = this.mc.options.renderDistance;
		int i1;
		if(this.worldRenderers != null) {
			for(i1 = 0; i1 < this.worldRenderers.length; ++i1) {
				this.worldRenderers[i1].stopRendering();
			}
		}

		i1 = 64 << 3 - this.renderDistance;
		if(i1 > 400) {
			i1 = 400;
		}

		this.renderChunksWide = i1 / 16 + 1;
		this.renderChunksTall = 8;
		this.renderChunksDeep = i1 / 16 + 1;
		this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
		this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
		int i2 = 0;
		int i3 = 0;
		this.minBlockX = 0;
		this.minBlockY = 0;
		this.minBlockZ = 0;
		this.maxBlockX = this.renderChunksWide;
		this.maxBlockY = this.renderChunksTall;
		this.maxBlockZ = this.renderChunksDeep;

		int i4;
		for(i4 = 0; i4 < this.worldRenderersToUpdate.size(); ++i4) {
			((WorldRenderer)this.worldRenderersToUpdate.get(i4)).needsUpdate = false;
		}

		this.worldRenderersToUpdate.clear();
		this.tileEntities.clear();

		for(i4 = 0; i4 < this.renderChunksWide; ++i4) {
			for(int i5 = 0; i5 < this.renderChunksTall; ++i5) {
				for(int i6 = 0; i6 < this.renderChunksDeep; ++i6) {
					this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4] = new WorldRenderer(this.theWorld, this.tileEntities, i4 * 16, i5 * 16, i6 * 16, 16, this.glRenderListBase + i2);
					if(this.occlusionEnabled) {
						this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].glOcclusionQuery = this.glOcclusionQueryBase.get(i3);
					}

					this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].isWaitingOnOcclusionQuery = false;
					this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].isVisible = true;
					this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].isInFrustum = true;
					this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].chunkIndex = i3++;
					this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].markDirty();
					this.sortedWorldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4] = this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4];
					this.worldRenderersToUpdate.add(this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4]);
					i2 += 3;
				}
			}
		}

		if(this.theWorld != null) {
			EntityPlayerSP entityPlayerSP7 = this.mc.thePlayer;
			this.markRenderersForNewPosition(MathHelper.floor_double(entityPlayerSP7.posX), MathHelper.floor_double(entityPlayerSP7.posY), MathHelper.floor_double(entityPlayerSP7.posZ));
			Arrays.sort(this.sortedWorldRenderers, new EntitySorter(entityPlayerSP7));
		}

		this.renderEntitiesStartupCounter = 2;
	}

	public void renderEntities(Vec3D vector, ICamera camera, float renderPartialTick) {
		if(this.renderEntitiesStartupCounter > 0) {
			--this.renderEntitiesStartupCounter;
		} else {
			TileEntityRenderer.instance.cacheActiveRenderInfo(this.theWorld, this.renderEngine, this.mc.fontRenderer, this.mc.thePlayer, renderPartialTick);
			RenderManager.instance.cacheActiveRenderInfo(this.theWorld, this.renderEngine, this.mc.fontRenderer, this.mc.thePlayer, this.mc.options, renderPartialTick);
			this.countEntitiesTotal = 0;
			this.countEntitiesRendered = 0;
			this.countEntitiesHidden = 0;
			EntityPlayerSP entityPlayerSP4 = this.mc.thePlayer;
			RenderManager.renderPosX = entityPlayerSP4.lastTickPosX + (entityPlayerSP4.posX - entityPlayerSP4.lastTickPosX) * (double)renderPartialTick;
			RenderManager.renderPosY = entityPlayerSP4.lastTickPosY + (entityPlayerSP4.posY - entityPlayerSP4.lastTickPosY) * (double)renderPartialTick;
			RenderManager.renderPosZ = entityPlayerSP4.lastTickPosZ + (entityPlayerSP4.posZ - entityPlayerSP4.lastTickPosZ) * (double)renderPartialTick;
			TileEntityRenderer.staticPlayerX = entityPlayerSP4.lastTickPosX + (entityPlayerSP4.posX - entityPlayerSP4.lastTickPosX) * (double)renderPartialTick;
			TileEntityRenderer.staticPlayerY = entityPlayerSP4.lastTickPosY + (entityPlayerSP4.posY - entityPlayerSP4.lastTickPosY) * (double)renderPartialTick;
			TileEntityRenderer.staticPlayerZ = entityPlayerSP4.lastTickPosZ + (entityPlayerSP4.posZ - entityPlayerSP4.lastTickPosZ) * (double)renderPartialTick;
			List list5 = this.theWorld.getLoadedEntityList();
			this.countEntitiesTotal = list5.size();

			int i6;
			for(i6 = 0; i6 < list5.size(); ++i6) {
				Entity entity7 = (Entity)list5.get(i6);
				if(entity7.isInRangeToRenderVec3D(vector) && camera.isBoundingBoxInFrustum(entity7.boundingBox) && (entity7 != this.mc.thePlayer || this.mc.options.thirdPersonView)) {
					++this.countEntitiesRendered;
					RenderManager.instance.renderEntity(entity7, renderPartialTick);
				}
			}

			for(i6 = 0; i6 < this.tileEntities.size(); ++i6) {
				TileEntityRenderer.instance.renderTileEntity((TileEntity)this.tileEntities.get(i6), renderPartialTick);
			}

		}
	}

	public String getDebugInfoRenders() {
		return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
	}

	public String getDebugInfoEntities() {
		return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
	}

	private void markRenderersForNewPosition(int i1, int i2, int i3) {
		i1 -= 8;
		i2 -= 8;
		i3 -= 8;
		this.minBlockX = Integer.MAX_VALUE;
		this.minBlockY = Integer.MAX_VALUE;
		this.minBlockZ = Integer.MAX_VALUE;
		this.maxBlockX = Integer.MIN_VALUE;
		this.maxBlockY = Integer.MIN_VALUE;
		this.maxBlockZ = Integer.MIN_VALUE;
		int i4 = this.renderChunksWide * 16;
		int i5 = i4 / 2;

		for(int i6 = 0; i6 < this.renderChunksWide; ++i6) {
			int i7 = i6 * 16;
			int i8 = i7 + i5 - i1;
			if(i8 < 0) {
				i8 -= i4 - 1;
			}

			i8 /= i4;
			i7 -= i8 * i4;
			if(i7 < this.minBlockX) {
				this.minBlockX = i7;
			}

			if(i7 > this.maxBlockX) {
				this.maxBlockX = i7;
			}

			for(int i9 = 0; i9 < this.renderChunksDeep; ++i9) {
				int i10 = i9 * 16;
				int i11 = i10 + i5 - i3;
				if(i11 < 0) {
					i11 -= i4 - 1;
				}

				i11 /= i4;
				i10 -= i11 * i4;
				if(i10 < this.minBlockZ) {
					this.minBlockZ = i10;
				}

				if(i10 > this.maxBlockZ) {
					this.maxBlockZ = i10;
				}

				for(int i12 = 0; i12 < this.renderChunksTall; ++i12) {
					int i13 = i12 * 16;
					if(i13 < this.minBlockY) {
						this.minBlockY = i13;
					}

					if(i13 > this.maxBlockY) {
						this.maxBlockY = i13;
					}

					WorldRenderer worldRenderer14 = this.worldRenderers[(i9 * this.renderChunksTall + i12) * this.renderChunksWide + i6];
					boolean z15 = worldRenderer14.needsUpdate;
					worldRenderer14.setPosition(i7, i13, i10);
					if(!z15 && worldRenderer14.needsUpdate) {
						this.worldRenderersToUpdate.add(worldRenderer14);
					}
				}
			}
		}

	}

	public int sortAndRender(EntityPlayer entityPlayer1, int i2, double d3) {
		if(this.mc.options.renderDistance != this.renderDistance) {
			this.loadRenderers();
		}

		if(i2 == 0) {
			this.renderersLoaded = 0;
			this.renderersBeingClipped = 0;
			this.renderersBeingOccluded = 0;
			this.renderersBeingRendered = 0;
			this.renderersSkippingRenderPass = 0;
		}

		double d5 = entityPlayer1.lastTickPosX + (entityPlayer1.posX - entityPlayer1.lastTickPosX) * d3;
		double d7 = entityPlayer1.lastTickPosY + (entityPlayer1.posY - entityPlayer1.lastTickPosY) * d3;
		double d9 = entityPlayer1.lastTickPosZ + (entityPlayer1.posZ - entityPlayer1.lastTickPosZ) * d3;
		double d11 = entityPlayer1.posX - this.prevSortX;
		double d13 = entityPlayer1.posY - this.prevSortY;
		double d15 = entityPlayer1.posZ - this.prevSortZ;
		if(d11 * d11 + d13 * d13 + d15 * d15 > 16.0D) {
			this.prevSortX = entityPlayer1.posX;
			this.prevSortY = entityPlayer1.posY;
			this.prevSortZ = entityPlayer1.posZ;
			this.markRenderersForNewPosition(MathHelper.floor_double(entityPlayer1.posX), MathHelper.floor_double(entityPlayer1.posY), MathHelper.floor_double(entityPlayer1.posZ));
			Arrays.sort(this.sortedWorldRenderers, new EntitySorter(entityPlayer1));
		}

		byte b17 = 0;
		int i33;
		if(this.occlusionEnabled && !this.mc.options.anaglyph && i2 == 0) {
			byte b18 = 0;
			int i19 = 16;
			this.checkOcclusionQueryResult(b18, i19);

			for(int i20 = b18; i20 < i19; ++i20) {
				this.sortedWorldRenderers[i20].isVisible = true;
			}

			i33 = b17 + this.renderSortedRenderers(b18, i19, i2, d3);

			do {
				int i34 = i19;
				i19 *= 2;
				if(i19 > this.sortedWorldRenderers.length) {
					i19 = this.sortedWorldRenderers.length;
				}

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glDisable(GL11.GL_FOG);
				GL11.glColorMask(false, false, false, false);
				GL11.glDepthMask(false);
				this.checkOcclusionQueryResult(i34, i19);
				GL11.glPushMatrix();
				float f35 = 0.0F;
				float f21 = 0.0F;
				float f22 = 0.0F;

				for(int i23 = i34; i23 < i19; ++i23) {
					if(this.sortedWorldRenderers[i23].skipAllRenderPasses()) {
						this.sortedWorldRenderers[i23].isInFrustum = false;
					} else {
						if(!this.sortedWorldRenderers[i23].isInFrustum) {
							this.sortedWorldRenderers[i23].isVisible = true;
						}

						if(this.sortedWorldRenderers[i23].isInFrustum && !this.sortedWorldRenderers[i23].isWaitingOnOcclusionQuery) {
							float f24 = MathHelper.sqrt_float(this.sortedWorldRenderers[i23].distanceToEntitySquared(entityPlayer1));
							int i25 = (int)(1.0F + f24 / 128.0F);
							if(this.cloudTickCounter % i25 == i23 % i25) {
								WorldRenderer worldRenderer26 = this.sortedWorldRenderers[i23];
								float f27 = (float)((double)worldRenderer26.posXMinus - d5);
								float f28 = (float)((double)worldRenderer26.posYMinus - d7);
								float f29 = (float)((double)worldRenderer26.posZMinus - d9);
								float f30 = f27 - f35;
								float f31 = f28 - f21;
								float f32 = f29 - f22;
								if(f30 != 0.0F || f31 != 0.0F || f32 != 0.0F) {
									GL11.glTranslatef(f30, f31, f32);
									f35 += f30;
									f21 += f31;
									f22 += f32;
								}

								ARBOcclusionQuery.glBeginQueryARB(GL15.GL_SAMPLES_PASSED, this.sortedWorldRenderers[i23].glOcclusionQuery);
								this.sortedWorldRenderers[i23].callOcclusionQueryList();
								ARBOcclusionQuery.glEndQueryARB(GL15.GL_SAMPLES_PASSED);
								this.sortedWorldRenderers[i23].isWaitingOnOcclusionQuery = true;
							}
						}
					}
				}

				GL11.glPopMatrix();
				GL11.glColorMask(true, true, true, true);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_FOG);
				i33 += this.renderSortedRenderers(i34, i19, i2, d3);
			} while(i19 < this.sortedWorldRenderers.length);
		} else {
			i33 = b17 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, i2, d3);
		}

		return i33;
	}

	private void checkOcclusionQueryResult(int i1, int i2) {
		for(int i3 = i1; i3 < i2; ++i3) {
			if(this.sortedWorldRenderers[i3].isWaitingOnOcclusionQuery) {
				this.occlusionResult.clear();
				ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[i3].glOcclusionQuery, GL15.GL_QUERY_RESULT_AVAILABLE, this.occlusionResult);
				if(this.occlusionResult.get(0) != 0) {
					this.sortedWorldRenderers[i3].isWaitingOnOcclusionQuery = false;
					this.occlusionResult.clear();
					ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[i3].glOcclusionQuery, GL15.GL_QUERY_RESULT, this.occlusionResult);
					this.sortedWorldRenderers[i3].isVisible = this.occlusionResult.get(0) != 0;
				}
			}
		}

	}

	private int renderSortedRenderers(int i1, int i2, int i3, double d4) {
		this.glRenderLists.clear();
		int i6 = 0;

		for(int i7 = i1; i7 < i2; ++i7) {
			if(i3 == 0) {
				++this.renderersLoaded;
				if(this.sortedWorldRenderers[i7].skipRenderPass[i3]) {
					++this.renderersSkippingRenderPass;
				} else if(!this.sortedWorldRenderers[i7].isInFrustum) {
					++this.renderersBeingClipped;
				} else if(this.occlusionEnabled && !this.sortedWorldRenderers[i7].isVisible) {
					++this.renderersBeingOccluded;
				} else {
					++this.renderersBeingRendered;
				}
			}

			if(!this.sortedWorldRenderers[i7].skipRenderPass[i3] && this.sortedWorldRenderers[i7].isInFrustum && this.sortedWorldRenderers[i7].isVisible) {
				int i8 = this.sortedWorldRenderers[i7].getGLCallListForPass(i3);
				if(i8 >= 0) {
					this.glRenderLists.add(this.sortedWorldRenderers[i7]);
					++i6;
				}
			}
		}

		EntityPlayerSP entityPlayerSP19 = this.mc.thePlayer;
		double d20 = entityPlayerSP19.lastTickPosX + (entityPlayerSP19.posX - entityPlayerSP19.lastTickPosX) * d4;
		double d10 = entityPlayerSP19.lastTickPosY + (entityPlayerSP19.posY - entityPlayerSP19.lastTickPosY) * d4;
		double d12 = entityPlayerSP19.lastTickPosZ + (entityPlayerSP19.posZ - entityPlayerSP19.lastTickPosZ) * d4;
		int i14 = 0;

		int i15;
		for(i15 = 0; i15 < this.allRenderLists.length; ++i15) {
			this.allRenderLists[i15].reset();
		}

		for(i15 = 0; i15 < this.glRenderLists.size(); ++i15) {
			WorldRenderer worldRenderer16 = (WorldRenderer)this.glRenderLists.get(i15);
			int i17 = -1;

			for(int i18 = 0; i18 < i14; ++i18) {
				if(this.allRenderLists[i18].isRenderedAt(worldRenderer16.posXMinus, worldRenderer16.posYMinus, worldRenderer16.posZMinus)) {
					i17 = i18;
				}
			}

			if(i17 < 0) {
				i17 = i14++;
				this.allRenderLists[i17].setLocation(worldRenderer16.posXMinus, worldRenderer16.posYMinus, worldRenderer16.posZMinus, d20, d10, d12);
			}

			this.allRenderLists[i17].render(worldRenderer16.getGLCallListForPass(i3));
		}

		this.renderAllRenderLists(i3, d4);
		return i6;
	}

	public void renderAllRenderLists(int i1, double d2) {
		for(int i4 = 0; i4 < this.allRenderLists.length; ++i4) {
			this.allRenderLists[i4].render();
		}

	}

	public void updateClouds() {
		++this.cloudTickCounter;
	}

	public void renderSky(float renderPartialTick) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3D vec3D2 = this.theWorld.getSkyColor(renderPartialTick);
		float f3 = (float)vec3D2.xCoord;
		float f4 = (float)vec3D2.yCoord;
		float f5 = (float)vec3D2.zCoord;
		float f7;
		float f8;
		if(this.mc.options.anaglyph) {
			float f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
			f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
			f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
			f3 = f6;
			f4 = f7;
			f5 = f8;
		}

		GL11.glColor3f(f3, f4, f5);
		Tessellator tessellator12 = Tessellator.instance;
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor3f(f3, f4, f5);
		GL11.glCallList(this.glSkyList);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glPushMatrix();
		f7 = 0.0F;
		f8 = 0.0F;
		float f9 = 0.0F;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef(f7, f8, f9);
		GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(this.theWorld.getCelestialAngle(renderPartialTick) * 360.0F, 1.0F, 0.0F, 0.0F);
		float f10 = 30.0F;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain/sun.png"));
		tessellator12.startDrawingQuads();
		tessellator12.addVertexWithUV((double)(-f10), 100.0D, (double)(-f10), 0.0D, 0.0D);
		tessellator12.addVertexWithUV((double)f10, 100.0D, (double)(-f10), 1.0D, 0.0D);
		tessellator12.addVertexWithUV((double)f10, 100.0D, (double)f10, 1.0D, 1.0D);
		tessellator12.addVertexWithUV((double)(-f10), 100.0D, (double)f10, 0.0D, 1.0D);
		tessellator12.draw();
		f10 = 20.0F;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain/moon.png"));
		tessellator12.startDrawingQuads();
		tessellator12.addVertexWithUV((double)(-f10), -100.0D, (double)f10, 1.0D, 1.0D);
		tessellator12.addVertexWithUV((double)f10, -100.0D, (double)f10, 0.0D, 1.0D);
		tessellator12.addVertexWithUV((double)f10, -100.0D, (double)(-f10), 0.0D, 0.0D);
		tessellator12.addVertexWithUV((double)(-f10), -100.0D, (double)(-f10), 1.0D, 0.0D);
		tessellator12.draw();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float f11 = this.theWorld.getStarBrightness(renderPartialTick);
		if(f11 > 0.0F) {
			GL11.glColor4f(f11, f11, f11, f11);
			GL11.glCallList(this.starGLCallList);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
		GL11.glColor3f(f3 * 0.2F + 0.04F, f4 * 0.2F + 0.04F, f5 * 0.6F + 0.1F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glCallList(this.glSkyList2);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
	}

	public void renderClouds(float f1) {
		if(this.mc.options.fancyGraphics) {
			this.renderCloudsFancy(f1);
		} else {
			GL11.glDisable(GL11.GL_CULL_FACE);
			float f2 = (float)(this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double)f1);
			byte b3 = 32;
			int i4 = 256 / b3;
			Tessellator tessellator5 = Tessellator.instance;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/clouds.png"));
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Vec3D vec3D6 = this.theWorld.getCloudColor(f1);
			float f7 = (float)vec3D6.xCoord;
			float f8 = (float)vec3D6.yCoord;
			float f9 = (float)vec3D6.zCoord;
			float f10;
			if(this.mc.options.anaglyph) {
				f10 = (f7 * 30.0F + f8 * 59.0F + f9 * 11.0F) / 100.0F;
				float f11 = (f7 * 30.0F + f8 * 70.0F) / 100.0F;
				float f12 = (f7 * 30.0F + f9 * 70.0F) / 100.0F;
				f7 = f10;
				f8 = f11;
				f9 = f12;
			}

			f10 = 4.8828125E-4F;
			double d22 = this.mc.thePlayer.prevPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX) * (double)f1 + (double)(((float)this.cloudTickCounter + f1) * 0.03F);
			double d13 = this.mc.thePlayer.prevPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ) * (double)f1;
			int i15 = MathHelper.floor_double(d22 / 2048.0D);
			int i16 = MathHelper.floor_double(d13 / 2048.0D);
			d22 -= (double)(i15 * 2048);
			d13 -= (double)(i16 * 2048);
			float f17 = 120.0F - f2 + 0.33F;
			float f18 = (float)(d22 * (double)f10);
			float f19 = (float)(d13 * (double)f10);
			tessellator5.startDrawingQuads();
			tessellator5.setColorRGBA_F(f7, f8, f9, 0.8F);

			for(int i20 = -b3 * i4; i20 < b3 * i4; i20 += b3) {
				for(int i21 = -b3 * i4; i21 < b3 * i4; i21 += b3) {
					tessellator5.addVertexWithUV((double)(i20 + 0), (double)f17, (double)(i21 + b3), (double)((float)(i20 + 0) * f10 + f18), (double)((float)(i21 + b3) * f10 + f19));
					tessellator5.addVertexWithUV((double)(i20 + b3), (double)f17, (double)(i21 + b3), (double)((float)(i20 + b3) * f10 + f18), (double)((float)(i21 + b3) * f10 + f19));
					tessellator5.addVertexWithUV((double)(i20 + b3), (double)f17, (double)(i21 + 0), (double)((float)(i20 + b3) * f10 + f18), (double)((float)(i21 + 0) * f10 + f19));
					tessellator5.addVertexWithUV((double)(i20 + 0), (double)f17, (double)(i21 + 0), (double)((float)(i20 + 0) * f10 + f18), (double)((float)(i21 + 0) * f10 + f19));
				}
			}

			tessellator5.draw();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
	}

	public void renderCloudsFancy(float f1) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		float f2 = (float)(this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double)f1);
		Tessellator tessellator3 = Tessellator.instance;
		float f4 = 12.0F;
		float f5 = 4.0F;
		double d6 = (this.mc.thePlayer.prevPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX) * (double)f1 + (double)(((float)this.cloudTickCounter + f1) * 0.03F)) / (double)f4;
		double d8 = (this.mc.thePlayer.prevPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ) * (double)f1) / (double)f4 + (double)0.33F;
		float f10 = 108.0F - f2 + 0.33F;
		int i11 = MathHelper.floor_double(d6 / 2048.0D);
		int i12 = MathHelper.floor_double(d8 / 2048.0D);
		d6 -= (double)(i11 * 2048);
		d8 -= (double)(i12 * 2048);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/clouds.png"));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Vec3D vec3D13 = this.theWorld.getCloudColor(f1);
		float f14 = (float)vec3D13.xCoord;
		float f15 = (float)vec3D13.yCoord;
		float f16 = (float)vec3D13.zCoord;
		float f17;
		float f18;
		float f19;
		if(this.mc.options.anaglyph) {
			f17 = (f14 * 30.0F + f15 * 59.0F + f16 * 11.0F) / 100.0F;
			f18 = (f14 * 30.0F + f15 * 70.0F) / 100.0F;
			f19 = (f14 * 30.0F + f16 * 70.0F) / 100.0F;
			f14 = f17;
			f15 = f18;
			f16 = f19;
		}

		f17 = (float)(d6 * 0.0D);
		f18 = (float)(d8 * 0.0D);
		f19 = 0.00390625F;
		f17 = (float)MathHelper.floor_double(d6) * f19;
		f18 = (float)MathHelper.floor_double(d8) * f19;
		float f20 = (float)(d6 - (double)MathHelper.floor_double(d6));
		float f21 = (float)(d8 - (double)MathHelper.floor_double(d8));
		byte b22 = 8;
		byte b23 = 3;
		float f24 = 9.765625E-4F;
		GL11.glScalef(f4, 1.0F, f4);

		for(int i25 = 0; i25 < 2; ++i25) {
			if(i25 == 0) {
				GL11.glColorMask(false, false, false, false);
			} else {
				GL11.glColorMask(true, true, true, true);
			}

			for(int i26 = -b23 + 1; i26 <= b23; ++i26) {
				for(int i27 = -b23 + 1; i27 <= b23; ++i27) {
					tessellator3.startDrawingQuads();
					float f28 = (float)(i26 * b22);
					float f29 = (float)(i27 * b22);
					float f30 = f28 - f20;
					float f31 = f29 - f21;
					if(f10 > -f5 - 1.0F) {
						tessellator3.setColorRGBA_F(f14 * 0.7F, f15 * 0.7F, f16 * 0.7F, 0.8F);
						tessellator3.setNormal(0.0F, -1.0F, 0.0F);
						tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + 0.0F), (double)(f31 + (float)b22), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
						tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + 0.0F), (double)(f31 + (float)b22), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
						tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + 0.0F), (double)(f31 + 0.0F), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
						tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + 0.0F), (double)(f31 + 0.0F), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
					}

					if(f10 <= f5 + 1.0F) {
						tessellator3.setColorRGBA_F(f14, f15, f16, 0.8F);
						tessellator3.setNormal(0.0F, 1.0F, 0.0F);
						tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + f5 - f24), (double)(f31 + (float)b22), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
						tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + f5 - f24), (double)(f31 + (float)b22), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
						tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + f5 - f24), (double)(f31 + 0.0F), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
						tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + f5 - f24), (double)(f31 + 0.0F), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
					}

					tessellator3.setColorRGBA_F(f14 * 0.9F, f15 * 0.9F, f16 * 0.9F, 0.8F);
					int i32;
					if(i26 > -1) {
						tessellator3.setNormal(-1.0F, 0.0F, 0.0F);

						for(i32 = 0; i32 < b22; ++i32) {
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 0.0F), (double)(f10 + 0.0F), (double)(f31 + (float)b22), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 0.0F), (double)(f10 + f5), (double)(f31 + (float)b22), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 0.0F), (double)(f10 + f5), (double)(f31 + 0.0F), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 0.0F), (double)(f10 + 0.0F), (double)(f31 + 0.0F), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
						}
					}

					if(i26 <= 1) {
						tessellator3.setNormal(1.0F, 0.0F, 0.0F);

						for(i32 = 0; i32 < b22; ++i32) {
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 1.0F - f24), (double)(f10 + 0.0F), (double)(f31 + (float)b22), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 1.0F - f24), (double)(f10 + f5), (double)(f31 + (float)b22), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + (float)b22) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 1.0F - f24), (double)(f10 + f5), (double)(f31 + 0.0F), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)i32 + 1.0F - f24), (double)(f10 + 0.0F), (double)(f31 + 0.0F), (double)((f28 + (float)i32 + 0.5F) * f19 + f17), (double)((f29 + 0.0F) * f19 + f18));
						}
					}

					tessellator3.setColorRGBA_F(f14 * 0.8F, f15 * 0.8F, f16 * 0.8F, 0.8F);
					if(i27 > -1) {
						tessellator3.setNormal(0.0F, 0.0F, -1.0F);

						for(i32 = 0; i32 < b22; ++i32) {
							tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + f5), (double)(f31 + (float)i32 + 0.0F), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + f5), (double)(f31 + (float)i32 + 0.0F), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + 0.0F), (double)(f31 + (float)i32 + 0.0F), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + 0.0F), (double)(f31 + (float)i32 + 0.0F), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
						}
					}

					if(i27 <= 1) {
						tessellator3.setNormal(0.0F, 0.0F, 1.0F);

						for(i32 = 0; i32 < b22; ++i32) {
							tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + f5), (double)(f31 + (float)i32 + 1.0F - f24), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + f5), (double)(f31 + (float)i32 + 1.0F - f24), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + (float)b22), (double)(f10 + 0.0F), (double)(f31 + (float)i32 + 1.0F - f24), (double)((f28 + (float)b22) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
							tessellator3.addVertexWithUV((double)(f30 + 0.0F), (double)(f10 + 0.0F), (double)(f31 + (float)i32 + 1.0F - f24), (double)((f28 + 0.0F) * f19 + f17), (double)((f29 + (float)i32 + 0.5F) * f19 + f18));
						}
					}

					tessellator3.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public boolean updateRenderers(EntityPlayer entityPlayer1, boolean z2) {
		Collections.sort(this.worldRenderersToUpdate, new RenderSorter(entityPlayer1));
		int i3 = this.worldRenderersToUpdate.size() - 1;
		int i4 = this.worldRenderersToUpdate.size();

		for(int i5 = 0; i5 < i4; ++i5) {
			WorldRenderer worldRenderer6 = (WorldRenderer)this.worldRenderersToUpdate.get(i3 - i5);
			if(!z2) {
				if(worldRenderer6.distanceToEntitySquared(entityPlayer1) > 1024.0F) {
					if(worldRenderer6.isInFrustum) {
						if(i5 >= 3) {
							return false;
						}
					} else if(i5 >= 1) {
						return false;
					}
				}
			} else if(!worldRenderer6.isInFrustum) {
				continue;
			}

			worldRenderer6.updateRenderer();
			this.worldRenderersToUpdate.remove(worldRenderer6);
			worldRenderer6.needsUpdate = false;
		}

		return this.worldRenderersToUpdate.size() == 0;
	}

	public void drawBlockBreaking(EntityPlayer entityPlayer1, MovingObjectPosition movingObjectPosition2, int i3, ItemStack itemStack4, float f5) {
		Tessellator tessellator6 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
		int i8;
		if(i3 == 0) {
			if(this.damagePartialTime > 0.0F) {
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
				int i7 = this.renderEngine.getTexture("/terrain.png");
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, i7);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
				GL11.glPushMatrix();
				i8 = this.theWorld.getBlockId(movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ);
				Block block9 = i8 > 0 ? Block.blocksList[i8] : null;
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glPolygonOffset(-3.0F, -3.0F);
				GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
				tessellator6.startDrawingQuads();
				double d10 = entityPlayer1.lastTickPosX + (entityPlayer1.posX - entityPlayer1.lastTickPosX) * (double)f5;
				double d12 = entityPlayer1.lastTickPosY + (entityPlayer1.posY - entityPlayer1.lastTickPosY) * (double)f5;
				double d14 = entityPlayer1.lastTickPosZ + (entityPlayer1.posZ - entityPlayer1.lastTickPosZ) * (double)f5;
				tessellator6.setTranslationD(-d10, -d12, -d14);
				tessellator6.disableColor();
				if(block9 == null) {
					block9 = Block.stone;
				}

				this.globalRenderBlocks.renderBlockUsingTexture(block9, movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ, 240 + (int)(this.damagePartialTime * 10.0F));
				tessellator6.draw();
				tessellator6.setTranslationD(0.0D, 0.0D, 0.0D);
				GL11.glPolygonOffset(0.0F, 0.0F);
				GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glDepthMask(true);
				GL11.glPopMatrix();
			}
		} else if(itemStack4 != null) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float f16 = MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.8F;
			GL11.glColor4f(f16, f16, f16, MathHelper.sin((float)System.currentTimeMillis() / 200.0F) * 0.2F + 0.5F);
			i8 = this.renderEngine.getTexture("/terrain.png");
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i8);
			int i17 = movingObjectPosition2.blockX;
			int i18 = movingObjectPosition2.blockY;
			int i11 = movingObjectPosition2.blockZ;
			if(movingObjectPosition2.sideHit == 0) {
				--i18;
			}

			if(movingObjectPosition2.sideHit == 1) {
				++i18;
			}

			if(movingObjectPosition2.sideHit == 2) {
				--i11;
			}

			if(movingObjectPosition2.sideHit == 3) {
				++i11;
			}

			if(movingObjectPosition2.sideHit == 4) {
				--i17;
			}

			if(movingObjectPosition2.sideHit == 5) {
				++i17;
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}

	public void drawSelectionBox(EntityPlayer entityPlayer1, MovingObjectPosition movingObjectPosition2, int i3, ItemStack itemStack4, float f5) {
		if(i3 == 0 && movingObjectPosition2.typeOfHit == 0) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			float f6 = 0.002F;
			int i7 = this.theWorld.getBlockId(movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ);
			if(i7 > 0) {
				Block.blocksList[i7].setBlockBoundsBasedOnState(this.theWorld, movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ);
				double d8 = entityPlayer1.lastTickPosX + (entityPlayer1.posX - entityPlayer1.lastTickPosX) * (double)f5;
				double d10 = entityPlayer1.lastTickPosY + (entityPlayer1.posY - entityPlayer1.lastTickPosY) * (double)f5;
				double d12 = entityPlayer1.lastTickPosZ + (entityPlayer1.posZ - entityPlayer1.lastTickPosZ) * (double)f5;
				this.drawOutlinedBoundingBox(Block.blocksList[i7].getSelectedBoundingBoxFromPool(this.theWorld, movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ).expand((double)f6, (double)f6, (double)f6).getOffsetBoundingBox(-d8, -d10, -d12));
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}

	}

	private void drawOutlinedBoundingBox(AxisAlignedBB axisAlignedBB1) {
		Tessellator tessellator2 = Tessellator.instance;
		tessellator2.startDrawing(3);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.draw();
		tessellator2.startDrawing(3);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.draw();
		tessellator2.startDrawing(1);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.draw();
	}

	public void markBlocksForUpdate(int i1, int i2, int i3, int i4, int i5, int i6) {
		int i7 = MathHelper.bucketInt(i1, 16);
		int i8 = MathHelper.bucketInt(i2, 16);
		int i9 = MathHelper.bucketInt(i3, 16);
		int i10 = MathHelper.bucketInt(i4, 16);
		int i11 = MathHelper.bucketInt(i5, 16);
		int i12 = MathHelper.bucketInt(i6, 16);

		for(int i13 = i7; i13 <= i10; ++i13) {
			int i14 = i13 % this.renderChunksWide;
			if(i14 < 0) {
				i14 += this.renderChunksWide;
			}

			for(int i15 = i8; i15 <= i11; ++i15) {
				int i16 = i15 % this.renderChunksTall;
				if(i16 < 0) {
					i16 += this.renderChunksTall;
				}

				for(int i17 = i9; i17 <= i12; ++i17) {
					int i18 = i17 % this.renderChunksDeep;
					if(i18 < 0) {
						i18 += this.renderChunksDeep;
					}

					int i19 = (i18 * this.renderChunksTall + i16) * this.renderChunksWide + i14;
					WorldRenderer worldRenderer20 = this.worldRenderers[i19];
					if(!worldRenderer20.needsUpdate) {
						this.worldRenderersToUpdate.add(worldRenderer20);
					}

					worldRenderer20.markDirty();
				}
			}
		}

	}

	public void markBlockAndNeighborsNeedsUpdate(int i1, int i2, int i3) {
		this.markBlocksForUpdate(i1 - 1, i2 - 1, i3 - 1, i1 + 1, i2 + 1, i3 + 1);
	}

	public void markBlockRangeNeedsUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this.markBlocksForUpdate(minX - 1, minY - 1, minZ - 1, maxX + 1, maxY + 1, maxZ + 1);
	}

	public void clipRenderersByFrustum(ICamera iCamera1, float f2) {
		for(int i3 = 0; i3 < this.worldRenderers.length; ++i3) {
			if(!this.worldRenderers[i3].skipAllRenderPasses() && (!this.worldRenderers[i3].isInFrustum || (i3 + this.frustumCheckOffset & 15) == 0)) {
				this.worldRenderers[i3].updateInFrustum(iCamera1);
			}
		}

		++this.frustumCheckOffset;
	}

	public void playRecord(String record, int x, int y, int z) {
		if(record != null) {
			this.mc.ingameGUI.setRecordPlayingMessage("C418 - " + record);
		}

		this.mc.sndManager.playStreaming(record, (float)x, (float)y, (float)z, 1.0F, 1.0F);
	}

	public void playSound(String sound, double posX, double posY, double posZ, float volume, float pitch) {
		float f10 = 16.0F;
		if(volume > 1.0F) {
			f10 *= volume;
		}

		if(this.mc.thePlayer.getDistanceSq(posX, posY, posZ) < (double)(f10 * f10)) {
			this.mc.sndManager.playSound(sound, (float)posX, (float)posY, (float)posZ, volume, pitch);
		}

	}

	public void spawnParticle(String string1, double d2, double d4, double d6, double d8, double d10, double d12) {
		double d14 = this.mc.thePlayer.posX - d2;
		double d16 = this.mc.thePlayer.posY - d4;
		double d18 = this.mc.thePlayer.posZ - d6;
		if(d14 * d14 + d16 * d16 + d18 * d18 <= 256.0D) {
			if(string1 == "bubble") {
				this.mc.effectRenderer.addEffect(new EntityBubbleFX(this.theWorld, d2, d4, d6, d8, d10, d12));
			} else if(string1 == "smoke") {
				this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.theWorld, d2, d4, d6));
			} else if(string1 == "explode") {
				this.mc.effectRenderer.addEffect(new EntityExplodeFX(this.theWorld, d2, d4, d6, d8, d10, d12));
			} else if(string1 == "flame") {
				this.mc.effectRenderer.addEffect(new EntityFlameFX(this.theWorld, d2, d4, d6, d8, d10, d12));
			} else if(string1 == "lava") {
				this.mc.effectRenderer.addEffect(new EntityLavaFX(this.theWorld, d2, d4, d6));
			} else if(string1 == "splash") {
				this.mc.effectRenderer.addEffect(new EntitySplashFX(this.theWorld, d2, d4, d6, d8, d10, d12));
			} else if(string1 == "largesmoke") {
				this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.theWorld, d2, d4, d6, 2.5F));
			} else if(string1 == "reddust") {
				this.mc.effectRenderer.addEffect(new EntityReddustFX(this.theWorld, d2, d4, d6));
			} else if(string1 == "snowballpoof") {
				this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.theWorld, d2, d4, d6, Item.snowball));
			} else if(string1 == "slime") {
				this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.theWorld, d2, d4, d6, Item.slimeBall));
			}

		}
	}

	public void obtainEntitySkin(Entity entity) {
		if(entity.skinUrl != null) {
			this.renderEngine.obtainImageData(entity.skinUrl, new ImageBufferDownload());
		}

	}

	public void releaseEntitySkin(Entity entity) {
		if(entity.skinUrl != null) {
			this.renderEngine.releaseImageData(entity.skinUrl);
		}

	}

	public void updateAllRenderers() {
		for(int i1 = 0; i1 < this.worldRenderers.length; ++i1) {
			if(this.worldRenderers[i1].isChunkLit) {
				if(!this.worldRenderers[i1].needsUpdate) {
					this.worldRenderersToUpdate.add(this.worldRenderers[i1]);
				}

				this.worldRenderers[i1].markDirty();
			}
		}

	}

	public void doNothingWithTileEntity(int x, int y, int z, TileEntity tileEntity) {
	}
}

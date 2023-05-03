package net.altias.simplekelpies.entity.custom;

import net.altias.simplekelpies.SimpleKelpies;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class KelpieEntity extends AbstractHorseEntity implements Angerable {
    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);

    public Entity lastPass;

    @Nullable
    private UUID angryAt;

    public KelpieEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new TakeRiderToWaterGoal(this));
        this.goalSelector.add(2, new SlayRiderGoal(this));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new SlayRiderGoal(this, new Class[0]));
        this.targetSelector.add(4, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(8, new UniversalAngerGoal<KelpieEntity>(this, true));
        if (this.shouldAmbientStand() && !this.hasAngerTime() &&!this.hasPassengers()) {
            this.goalSelector.add(9, new AmbientStandGoal(this));
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER_TIME, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.world, nbt);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity)
    {
        return null;
    }

    @Override
    public EntityView method_48926() {
        return null;
    }

    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.dataTracker.set(ANGER_TIME, angerTime);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    @Override
    protected void playWalkSound(BlockSoundGroup group) {
        super.playWalkSound(group);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, group.getVolume() * 0.6f, group.getPitch());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.hasAngerTime()) {
            return SoundEvents.ENTITY_WOLF_GROWL;
        }

        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getEatSound() {
        return SoundEvents.ENTITY_HORSE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.ENTITY_HORSE_ANGRY;
    }

    @Override
    public boolean eatsGrass()
    {
        if (hasAngerTime())
        {
            return false;
        }
        return true;
    }

    @Override
    public void tickMovement()
    {
        super.tickMovement();

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }
    }

    @Override
    public void tick() {

        super.tick();

        if (this.hasPassengers())
        {
            lastPass = getFirstPassenger();
        }

        if (lastPass != null) {
            if (!lastPass.isAlive()) {
                lastPass = null;
            }
        }



    }

    class TakeRiderToWaterGoal extends MoveIntoWaterGoal
    {
        private final PathAwareEntity mob;
        private LivingEntity pass;

        public TakeRiderToWaterGoal(PathAwareEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public boolean canStart()
        {
            if (super.canStart() && this.mob.hasPassengers() && !this.mob.isTouchingWater())
            {
                if(this.mob.getFirstPassenger() instanceof LivingEntity) {
                    this.pass = (LivingEntity)this.mob.getFirstPassenger();
                    return true;

                }

            }
            return false;
        }

        @Override
        public void start() {
            Vec3i blockPos = null;
            BlockPos oldPos = null;
            BlockPos newPos = null;
            Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 20.0), MathHelper.floor(this.mob.getY() - 10.0), MathHelper.floor(this.mob.getZ() - 20.0), MathHelper.floor(this.mob.getX() + 20.0), this.mob.getBlockY(), MathHelper.floor(this.mob.getZ() + 20.0));
            for (BlockPos blockPos2 : iterable) {
                if (!this.mob.world.getFluidState(blockPos2).isIn(FluidTags.WATER)) continue;
                blockPos = blockPos2;
                break;
            }
            if (blockPos != null) {
                this.mob.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
            }
        }
    }

    class SlayRiderGoal extends TrackTargetGoal{

        private final KelpieEntity mob;
        private LivingEntity pass;
        private final Class<?>[] noRevengeTypes;

        public SlayRiderGoal(KelpieEntity mob,Class<?> ... noRevengeTypes)
        {
            super(mob,true);
            this.noRevengeTypes = noRevengeTypes;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
            this.mob = mob;
        }
        @Override
        public boolean canStart() {

                if(this.mob.hasPassengers() && this.mob.isTouchingWater())
                {
                    if(this.mob.getFirstPassenger() instanceof LivingEntity) {
                        this.pass = (LivingEntity)this.mob.getFirstPassenger();
                        return true;
                    }

                }

            if(this.mob.lastPass != null && !this.mob.hasPassengers())
            {
                if(this.mob.lastPass instanceof LivingEntity) {
                    this.pass = (LivingEntity)lastPass;
                    return true;
                }

            }

            return false;
        }

        @Override
        public void start()
        {
                this.mob.setTarget(this.pass);
                this.target = this.mob.getTarget();
                this.maxTimeWithoutVisibility = 300;


                super.start();

        }
    }

}

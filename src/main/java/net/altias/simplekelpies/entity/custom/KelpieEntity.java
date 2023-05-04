package net.altias.simplekelpies.entity.custom;

import net.altias.simplekelpies.SimpleKelpies;
import net.altias.simplekelpies.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class KelpieEntity extends AbstractHorseEntity implements Angerable {
    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    private static final int SADDLED_FLAG = 4;

    public Entity lastPass;
    public boolean noWater;

    @Nullable
    private UUID angryAt;

    public KelpieEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 3.0f)
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, 1.1)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new TakeRiderToWaterGoal(this));
        this.goalSelector.add(2, new SlayRiderGoal(this));
       // this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(3, new KelpieRevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new SlayRiderGoal(this, new Class[0]));
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {


        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();

        if(itemStack.isOf(ModItems.GOLDEN_BRIDLE) && !this.isTame())
        {
            this.setOwnerUuid(player.getUuid());
            this.setTame(true);
            this.setHorseFlag(SADDLED_FLAG,true);

            this.world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);

            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            return ActionResult.SUCCESS;
        }

        if (this.isTame() && itemStack.isFood() && (item.getFoodComponent().isMeat() || itemStack.isOf(Items.COD) || itemStack.isOf(Items.SALMON)) && this.getHealth() < this.getMaxHealth()) {

            this.heal(item.getFoodComponent().getHunger());
            this.playEatingAnimation();
            this.emitGameEvent(GameEvent.EAT);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        super.interactMob(player, hand);

        return ActionResult.SUCCESS;
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
    public boolean canBreatheInWater() {
        return true;
    }

    private void playEatingAnimation() {
        SoundEvent soundEvent;
        //this.setEating();
        if (!this.isSilent() && (soundEvent = this.getEatSound()) != null) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }




    @Override
    public void tickMovement()
    {
        super.tickMovement();

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }

        if (this.isWet())
        {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20,2,true,false),this);
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

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }



    }

    @Override
    public boolean hasArmorSlot() {
        return true;
    }

    @Override
    public boolean canBeSaddled() {
        return false;
    }

    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        this.setHorseFlag(SADDLED_FLAG, this.isTame());
    }

    class TakeRiderToWaterGoal extends MoveIntoWaterGoal
    {
        private final KelpieEntity mob;
        private LivingEntity pass;

        public TakeRiderToWaterGoal(KelpieEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public boolean canStart()
        {
            if (super.canStart() && this.mob.hasPassengers() && this.mob.isOnGround() && !this.mob.world.getFluidState(this.mob.getBlockPos()).isIn(FluidTags.WATER) && !this.mob.isTame())
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
                noWater = false;
            }
            else
            {
                noWater = true;
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

            if(this.mob.lastPass instanceof PlayerEntity)
            {
                PlayerEntity p = (PlayerEntity)lastPass;
                if(p.getAbilities().creativeMode)
                {
                    return(false);
                }
            }

                if(this.mob.lastPass!= null && this.mob.isTouchingWater() && !this.mob.isTame())
                {
                    if(this.mob.getFirstPassenger() instanceof LivingEntity) {
                        this.pass = (LivingEntity)this.mob.getFirstPassenger();
                        return true;
                    }

                }

            if(this.mob.lastPass != null && !this.mob.hasPassengers() && !this.mob.isTame())
            {
                if(this.mob.lastPass instanceof LivingEntity) {
                    this.pass = (LivingEntity)lastPass;
                    return true;
                }

            }

            if(this.mob.hasPassengers() && this.mob.noWater && !this.mob.isTame() && this.mob.lastPass != null)
            {
                return true;
            }

            return false;
        }

        @Override
        public void start()
        {
                this.mob.setTarget(this.pass);
                this.target = this.mob.getTarget();
                this.maxTimeWithoutVisibility = 300;

                if (!this.mob.lastPass.isAlive())
                {
                    this.mob.lastPass = null;
                }


                super.start();

        }
    }

    class KelpieRevengeGoal extends RevengeGoal
    {
        private final KelpieEntity mob;
        private int lastAttackedTime;
        private final Class<?>[] noRevengeTypes;
        private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor();

        public KelpieRevengeGoal(KelpieEntity mob, Class<?> ... noRevengeTypes) {
            super(mob, noRevengeTypes);
            this.mob = mob;
            this.noRevengeTypes = noRevengeTypes;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }

        @Override
        public boolean canStart() {
            int i = this.mob.getLastAttackedTime();
            LivingEntity livingEntity = this.mob.getAttacker();

            if (i == this.lastAttackedTime || livingEntity == null) {
                return false;
            }

            if(livingEntity instanceof PlayerEntity && this.mob.isTame())
            {
                return false;
            }
            if (livingEntity.getType() == EntityType.PLAYER && this.mob.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
                return false;
            }
            for (Class<?> class_ : this.noRevengeTypes) {
                if (!class_.isAssignableFrom(livingEntity.getClass())) continue;
                return false;
            }
            return this.canTrack(livingEntity, VALID_AVOIDABLES_PREDICATE);
        }
    }

}

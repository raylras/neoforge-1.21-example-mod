package example.neoed.client.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.mojang.math.Transformation
import example.neoed.Neoed
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.BlockModelRotation
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ModelState
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.client.ClientHooks
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.model.CompositeModel
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel
import net.neoforged.neoforge.client.model.QuadTransformers
import net.neoforged.neoforge.client.model.SimpleModelState
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.function.Function

private val FLUID_TRANSFORM = Transformation(Vector3f(), Quaternionf(), Vector3f(1.0f, 1.0f, 1.002f), Quaternionf())

class InfinityBucketItemModel(private val fluid: Fluid = Fluids.EMPTY) : IUnbakedGeometry<InfinityBucketItemModel> {
    object Loader : IGeometryLoader<InfinityBucketItemModel> {
        override fun read(jsonObject: JsonObject, ctx: JsonDeserializationContext): InfinityBucketItemModel {
            return InfinityBucketItemModel()
        }
    }

    override fun bake(
        context: IGeometryBakingContext,
        baker: ModelBaker,
        spriteGetter: Function<Material, TextureAtlasSprite>,
        modelState: ModelState,
        overrides: ItemOverrides
    ): BakedModel {
        val baseSprite = spriteGetter.getSpriteOrNull(context, "base")
        val fluidSprite = spriteGetter.getSpriteOrNull(fluid)

        // determine particle
        var particleSprite = fluidSprite ?: baseSprite ?: spriteGetter.defaultSprite()

        // We need to disable GUI 3D and block lighting for this to render properly
        val itemContext = StandaloneGeometryBakingContext.builder(context)
            .withGui3d(false)
            .withUseBlockLight(false)
            .build(ResourceLocation.fromNamespaceAndPath(Neoed.ID, "infinity_bucket"))
        val modelBuilder = CompositeModel.Baked.builder(
            itemContext,
            particleSprite,
            ContainedFluidOverrideHandler(baker, itemContext),
            context.transforms
        )

        // Add in the base
        if (baseSprite != null) {
            val renderType = DynamicFluidContainerModel.getLayerRenderTypes(false)
            val unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite)
            val quads = UnbakedGeometryHelper.bakeElements(unbaked, { _ -> baseSprite }, modelState)
            modelBuilder.addQuads(renderType, quads)
        }

        // Add in the fluid
        if (fluidSprite != null) {
            val templateSprite = spriteGetter.getSpriteOrNull(context, "mask") ?: spriteGetter.defaultSprite()

            // Fluid layer
            val transformedState = SimpleModelState(modelState.rotation.compose(FLUID_TRANSFORM), modelState.isUvLocked)
            val unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite) // Use template as mask
            val quads = UnbakedGeometryHelper.bakeElements(
                unbaked,
                { _ -> fluidSprite },
                transformedState
            ) // Bake with fluid texture

            // apply light
            val lightLevel = fluid.fluidType.lightLevel
            val renderTypes = DynamicFluidContainerModel.getLayerRenderTypes(lightLevel > 0)
            if (lightLevel > 0) {
                QuadTransformers.settingEmissivity(lightLevel).processInPlace(quads)
            }

            // apply color
            val color = fluid.fluidType.tintColor
            if (color != -1) {
                QuadTransformers.applyingColor(color).processInPlace(quads)
            }

            modelBuilder.addQuads(renderTypes, quads)
        }

        modelBuilder.setParticle(particleSprite)
        return modelBuilder.build()
    }

    private class ContainedFluidOverrideHandler(
        private val baker: ModelBaker,
        private val owner: IGeometryBakingContext
    ) : ItemOverrides() {
        private val cache: MutableMap<Fluid, BakedModel> = mutableMapOf<Fluid, BakedModel>()

        override fun resolve(
            originalModel: BakedModel,
            stack: ItemStack,
            level: ClientLevel?,
            entity: LivingEntity?,
            seed: Int
        ): BakedModel? {
            return FluidUtil.getFluidContained(stack)
                .map(FluidStack::getFluid)
                .map<BakedModel> { fluid ->
                    cache.computeIfAbsent(fluid) {
                        val unbaked = InfinityBucketItemModel(fluid)
                        val baked = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this)
                        baked
                    }
                }
                .orElse(originalModel)
        }
    }
}

private val FluidType.tintColor: Int
    get() = IClientFluidTypeExtensions.of(this).tintColor

private val FluidType.stillTexture: ResourceLocation
    get() = IClientFluidTypeExtensions.of(this).stillTexture

typealias SpriteGetter = Function<Material, TextureAtlasSprite>

private fun SpriteGetter.getSpriteOrNull(context: IGeometryBakingContext, name: String): TextureAtlasSprite? {
    val material: Material? = if (context.hasMaterial(name)) context.getMaterial(name) else null
    val sprite: TextureAtlasSprite? = if (material != null) this.apply(material) else null
    return sprite
}

private fun SpriteGetter.getSpriteOrNull(fluid: Fluid): TextureAtlasSprite? {
    val material = if (fluid != Fluids.EMPTY) ClientHooks.getBlockMaterial(fluid.fluidType.stillTexture) else null
    val sprite = if (material != null) this.apply(material) else null
    return sprite
}

private fun SpriteGetter.defaultSprite(): TextureAtlasSprite {
    return this.apply(
        Material(
            InventoryMenu.BLOCK_ATLAS,
            MissingTextureAtlasSprite.getLocation()
        )
    )
}

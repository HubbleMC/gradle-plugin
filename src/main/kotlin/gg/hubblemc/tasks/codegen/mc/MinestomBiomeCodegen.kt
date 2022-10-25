package gg.hubblemc.tasks.codegen.mc

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import gg.hubblemc.tasks.codegen.Codegen
import gg.hubblemc.tasks.codegen.fetchMCData
import org.gradle.api.Project
import java.io.File
import javax.lang.model.element.Modifier

// TODO: Cleanup
@Suppress("unused")
object MinestomBiomeCodegen : Codegen() {
    override fun generate(project: Project, folder: File) {
        val data = project.fetchMCData<List<BiomeData>>("biomes")

        val typeClass = ClassName.get("net.minestom.server.world.biomes", "Biome")
        val biomeEffectsClass = ClassName.get("net.minestom.server.world.biomes", "BiomeEffects")
        val namespaceIdClass = ClassName.get("net.minestom.server.utils", "NamespaceID")

        createClassFile("MinecraftBiomes") {
            data.forEach {
                val initializer = CodeBlock.builder()
                    .add("\$T.builder()\n", typeClass)
                    .add("  .category(\$T.${it.category.toUpperCase()})\n", typeClass.nestedClass("Category"))
                    .add("  .name(\$T.from(\"minecraft:${it.name}\"))\n", namespaceIdClass)
                    .add("  .temperature(${it.temperature}F)\n")
                    .add("  .downfall(${it.rainfall}F)\n")
                    .add("  .depth(${it.depth}F)\n")
                    .add("  .effects(\$T.builder()\n", biomeEffectsClass)
                    .add("    .fogColor(0x${"%X".format(it.color)})\n")
                    .add("    .skyColor(0x78A7FF)\n")
                    .add("    .waterColor(0x3F76E4)\n")
                    .add("    .waterFogColor(0x50533)\n")
                    .add("    .build())\n")
                    .add("  .build()")
                    .build()

                val field = FieldSpec.builder(typeClass, it.name.toUpperCase())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(initializer)
                    .build()
                addField(field)
            }

            addLookupFunction(
                ClassName.get("java.lang", "String"),
                typeClass,
                data.associate {
                    "\"minecraft:${it.name}\"" to it.name.toUpperCase()
                }
            )
        }.writeTo(folder)
    }
}

data class BiomeData(
    val id: Int,
    val name: String,
    val category: String,
    val temperature: Double,
    val precipitation: String,
    val depth: Double,
    val dimension: String,
    val displayName: String,
    val color: Int,
    val rainfall: Double
)

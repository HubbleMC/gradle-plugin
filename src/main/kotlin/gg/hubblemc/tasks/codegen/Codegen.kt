/*
 * HubbleMC - Gradle Plugin
 * Copyright (C) 2022  Zerite Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gg.hubblemc.tasks.codegen

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import org.gradle.api.Project
import java.io.File
import javax.lang.model.element.Modifier

abstract class Codegen {

    abstract fun generate(project: Project, folder: File)

    fun createClassFile(name: String, block: TypeSpec.Builder.() -> Unit): JavaFile {
        val typeSpec = TypeSpec.classBuilder(name)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(
                AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\$S", "unused").build()
            )
            .addJavadoc(
                """
                This file was autogenerated.
                DO NOT EDIT.
                """.trimIndent()
            )
            .apply(block)
        return JavaFile.builder("gg.hubblemc.codegen", typeSpec.build()).skipJavaLangImports(true).build()
    }

    /**
     * Creates a lookup function using the given key.
     *
     * @param key The parameter's type.
     * @param type The return type that is being looked up.
     * @param mappings A map where the first element represents
     *        the lookup expression, and the second represents the return value expression.
     */
    fun TypeSpec.Builder.addLookupFunction(key: TypeName, type: TypeName, mappings: Map<String, String>) {
        val method = MethodSpec.methodBuilder("lookup")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(key, "id")
            .returns(type)
            // We can't use controlFlow since it doesn't support enhanced switch
            .addCode("return switch (id) {\n")
            .also {
                mappings.forEach { (key, value) ->
                    it.addCode("  case \$L -> \$L;\n", key, value)
                }
            }
            .addCode("  default -> null;\n") // TODO: Make this customizable
            .addCode("};")
            .build()
        addMethod(method)
    }
}

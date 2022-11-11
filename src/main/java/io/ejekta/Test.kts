package io.ejekta

import io.ejekta.kambrik.gui.reactor.MouseReactor
import net.minecraft.text.Text
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties


val a = Text.literal("Hi!")

println(a::class.companionObject)

println(a::class.staticProperties)

println("======")


println(MouseReactor::class.staticProperties)
println(MouseReactor::class.staticFunctions)
println(MouseReactor::class.companionObject)


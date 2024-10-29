package net.wouterb.blockblock.mixin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.wouterb.blockblock.command.WildcardPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(RegistryEntryPredicateArgumentType.class)
public class RegistryEntryPredicateArgumentTypeMixin<T> {

    @Final
    @Shadow
    private RegistryWrapper<T> registryWrapper;

    @Final
    @Shadow
    RegistryKey<? extends Registry<T>> registryRef;

    @Inject(method = "parse*", at = @At("HEAD"), cancellable = true)
    private void injectParse(StringReader stringReader, CallbackInfoReturnable<RegistryEntryPredicateArgumentType.EntryPredicate<T>> cir) throws CommandSyntaxException {
        String input = stringReader.getString();

        // Parse the identifier from the stringReader
        Identifier identifier = Identifier.fromCommandInput(stringReader);
        RegistryKey<T> key = RegistryKey.of(this.registryRef, identifier);

        // Check if the entry exists in the registry
        Optional<RegistryEntry.Reference<T>> entryReference = this.registryWrapper.getOptional(key);

        // If the input contains wildcards or is invalid, handle accordingly
        if ((input.contains("*") || entryReference.isEmpty()) && input.contains(":")) {
            String remainingInput = stringReader.getRemaining();
            stringReader.setCursor(stringReader.getTotalLength());
            cir.setReturnValue(new WildcardPredicate<>(remainingInput));
        }
    }

    @Inject(method = "getRegistryEntryPredicate", at = @At("HEAD"), cancellable = true)
    private static <T> void getRegistryEntryPredicate(
            CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef, CallbackInfoReturnable<RegistryEntryPredicateArgumentType.EntryPredicate<T>> cir
    ) {
        if (context.getInput().contains("*")) {
            String pattern = extractWordWithStar(context.getInput());
            cir.setReturnValue(new WildcardPredicate<>(pattern));
        }
    }

    @Unique
    private static String extractWordWithStar(String str) {
        Pattern pattern = Pattern.compile("\\S*\\*\\S*");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }
}

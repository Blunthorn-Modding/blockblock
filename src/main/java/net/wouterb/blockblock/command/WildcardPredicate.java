package net.wouterb.blockblock.command;

import com.mojang.datafixers.util.Either;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class WildcardPredicate<T> implements RegistryEntryPredicateArgumentType.EntryPredicate<T> {
    private final String pattern;

    public WildcardPredicate(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Either<RegistryEntry.Reference<T>, RegistryEntryList.Named<T>> getEntry() {
        // This predicate doesn't match a single entry but a pattern, so we don't need this
        return Either.right(null);
    }

    @Override
    public <E> Optional<RegistryEntryPredicateArgumentType.EntryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        // Cast the wildcard to any matching entry
        return Optional.empty();
    }

    @Override
    public boolean test(RegistryEntry<T> registryEntry) {
        return pattern.contains("*");
    }

    @Override
    public String asString() {
        return this.pattern;
    }

}
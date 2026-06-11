/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.workspace.elements;

import net.mcreator.element.ModElementType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Set of mod elements in Workspace. Maintains transient indexes for fast lookup by name and type.
 */
public class ModElementList extends LinkedHashSet<ModElement> {

	private transient final Map<String, ModElement> byName = new HashMap<>();
	private transient final Map<ModElementType<?>, Set<ModElement>> byType = new HashMap<>();

	public ModElementList() {
		super();
	}

	public ModElementList(Collection<? extends ModElement> elements) {
		super(elements);
		for (ModElement element : this)
			addToIndexes(element);
	}

	@Override public boolean add(ModElement element) {
		if (!super.add(element))
			return false;
		addToIndexes(element);
		return true;
	}

	@Override public boolean addAll(@Nonnull Collection<? extends ModElement> elements) {
		boolean modified = false;
		for (ModElement element : elements)
			modified |= add(element);
		return modified;
	}

	@Override public boolean remove(Object element) {
		if (!super.remove(element))
			return false;
		if (element instanceof ModElement modElement)
			removeFromIndexes(modElement);
		return true;
	}

	@Override public boolean removeAll(@Nonnull Collection<?> elements) {
		if (!super.removeAll(elements))
			return false;
		rebuildIndexes();
		return true;
	}

	@Override public boolean retainAll(@Nonnull Collection<?> elements) {
		if (!super.retainAll(elements))
			return false;
		rebuildIndexes();
		return true;
	}

	@Override public void clear() {
		super.clear();
		byName.clear();
		byType.clear();
	}

	@Override public boolean removeIf(@Nonnull Predicate<? super ModElement> filter) {
		if (!super.removeIf(filter))
			return false;
		rebuildIndexes();
		return true;
	}

	@Nonnull @Override public Iterator<ModElement> iterator() {
		final Iterator<ModElement> iterator = super.iterator();
		return new Iterator<>() {
			private ModElement lastReturned;

			@Override public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override public ModElement next() {
				lastReturned = iterator.next();
				return lastReturned;
			}

			@Override public void remove() {
				iterator.remove();
				if (lastReturned != null) {
					removeFromIndexes(lastReturned);
					lastReturned = null;
				}
			}
		};
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod") @Override public ModElementList clone() {
		return new ModElementList(this);
	}

	public boolean containsName(@Nonnull String name) {
		return byName.containsKey(name);
	}

	public boolean containsType(@Nonnull ModElementType<?> type) {
		return byType.containsKey(type);
	}

	@Nullable public ModElement getByName(@Nonnull String name) {
		return byName.get(name);
	}

	@Nonnull public Collection<ModElement> getByType(@Nonnull ModElementType<?> type) {
		Set<ModElement> elements = byType.get(type);
		return elements == null ? Set.of() : Collections.unmodifiableSet(elements);
	}

	private void addToIndexes(ModElement element) {
		byName.put(element.getName(), element);
		byType.computeIfAbsent(element.getType(), _ -> new HashSet<>()).add(element);
	}

	private void removeFromIndexes(ModElement element) {
		byName.remove(element.getName());

		Set<ModElement> typeElements = byType.get(element.getType());
		if (typeElements == null)
			return;

		typeElements.remove(element);
		if (typeElements.isEmpty())
			byType.remove(element.getType());
	}

	private void rebuildIndexes() {
		byName.clear();
		byType.clear();
		for (ModElement element : this)
			addToIndexes(element);
	}

}

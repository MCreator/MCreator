/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.util;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.*;

public class ArrayListListModel<T> extends AbstractListModel<T> implements List<T> {
	private final ArrayList<T> list = new ArrayList<>();

	@Override public int size() {
		return list.size();
	}

	@Override public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override public boolean contains(Object o) {
		return list.contains(o);
	}

	@Nonnull @Override public Iterator<T> iterator() {
		return list.iterator();
	}

	@Nonnull @Override public Object[] toArray() {
		return list.toArray();
	}

	@Nonnull @Override @SuppressWarnings("unchecked") public <Type> Type[] toArray(@Nonnull Type[] a) {
		return (Type[]) list.toArray();
	}

	@Override public boolean add(T t) {
		boolean add = list.add(t);
		int location = list.indexOf(t);
		fireIntervalAdded(this, location, location);
		return add;
	}

	@Override public boolean remove(Object o) {
		int location = list.indexOf(o);
		boolean remove = list.remove(o);
		fireIntervalRemoved(this, location, location);
		return remove;
	}

	@Override public boolean containsAll(@Nonnull Collection<?> c) {
		return list.containsAll(c);
	}

	@Override public boolean addAll(@Nonnull Collection<? extends T> c) {
		int start = list.size();
		boolean addAll = list.addAll(c);
		fireIntervalAdded(this, start, start + c.size());
		return addAll;
	}

	@Override public boolean addAll(int index, @Nonnull Collection<? extends T> c) {
		boolean addAll = list.addAll(index, c);
		fireIntervalAdded(this, index, index + c.size());
		return addAll;
	}

	@Override public boolean removeAll(@Nonnull Collection<?> c) {
		int lastIndex = list.size() - 1;
		boolean removeAll = list.removeAll(c);
		fireIntervalRemoved(this, 0, Math.max(0, lastIndex));
		return removeAll;
	}

	@Override public boolean retainAll(@Nonnull Collection<?> c) {
		boolean retainAll = list.retainAll(c);
		fireIntervalRemoved(this, 0, Math.max(0, list.size() - 1));
		return retainAll;
	}

	@Override public void clear() {
		int lastIndex = list.size() - 1;
		list.clear();
		fireIntervalRemoved(this, 0, Math.max(0, lastIndex));
	}

	@Override public T get(int index) {
		return list.get(index);
	}

	@Override public T set(int index, T element) {
		T set = list.set(index, element);
		fireContentsChanged(this, index, index);
		return set;
	}

	@Override public void add(int index, T element) {
		list.add(index, element);
		fireIntervalAdded(this, index, index);
	}

	@Override public T remove(int index) {
		T removed = list.remove(index);
		fireIntervalRemoved(this, index, index);
		return removed;
	}

	@Override public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Nonnull @Override public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	@Nonnull @Override public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	@Nonnull @Override public List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override public int getSize() {
		return list.size();
	}

	@Override public T getElementAt(int index) {
		return list.get(index);
	}

	protected void switchElements(int index0, int index1) {
		T bottom = get(index0);
		set(index0, get(index1));
		set(index1, bottom);
	}

	public boolean moveUp(int index) {
		if (index > 0) {
			switchElements(index, index - 1);
			return true;
		}
		return false;
	}

	public boolean moveDown(int index) {
		if (index < size() - 1) {
			switchElements(index, index + 1);
			return true;
		}
		return false;
	}
}

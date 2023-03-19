/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.component.filebrowser;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public record SynchronousJFXDirectoryChooser(Stage parent, Supplier<DirectoryChooser> DirectoryChooserFactory) {
	/**
	 * Constructs a new file chooser that will use the provided factory.
	 * <p>
	 * The factory is accessed from the JavaFX event thread, so it should either
	 * be immutable or at least its state shouldn't be changed randomly while
	 * one of the dialog-showing method calls is in progress.
	 * <p>
	 * The factory should create and set up the chooser, for example,
	 * by setting extension filters. If there is no need to perform custom
	 * initialization of the chooser, DirectoryChooser::new could be passed as
	 * a factory.
	 * <p>
	 * Alternatively, the method parameter supplied to the showDialog()
	 * function can be used to provide custom initialization.
	 *
	 * @param DirectoryChooserFactory the function used to construct new choosers
	 */
	public SynchronousJFXDirectoryChooser {
	}

	/**
	 * Shows the DirectoryChooser dialog by calling the provided method.
	 * <p>
	 * Waits for one second for the dialog-showing task to start in the JavaFX
	 * event thread, then throws an IllegalStateException if it didn't start.
	 *
	 * @param <T>    the return type of the method, usually File or List&lt;File&gt;
	 * @param method a function calling one of the dialog-showing methods
	 * @return whatever the method returns
	 * @see #showDialog(Function, long, TimeUnit)
	 */
	public <T> T showDialog(Function<DirectoryChooser, T> method) {
		return showDialog(method, 1, TimeUnit.SECONDS);
	}

	/**
	 * Shows the DirectoryChooser dialog by calling the provided method. The dialog
	 * is created by the factory supplied to the constructor, then it is shown
	 * by calling the provided method on it, then the result is returned.
	 * <p>
	 * Everything happens in the right threads thanks to
	 * {@link SynchronousJFXCaller}. The task performed in the JavaFX thread
	 * consists of two steps: construct a chooser using the provided factory
	 * and invoke the provided method on it. Any exception thrown during these
	 * steps will be rethrown in the calling thread, which shouldn't
	 * normally happen unless the factory throws an unchecked exception.
	 * </p>
	 * <p>
	 * If the calling thread is interrupted during either the wait for
	 * the task to start or for its result, then null is returned and
	 * the Thread interrupted status is set.
	 * </p>
	 *
	 * @param <T>     return type (usually File or List&lt;File&gt;)
	 * @param method  a function that calls the desired DirectoryChooser method
	 * @param timeout time to wait for Platform.runLater() to <em>start</em>
	 *                the dialog-showing task (once started, it is allowed to run as long
	 *                as needed)
	 * @param unit    the time unit of the timeout argument
	 * @return whatever the method returns
	 * @throws IllegalStateException if Platform.runLater() fails to start
	 *                               the dialog-showing task within the given timeout
	 */
	public <T> T showDialog(Function<DirectoryChooser, T> method, long timeout, TimeUnit unit) {
		Callable<T> task = () -> {
			DirectoryChooser chooser = DirectoryChooserFactory.get();
			return method.apply(chooser);
		};
		SynchronousJFXCaller<T> caller = new SynchronousJFXCaller<>(task);
		try {
			return caller.call(timeout, unit);
		} catch (RuntimeException | Error ex) {
			throw ex;
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return null;
		} catch (Exception ex) {
			throw new AssertionError("Got unexpected checked exception from" + " SynchronousJFXCaller.call()", ex);
		}
	}

	/**
	 * Shows a DirectoryChooser using DirectoryChooser.showOpenDialog().
	 *
	 * @return the return value of DirectoryChooser.showOpenDialog()
	 * @see #showDialog(Function, long, TimeUnit)
	 */
	public File showDialog() {
		return showDialog(chooser -> chooser.showDialog(parent));
	}

}
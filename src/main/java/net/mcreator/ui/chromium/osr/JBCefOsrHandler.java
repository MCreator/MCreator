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

package net.mcreator.ui.chromium.osr;

import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefDragData;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.cef.misc.CefRange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class JBCefOsrHandler implements CefRenderHandler {

	private final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private final @Nonnull JComponent myComponent;
	private final @Nonnull Function<? super JComponent, ? extends Rectangle> myScreenBoundsProvider;

	protected volatile @Nullable JBHiDPIScaledImage myImage;
	protected volatile @Nullable JBHiDPIScaledImage myPopupImage;

	private volatile boolean myPopupShown = false;
	private volatile @Nonnull Rectangle myPopupBounds = new Rectangle();
	protected final Object myPopupMutex = new Object();

	private volatile @Nullable VolatileImage myVolatileImage;
	protected volatile boolean myContentOutdated = false;
	private volatile @Nullable JBCefCaretListener myCaretListener;

	// DIP (device independent pixel aka logic pixel) size in screen pixels. Expected to be != 1 only if JRE supports HDPI
	private volatile double myPixelDensity = 1;
	private volatile double myScaleFactor = 1;

	private final @Nonnull AtomicReference<Point> myLocationOnScreenRef = new AtomicReference<>(new Point());

	public JBCefOsrHandler(@Nonnull JComponent wrapper) {
		myComponent = wrapper;
		myScreenBoundsProvider = component -> {
			if (component != null && !GraphicsEnvironment.isHeadless()) {
				try {
					return component.isShowing() ?
							component.getGraphicsConfiguration().getDevice().getDefaultConfiguration().getBounds() :
							GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
									.getDefaultConfiguration().getBounds();
				} catch (Exception ignored) {
				}
			}

			return new Rectangle(0, 0, 0, 0);
		};
	}

	@Override public void onPopupShow(CefBrowser browser, boolean show) {
		synchronized (myPopupMutex) {
			myPopupShown = show;
		}
	}

	@Override public void onPopupSize(CefBrowser browser, Rectangle size) {
		synchronized (myPopupMutex) {
			myPopupBounds = size;
		}
	}

	@Override
	public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width,
			int height) {
		JBHiDPIScaledImage image = popup ? myPopupImage : myImage;

		Dimension size = getRealImageSize(image);
		if (size.width != width || size.height != height) {
			image = JBHiDPIScaledImage.createFrom(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE),
					getPixelDensity(), null);
		}

		if (popup) {
			synchronized (myPopupMutex) {
				drawByteBuffer(Objects.requireNonNull(image), buffer, dirtyRects);
				myPopupImage = image;
			}
		} else {
			drawByteBuffer(Objects.requireNonNull(image), buffer, dirtyRects);
			myImage = image;
		}

		myContentOutdated = true;
		SwingUtilities.invokeLater(() -> {
			if (!browser.getUIComponent().isShowing())
				return;
			Component component = browser.getUIComponent();
			JRootPane root = SwingUtilities.getRootPane(component);
			RepaintManager rm = RepaintManager.currentManager(root);
			Rectangle dirtySrc = new Rectangle(0, 0, component.getWidth(), component.getHeight());
			Rectangle dirtyDst = SwingUtilities.convertRectangle(component, dirtySrc, root);
			int dx = 1;
			// NOTE: should mark area outside browser (otherwise the background component won't be repainted)
			rm.addDirtyRegion(root, dirtyDst.x - dx, dirtyDst.y - dx, dirtyDst.width + dx * 2,
					dirtyDst.height + dx * 2);
		});
	}

	protected Dimension getCurrentFrameSize() {
		JBHiDPIScaledImage image = myImage;
		return image == null ? null : new Dimension(image.getWidth(), image.getHeight());
	}

	public void paint(Graphics2D g) {
		Dimension frameSize = getCurrentFrameSize();
		if (frameSize == null)
			return;

		VolatileImage vi = myVolatileImage;

		do {
			boolean contentOutdated = myContentOutdated;
			myContentOutdated = false;
			if (vi == null || vi.getWidth() != frameSize.width || vi.getHeight() != frameSize.height) {
				vi = createVolatileImage(g, frameSize.width, frameSize.height);
			} else if (contentOutdated) {
				drawVolatileImage(vi);
			}

			switch (vi.validate(g.getDeviceConfiguration())) {
			case VolatileImage.IMAGE_RESTORED -> drawVolatileImage(vi);
			case VolatileImage.IMAGE_INCOMPATIBLE -> vi = createVolatileImage(g, frameSize.width, frameSize.height);
			}

			g.drawImage(vi, 0, 0, null);
		} while (vi.contentsLost());

		myVolatileImage = vi;

		if (myPopupShown) {
			synchronized (myPopupMutex) {
				JBHiDPIScaledImage popupImage = myPopupImage;
				if (myPopupShown && popupImage != null) {
					JBHiDPIScaledImage.drawImage(g, popupImage, myPopupBounds.x, myPopupBounds.y, null);
				}
			}
		}
	}

	public void setScreenInfo(double pixelDensity, double scaleFactor) {
		myPixelDensity = pixelDensity;
		myScaleFactor = scaleFactor;
	}

	protected double getPixelDensity() {return myPixelDensity;}

	protected double getScaleFactor() {return myScaleFactor;}

	@Override public Rectangle getViewRect(CefBrowser browser) {
		Component component = browser.getUIComponent();
		double scale = getScaleFactor();
		double value = component.getWidth() / scale;
		double value1 = component.getHeight() / scale;
		return new Rectangle(0, 0, (int) Math.ceil(value), (int) Math.ceil(value1));
	}

	@Override public boolean getScreenInfo(CefBrowser browser, CefScreenInfo screenInfo) {
		Rectangle rect = myScreenBoundsProvider.apply(myComponent);
		double scale = myScaleFactor * myPixelDensity;
		screenInfo.Set(scale, 32, 4, false, rect, rect);
		return true;
	}

	@Override public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
		Point pt = viewPoint.getLocation();
		Point loc = myLocationOnScreenRef.get();
		if (OS.isMacintosh()) {
			Rectangle rect = myScreenBoundsProvider.apply(myComponent);
			pt.setLocation(loc.x + pt.x, rect.height - loc.y - pt.y);
		} else {
			pt.translate(loc.x, loc.y);
		}
		return OS.isMacintosh() ? pt : toRealCoordinates(pt);
	}

	@Override public double getDeviceScaleFactor(CefBrowser browser) {
		return myScaleFactor * myPixelDensity;
	}

	@SuppressWarnings("MagicConstant") @Override public boolean onCursorChange(CefBrowser browser, int cursorType) {
		SwingUtilities.invokeLater(() -> browser.getUIComponent().setCursor(new Cursor(cursorType)));
		return true;
	}

	@Override public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
		return false;
	}

	@Override public void updateDragCursor(CefBrowser browser, int operation) {
	}

	@Override
	public void OnImeCompositionRangeChanged(CefBrowser browser, CefRange selectionRange, Rectangle[] characterBounds) {
		JBCefCaretListener listener = myCaretListener;
		if (listener != null) {
			listener.onImeCompositionRangeChanged(selectionRange, characterBounds);
		}
	}

	@Override public void OnTextSelectionChanged(CefBrowser browser, String selectedText, CefRange selectionRange) {
		JBCefCaretListener listener = myCaretListener;
		if (listener != null) {
			listener.onTextSelectionChanged(selectedText, selectionRange);
		}
	}

	public void setLocationOnScreen(Point location) {
		myLocationOnScreenRef.set(location);
	}

	private @Nonnull Point toRealCoordinates(@Nonnull Point pt) {
		double scale = getPixelDensity();
		return new Point((int) Math.round(pt.x * scale), (int) Math.round(pt.y * scale));
	}

	void addCaretListener(JBCefCaretListener listener) {
		myCaretListener = listener;
	}

	private static @Nonnull Dimension getRealImageSize(JBHiDPIScaledImage image) {
		if (image == null)
			return new Dimension(0, 0);
		BufferedImage bi = (BufferedImage) image.getDelegate();
		return new Dimension(bi.getWidth(), bi.getHeight());
	}

	private static void drawByteBuffer(@Nonnull JBHiDPIScaledImage dst, @Nonnull ByteBuffer src,
			Rectangle[] rectangles) {
		BufferedImage image = (BufferedImage) dst.getDelegate();
		int[] dstData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		IntBuffer srcData = src.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
		for (Rectangle rect : rectangles) {
			if (rect.width < image.getWidth()) {
				for (int line = rect.y; line < rect.y + rect.height; line++) {
					int offset = line * image.getWidth() + rect.x;
					srcData.position(offset).get(dstData, offset, Math.min(rect.width, src.capacity() - offset));
				}
			} else { // optimized for a buffer wide dirty rect
				int offset = rect.y * image.getWidth();
				srcData.position(offset)
						.get(dstData, offset, Math.min(rect.height * image.getWidth(), src.capacity() - offset));
			}
		}
	}

	protected void drawVolatileImage(VolatileImage vi) {
		JBHiDPIScaledImage image = myImage;

		// Draw the buffered image into VolatileImage
		Graphics2D g = (Graphics2D) vi.getGraphics().create();
		try {
			g.setBackground(TRANSPARENT);
			g.setComposite(AlphaComposite.Src);
			g.clearRect(0, 0, vi.getWidth(), vi.getHeight());

			if (image != null) {
				JBHiDPIScaledImage.drawImage(g, image, 0, 0, null);
			}
		} finally {
			g.dispose();
		}
	}

	private VolatileImage createVolatileImage(Graphics2D g, int width, int height) {
		VolatileImage image = g.getDeviceConfiguration()
				.createCompatibleVolatileImage(width, height, Transparency.TRANSLUCENT);

		Graphics2D gimg = (Graphics2D) image.getGraphics().create();
		gimg.setBackground(TRANSPARENT);
		gimg.setComposite(AlphaComposite.Src);
		gimg.clearRect(0, 0, image.getWidth(), image.getHeight());
		gimg.dispose();

		drawVolatileImage(image);
		return image;
	}

}

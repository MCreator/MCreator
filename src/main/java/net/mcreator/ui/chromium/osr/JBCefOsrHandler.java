// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Modifications by Pylo and opensource contributors

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class JBCefOsrHandler implements CefRenderHandler {

	private final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private final @Nonnull JComponent myComponent;
	private final @Nonnull Function<? super JComponent, ? extends Rectangle> myScreenBoundsProvider;

	protected volatile @Nullable JBHiDPIScaledImage myImage;
	protected @Nullable JBHiDPIScaledImage myBackImage;
	protected final Object myImageMutex = new Object();
	private final List<Rectangle> myTextureDirtyRects = new ArrayList<>();

	protected volatile @Nullable JBHiDPIScaledImage myPopupImage;
	private volatile boolean myPopupShown = false;
	private volatile @Nonnull Rectangle myPopupBounds = new Rectangle();
	protected final Object myPopupMutex = new Object();

	private volatile @Nullable VolatileImage myVolatileImage;
	protected volatile boolean myContentOutdated = false;
	private volatile @Nullable JBCefCaretListener myCaretListener;

	private volatile double myPixelDensity = 1;

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
		// SAFE SNAPSHOT: CEF mutates the dirtyRects array constantly.
		// We must clone them before passing them to other threads.
		Rectangle[] safeRects = new Rectangle[dirtyRects.length];
		for (int i = 0; i < dirtyRects.length; i++) {
			safeRects[i] = new Rectangle(dirtyRects[i]);
		}

		if (popup) {
			JBHiDPIScaledImage image = myPopupImage;
			Dimension size = getRealImageSize(image);
			if (image == null || size.width != width || size.height != height) {
				image = JBHiDPIScaledImage.createFrom(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE),
						getPixelDensity(), null);
			}

			synchronized (myPopupMutex) {
				drawByteBuffer(image, buffer, safeRects);
				myPopupImage = image;
			}
		} else {
			// 1. Write to Back Buffer (SLOW native read, NO LOCK so Swing doesn't freeze)
			Dimension backSize = getRealImageSize(myBackImage);
			if (myBackImage == null || backSize.width != width || backSize.height != height) {
				myBackImage = JBHiDPIScaledImage.createFrom(
						new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE), getPixelDensity(), null);
			}
			drawByteBuffer(myBackImage, buffer, safeRects);

			// 2. Safely sync targeted patches to Front Buffer
			synchronized (myImageMutex) {
				JBHiDPIScaledImage front = myImage;
				Dimension frontSize = getRealImageSize(front);

				if (frontSize.width != width || frontSize.height != height) {
					// If resized, create new front buffer and do a one-time full copy
					front = JBHiDPIScaledImage.createFrom(
							new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE), getPixelDensity(), null);
					myImage = front;

					int[] backData = ((DataBufferInt) ((BufferedImage) myBackImage.getDelegate()).getRaster()
							.getDataBuffer()).getData();
					int[] frontData = ((DataBufferInt) ((BufferedImage) front.getDelegate()).getRaster()
							.getDataBuffer()).getData();
					System.arraycopy(backData, 0, frontData, 0, backData.length);

					myTextureDirtyRects.clear();
					myContentOutdated = true; // Flag for a full VRAM clear and upload
				} else if (front != null) {
					// Targeted Dirty Rect Copy! Only copies what changed.
					copyRectangles(myBackImage, front, safeRects, width);
					Collections.addAll(myTextureDirtyRects, safeRects);
				}
			}
		}

		SwingUtilities.invokeLater(() -> {
			if (!browser.getUIComponent().isShowing())
				return;
			Component component = browser.getUIComponent();
			JRootPane root = SwingUtilities.getRootPane(component);
			if (root == null)
				return;
			RepaintManager rm = RepaintManager.currentManager(root);
			int dx = 4; // we mark area outside browser (otherwise the background component won't be repainted)

			if (popup) {
				Rectangle dirtySrc = new Rectangle(myPopupBounds.x, myPopupBounds.y, myPopupBounds.width,
						myPopupBounds.height);
				Rectangle dirtyDst = SwingUtilities.convertRectangle(component, dirtySrc, root);
				rm.addDirtyRegion(root, dirtyDst.x - dx, dirtyDst.y - dx, dirtyDst.width + dx * 2,
						dirtyDst.height + dx * 2);
			} else {
				double scale = getPixelDensity();
				// Iterate over the SAFE array we cloned
				for (Rectangle rect : safeRects) {
					Rectangle dirtySrc = toLogicalRectangle(rect, scale);
					Rectangle dirtyDst = SwingUtilities.convertRectangle(component, dirtySrc, root);
					rm.addDirtyRegion(root, dirtyDst.x - dx, dirtyDst.y - dx, dirtyDst.width + dx * 2,
							dirtyDst.height + dx * 2);
				}
			}
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

		List<Rectangle> rectsToUpdate = null;
		boolean fullUpdate = false;

		// Extract pending VRAM updates
		synchronized (myImageMutex) {
			if (myContentOutdated) {
				fullUpdate = true;
				myContentOutdated = false;
				myTextureDirtyRects.clear();
			} else if (!myTextureDirtyRects.isEmpty()) {
				rectsToUpdate = new ArrayList<>(myTextureDirtyRects);
				myTextureDirtyRects.clear();
			}
		}

		do {
			if (vi == null || vi.getWidth() != frameSize.width || vi.getHeight() != frameSize.height) {
				vi = createVolatileImage(g, frameSize.width, frameSize.height); // create implicitly calls full draw
			} else if (fullUpdate) {
				drawVolatileImage(vi, null);
			} else if (rectsToUpdate != null) {
				drawVolatileImage(vi, rectsToUpdate);
			}

			switch (vi.validate(g.getDeviceConfiguration())) {
			case VolatileImage.IMAGE_RESTORED -> drawVolatileImage(vi, null);
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

	public void setPixelDensity(double pixelDensity) {
		myPixelDensity = pixelDensity;
	}

	protected double getPixelDensity() {
		return myPixelDensity;
	}

	@Override public Rectangle getViewRect(CefBrowser browser) {
		Component component = browser.getUIComponent();
		double value = component.getWidth();
		double value1 = component.getHeight();
		return new Rectangle(0, 0, (int) Math.ceil(value), (int) Math.ceil(value1));
	}

	@Override public boolean getScreenInfo(CefBrowser browser, CefScreenInfo screenInfo) {
		Rectangle rect = myScreenBoundsProvider.apply(myComponent);
		screenInfo.Set(myPixelDensity, 32, 4, false, rect, rect);
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
		return myPixelDensity;
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

	private static void copyRectangles(@Nonnull JBHiDPIScaledImage src, @Nonnull JBHiDPIScaledImage dst,
			Rectangle[] rectangles, int width) {
		BufferedImage srcImage = (BufferedImage) src.getDelegate();
		BufferedImage dstImage = (BufferedImage) dst.getDelegate();
		int[] srcData = ((DataBufferInt) srcImage.getRaster().getDataBuffer()).getData();
		int[] dstData = ((DataBufferInt) dstImage.getRaster().getDataBuffer()).getData();

		for (Rectangle rect : rectangles) {
			if (rect.width == width) {
				// Optimize if the dirty rect spans the whole width
				int offset = rect.y * width;
				System.arraycopy(srcData, offset, dstData, offset, rect.height * width);
			} else {
				// Copy line by line for targeted partial rects
				for (int line = rect.y; line < rect.y + rect.height; line++) {
					int offset = line * width + rect.x;
					System.arraycopy(srcData, offset, dstData, offset, rect.width);
				}
			}
		}
	}

	private Rectangle toLogicalRectangle(Rectangle rect, double scale) {
		int logicalX = (int) Math.floor(rect.x / scale);
		int logicalY = (int) Math.floor(rect.y / scale);
		int logicalW = (int) Math.ceil((rect.x + rect.width) / scale) - logicalX;
		int logicalH = (int) Math.ceil((rect.y + rect.height) / scale) - logicalY;
		return new Rectangle(logicalX, logicalY, logicalW, logicalH);
	}

	protected void drawVolatileImage(VolatileImage vi, @Nullable List<Rectangle> dirtyRects) {
		// Draw the buffered image into VolatileImage
		Graphics2D g = (Graphics2D) vi.getGraphics().create();
		try {
			g.setBackground(TRANSPARENT);
			g.setComposite(AlphaComposite.Src);

			synchronized (myImageMutex) {
				JBHiDPIScaledImage image = myImage;
				if (image == null)
					return;

				if (dirtyRects == null || dirtyRects.isEmpty()) {
					// Full redraw
					g.clearRect(0, 0, vi.getWidth(), vi.getHeight());
					JBHiDPIScaledImage.drawImage(g, image, 0, 0, null);
				} else {
					// Targeted redraw using clips to prevent massive CPU-to-GPU uploads
					double scale = getPixelDensity();
					int pad = 1; // 1-pixel padding catches sub-pixel bleeding
					for (Rectangle rect : dirtyRects) {
						Rectangle logicalRect = toLogicalRectangle(rect, scale);

						// Apply clip with safety padding
						g.setClip(logicalRect.x - pad, logicalRect.y - pad, logicalRect.width + pad * 2,
								logicalRect.height + pad * 2);
						g.clearRect(logicalRect.x - pad, logicalRect.y - pad, logicalRect.width + pad * 2,
								logicalRect.height + pad * 2);
						JBHiDPIScaledImage.drawImage(g, image, 0, 0, null);
					}
				}
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

		drawVolatileImage(image, null);
		return image;
	}

}

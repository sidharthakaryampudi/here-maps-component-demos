package com.nokia.maps.gui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.gui.item.GUIData;
import com.nokia.maps.map.Point;

/**
 * 
 * Graphical rendering of a Custom Map Component which displays a sidebar
 * holding a series of menu options. Unlike the IconCommand Bar it is expected
 * that these items will fire a series of actions. The control can be dragged
 * onto the screen and hidden to maximise the map displayed.
 * 
 */
public class SideBarRenderer extends GUIItemRenderer {

	private static final int SIDE_BAR_WIDTH = 40;

	private final Image handle;
	private Point handleAt = new Point(0, 0);
	private Point touchedAt;
	private boolean visible;
	private final SideBar sidebar;
	private final int textColor = 0xFFFFFF;

	/**
	 * Default constructor
	 * 
	 * @param handle
	 *            - The Image for the handle of the control.
	 */
	public SideBarRenderer(Image handle) {
		super();
		this.handle = handle;
		sidebar = new SideBar();
		this.setGUIBackground(new BackgroundBox(2, 0,
				TextButtonRenderer.MID_GREY, TextButtonRenderer.MID_GREY,
				TextButtonRenderer.DARK_GREY, TextButtonRenderer.DARK_GREY));
		this.setGUIItem(sidebar);
		setAnchor(-SIDE_BAR_WIDTH, 0);

	}

	/**
	 * The paint method draws the handle in addition to the GUI control,
	 * background etc.
	 */
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(handle, handleAt.getX(), handleAt.getY(), Graphics.TOP
				| Graphics.LEFT);
	}

	/**
	 * Setting the preferred dimensions set the position of the handle on
	 * screen.
	 * 
	 * @param maxWidth
	 * @param maxHeight
	 */
	public void setPreferredDimensions(int maxWidth, int maxHeight) {
		handleAt.setY((maxHeight - handle.getHeight()) / 2);
		sidebar.setHeight(maxHeight);
	}

	/**
	 * Whether the hit area of the handle has been touched. This is slightly
	 * larger than the handle Image itself, since the control is desigedn to be
	 * dragged.
	 * 
	 * @param x
	 * @param y
	 * @return <code>true</code> if the handle has been touched
	 *         <code>false</code> otherwise.
	 */
	public boolean isHandleTouched(int x, int y) {

		return ((Math.abs(x - handleAt.getX()) < 30) && y > handleAt.getY() && y < handleAt
				.getY() + handle.getHeight());

	}

	/**
	 * Whether the hit area of the sidebar has been touched
	 * 
	 * @param x
	 * @param y
	 * @return <code>true</code> if the sidebar has been touched
	 *         <code>false</code> otherwise.
	 */
	public boolean isSidebarTouched(int x, int y) {
		return visible & x < SIDE_BAR_WIDTH;
	}

	/**
	 * Returns the selected Sidebar Action item.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int touchAt(int x, int y) {
		touchedAt = new Point(x, y);
		if (visible) {
			return sidebar.touchAt(touchedAt);
		} else {
			return -1;
		}
	}

	/**
	 * Drags the Side bar if the offset is larger than the threshold.
	 * 
	 * @param x
	 * @param y
	 */
	public void draggedTo(int x, int y) {
		Point draggedTo = new Point(x, y);
		draggedTo.translate(-touchedAt.getX(), 0);

		// Set the visibility of the control if moved more than 5 pixels.
		if (Math.abs(draggedTo.getX()) > 5) {
			setVisible(draggedTo.getX() > 0);
		}
	}

	/**
	 * Sets the Icons for Actions which are not selected.
	 * 
	 * @param icons
	 */
	public void setUnselected(Image[] icons) {
		sidebar.setImages(icons);
	}

	/**
	 * Sets the Icons for Actions which are selected.
	 * 
	 * @param icons
	 */
	public void setSelected(Image[] icons) {
		sidebar.setHightlightImages(icons);
	}

	/**
	 * Sets an optional label for each Actions.
	 * 
	 * @param labels
	 */
	public void setLabels(String[] labels) {
		sidebar.setLabels(labels);
	}

	/**
	 * Whether the Sidebar control is open.
	 * 
	 * @return <code>true</code> if the control is open. <code>false</code>
	 *         otherwise.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the sidebar open or closed.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			handleAt.setX(SIDE_BAR_WIDTH);
			setAnchor(0, 0);

		} else {
			handleAt.setX(0);
			setAnchor(-SIDE_BAR_WIDTH, 0);
		}
	}

	/**
	 * 
	 * Class for graphically displaying the items within the SideBar.
	 * 
	 */
	private class SideBar implements GUIData {

		private int height;
		private Image[] images = {};
		private Image[] highlightImages = {};
		private String[] labels = {};
		private int selectedIndex = -1;

		public void paint(Graphics g, int x, int y, int anchor) {

			g.setColor(textColor);

			y = y + 10;
			for (int i = 0; i < images.length; i++) {
				g.drawImage(
						(i == selectedIndex && isHighlight()) ? getHighlightImage(i)
								: getImage(i), x + 4, y, Graphics.TOP
								| Graphics.LEFT);

				y = y + getImage(i).getHeight();

				if (getLabel(i) != null) {
					g.setFont(GUIItemRenderer.SMALL_FONT);
					g.drawString(getLabel(i), x + 2, y, Graphics.TOP
							| Graphics.LEFT);
					y = y + GUIItemRenderer.SMALL_FONT.getHeight();
				}

				y = y + 10;
			}
		}

		/**
		 * The display is a fixed width
		 */
		public int getWidth() {
			return SIDE_BAR_WIDTH;
		}

		/**
		 * Retrieves the height of the control. The height depends on the Orientation.
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * Sets the height of the control.
		 * @param height
		 *
		 */
		public void setHeight(int height) {
			this.height = height;
		}

		/**
		 * 
		 * @param index
		 * @return the Image associated with the nth Action
		 */
		public Image getImage(int index) {
			return images[index];
		}

		/**
		 * 
		 * @param icons  Images for each Action
		 */
		public void setImages(Image[] icons) {
			this.images = icons;
		}

		/**
		 * 
		 * @param index
		 * @return The label for the nth Action.
		 */
		public String getLabel(int index) {
			return labels == null || index >= labels.length ? null
					: labels[index];
		}

		/**
		 * 
		 * @param labels labels for each action.
		 */
		public void setLabels(String[] labels) {
			this.labels = labels;
		}

		/**
		 * 
		 * @param index
		 * @return the Image for the nth Action whilst highlighted.
		 */
		public Image getHighlightImage(int index) {
			return highlightImages == null || index >= highlightImages.length
					|| highlightImages[index] == null ? getImage(index)
					: highlightImages[index];
		}

		/**
		 * 
		 * @param icons  Images for each Action whilst highlighted.
		 */
		public void setHightlightImages(Image[] icons) {
			this.highlightImages = icons;
		}

		/**
		 * 
		 * @param point
		 * @return Which action (if any has been touched)
		 */
		public int touchAt(Point point) {
			selectedIndex = -1; // DEFAULT is not selected.
			int y = point.getY();

			if (y > 0 & point.getX() < SIDE_BAR_WIDTH) {
				for (int i = 0; i < images.length; i++) {
					y = y - getImage(i).getHeight() - 10;

					if (getLabel(i) != null) {
						y = y - GUIItemRenderer.SMALL_FONT.getHeight();
					}

					if (y < 0) {
						selectedIndex = i;
						break;
					}
				}
			}
			return selectedIndex;
		}

	}

}
